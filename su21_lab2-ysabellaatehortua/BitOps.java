public class bitOps{
public static int isOdd(int x){
          return x & 1;
       }
public static int DivBy4(int x){
    return x >>> 2;
}

public static int nearestOdd(int x){
    return x | 1;
}

public static int flipParity(int x){
    return x ^ 1;
}

public static int isNegative(int x){
    return x >>> 31;
}

public static int clearBits(int x){
    int y = 0b000000000000000000000000011110000;
    return x & y;
}

public static int setBits(int x){
    int y = 0b000000000000000000000000001100000;
    int z = 0b11111111111111111111111100001111;
    x = x & z;
    x = x | y;
    return x;
}