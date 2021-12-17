import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;


public class cache {

	static int associativity = 2;          // Associativity of cache
	static int blocksize_bytes = 32;       // Cache Block size in bytes
	static int cachesize_kb = 64;          // Cache size in KB
	static int miss_penalty = 30;

	public static void print_usage()
	{
	  System.out.println("Usage: gunzip2 -c <tracefile> | java cache -a assoc -l blksz -s size -mp mispen\n");
	  System.out.println("  tracefile : The memory trace file\n");
	  System.out.println("  -a assoc : The associativity of the cache\n");
	  System.out.println("  -l blksz : The blocksize (in bytes) of the cache\n");
	  System.out.println("  -s size : The size (in KB) of the cache\n");
	  System.out.println("  -mp mispen: The miss penalty (in cycles) of a miss\n");
	  System.exit(0);
	}

	public static Queue<Integer> put(Queue<Integer> LRU, int hit) {
		int current = LRU.remove();
		int first = current;
		while (current != hit) {
			LRU.add(current);
			current = LRU.remove();
		}
		current = LRU.peek();
		if (first == hit) {
			first = current;
		}
		while (current != first) {
			LRU.remove();
			LRU.add(current);
			current = LRU.peek();
		}
		LRU.add(hit);
		return LRU;
	}

	public static void main(String[] args) {
		  long address;
		  int loadstore, icount;

		  int i = 0;
		  int j = 0;
		  // Process the command line arguments
		 // Process the command line arguments
		  while (j < args.length) {
		    if (args[j].equals("-a")) {
		      j++;
		      if (j >= args.length)
		        print_usage();
		      associativity = Integer.parseInt(args[j]);
		      j++;
		    } else if (args[j].equals("-l")) {
		      j++;
		      if (j >= args.length)
		        print_usage ();
		      blocksize_bytes = Integer.parseInt(args[j]);
		      j++;
		    } else if (args[j].equals("-s")) {
		      j++;
		      if (j >= args.length)
		        print_usage ();
		      cachesize_kb = Integer.parseInt(args[j]);
		      j++;
		    } else if (args[j].equals("-mp")) {
		      j++;
		      if (j >= args.length)
		        print_usage ();
		      miss_penalty = Integer.parseInt(args[j]);
		      j++;
		    } else {
		    	System.out.println("Bad argument: " + args[j]);
		      print_usage ();
		    }
		  }

		  // print out cache configuration
		  System.out.println("Cache parameters:\n");
		  System.out.format("Cache Size (KB)\t\t\t%d\n", cachesize_kb);
		  System.out.format("Cache Associativity\t\t%d\n", associativity);
		  System.out.format("Cache Block Size (bytes)\t%d\n", blocksize_bytes);
		  System.out.format("Miss penalty (cyc)\t\t%d\n",miss_penalty);
		  System.out.println("\n");

		  int rows = cachesize_kb * 1024 / (blocksize_bytes * associativity);
			//initialize cache with num blocks and assoc
		  int[][] cache = new int[rows][associativity];
		  List<Queue<Integer>> LRUqueue = new ArrayList<Queue<Integer>>(rows);
		  for (int n = 0; n < rows; ++n) {
    		LRUqueue.add(new LinkedList<Integer>());
		  }
		  int[][] data = new int[rows][associativity];
		  int[][] check = new int[rows][associativity];

			int mem_penalty = 0;
		  int execution_time = 0;
		  int instructions = 0;
			int dirty_evictions = 0;
		  int load_misses = 0;
		  int store_misses = 0;
		  int load_hits = 0;
		  int store_hits = 0;

		  Scanner sc = new Scanner(System.in);
		  while (sc.hasNext()) {
			sc.next(); //get rid of hashmark
			loadstore = sc.nextInt();
			address = sc.nextLong(16); //16 specifies it's in hex
			icount = sc.nextInt();
			instructions += icount;
			execution_time += icount;

		  //here is where you will want to process your memory accesses
			int index = (int)(address / blocksize_bytes) % rows;
			int tag = (int) (address / rows);
			boolean miss = true;
			boolean hit = false;
			if(i<10){
				System.out.println("\t " + loadstore + " " + Long.toHexString(address) + " " + icount);
			}

			i++;
// 1st access
			if (cache[index] == null) {
				miss = true;
				mem_penalty += miss_penalty;
				execution_time += miss_penalty;
				data[index][0] = loadstore;
				cache[index][0] = tag;
				LRUqueue.get(index).add(tag);
				System.out.println("null");
			}

			for (int n = 0; n < associativity; n++) {
				if (hit == true) {
					continue;
				}
				if ((cache[index][n] == tag) && (check[index][n] == 1)) {
					if (LRUqueue.get(index).size() > 1) {
						Queue<Integer> placed = put(LRUqueue.get(index), tag);
						LRUqueue.set(index, placed);
					}
					if (data[index][n] != 1) {
						data[index][n] = loadstore;
					}
					hit = true;
					miss = false;
				} else { //miss, cache has room
					if ((check[index][n] == 0)) {
						cache[index][n] = tag;
						if (data[index][n] != 1) {
							data[index][n] = loadstore;
						}
						LRUqueue.get(index).add(tag);
						miss = true;
						check[index][n] = 1;
						execution_time += miss_penalty;
						mem_penalty += miss_penalty;
						hit = true;
					} else if (n == associativity - 1){
						//miss, no room
						int LRU = LRUqueue.get(index).remove();
						for (int y = 0; y < associativity; y++) {
							if (cache[index][y] == LRU) {
								//evict
								if (data[index][y] == 1) { //check if dirty (mem had old copy)
									dirty_evictions++;
									execution_time += miss_penalty + 2;
									mem_penalty += miss_penalty + 2;
								} else {
									mem_penalty += miss_penalty;
									execution_time += miss_penalty;
								}
								cache[index][y] = tag;
								data[index][y] = loadstore;
								check[index][y] = 1;
								LRUqueue.get(index).add(tag);
								hit = true;
								miss = true;
							}
						}
					}
				}
			}

			if ((loadstore == 0) && (miss == true)) {
				load_misses += 1;
			} else if ((loadstore == 0) && !miss) {
				load_hits += 1;
			} else if ((loadstore == 1) && (miss == true)) {
				store_misses += 1;
			} else if ((loadstore == 1) && !miss){
				store_hits += 1;
			}
		  }

			int mem_accesses = load_hits + store_hits + load_misses + store_misses;
			float total_miss_rate = (float)(load_misses + store_misses) / mem_accesses;
			float read_miss = load_misses / (float)(load_hits + load_misses);
			float mem_cpi = mem_penalty / (float) instructions;
			float cpi = execution_time / (float) instructions;
			float avg_mem_access = mem_penalty / (float) mem_accesses;

		  // Here is where you want to print out stats
//		  System.out.format("Lines found = %i \n",i);
		  System.out.println("Simulation results:\n");
		  //  Use your simulator to output the following statistics.  The
		  //  print statements are provided, just replace the question marks with
		  //  your calcuations.
		  System.out.format("\texecution time %d cycles\n", execution_time);
		  System.out.format("\tinstructions %d\n", instructions);
		  System.out.format("\tmemory accesses %d\n", mem_accesses);
		  System.out.format("\toverall miss rate %.2f\n", total_miss_rate );
		  System.out.format("\tread miss rate %.2f\n", read_miss);
		  System.out.format("\tmemory CPI %.2f\n", mem_cpi);
		  System.out.format("\ttotal CPI %.2f\n", cpi);
		  System.out.format("\taverage memory access time %.2f cycles\n",  avg_mem_access);
		  System.out.format("\tdirty evictions %d\n", dirty_evictions);
		  System.out.format("\tload_misses %d\n", load_misses);
		  System.out.format("\tstore_misses %d\n", store_misses);
		  System.out.format("\tload_hits %d\n", load_hits);
		  System.out.format("\tstore_hits %d\n", store_hits);

	}

}
