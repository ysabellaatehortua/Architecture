public class BitDriver{
    

    	public static void main(String[] args) {

	    int input = 0;
	    try {
		input = Integer.parseInt(args[0]);
	    } catch (Exception e) {
		System.out.println("You need to enter an integer argument");
		System.exit(0);
	    }

	    System.out.println("isOdd on " + input + " returns " + BitOps.isOdd(input));
	    System.out.println("isNegative on " + input + " returns " + BitOps.isNegative(input));
	    System.out.println("DivBy4 on " + input + " returns " + BitOps.DivBy4(input));
      	    System.out.println("nearestOdd on " + input + " returns " + BitOps.nearestOdd(input));
	    System.out.println("flipParity on " + input + " returns " + BitOps.flipParity(input));
	    System.out.println("clearBits on " + input + " returns " + BitOps.clearBits(input));
	    System.out.println("setBits on " + input + " returns " + BitOps.setBits(input));
	}
    	
}
