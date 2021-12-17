// Ysabella Atehortua and Loren Behumi-Davis
// MyXfloat.java

public class MyXfloat extends Xfloat {

    int MYMASK = 0x80000000;

    public MyXfloat() {
	super();
    }

    public MyXfloat(float f) {
	super(f);
    }

    public MyXfloat(byte sign, byte exp, int man) {
	super(sign, exp, man);
    }

    public Xfloat xadd(Xfloat y) {
	byte newSign, newExp, diff;
	int newMan, shift;
	long xman, yman;
  // check if is 0
	if (this.exp == 0)
	    return new MyXfloat(y.sign, y.exp, y.man);
	else if (y.exp == 0)
	    return new MyXfloat(this.sign, this.exp, this.man);
	else {
	    shift = 0;
      // or w MSB
	    xman = this.man | BMASK;
	    yman = y.man | BMASK;
	    diff = (byte) (this.exp - y.exp);
      // if signs are different, normalize the exponents and shift man
	    if (diff < 0) {
		diff = (byte) -diff;
		xman = xman >> diff;
		newExp = y.exp;
	    } else if (diff > 0) {
		yman = yman >> diff;
		newExp = this.exp;
	    } else
      // if signs are same do nothing, check the signs
		newExp = this.exp;
	    if (this.sign == y.sign) {
		newSign = this.sign;
		xman += yman;
    // deal with poss overflow
    // System.out.println(xman);
		while ((xman & MYMASK) == 0) {
		    MYMASK = MYMASK >> 1;
		    shift++;
		}
  //  System.out.println(shift);
    // norm mantissa
		if (shift <= 8) {
		    xman = xman >> (8 - shift);
		    newExp = (byte) (newExp + (8 - shift));
		} else {
		    newExp = (byte) (newExp - (shift - 8));
		    xman = xman << (shift - 8);
		}
		newMan = (int) (xman & MMASK);
  } else if (this.sign == 1) { // same procedure for negative
		if (xman > yman) {
		    newSign = 1;
		    xman -= yman;
		    while ((xman & MYMASK) == 0) {
			MYMASK = MYMASK >> 1;
			shift++;
		    }
		    if (shift <= 8) {
			xman = xman >> (8 - shift);
			newExp = (byte) (newExp + (8 - shift));
		    } else {
			newExp = (byte) (newExp - (shift - 8));
			xman = xman << (shift - 8);
		    }
		    newMan = (int) (xman & MMASK);
		} else if (xman < yman) {
		    newSign = 0;
		    yman -= xman; //redundant code (again)
		    while ((yman & MYMASK) == 0) {
			MYMASK = MYMASK >> 1;
			shift++;
		    }
		    if (shift <= 8) {
			yman = yman >> (8 - shift);
			newExp = (byte) (newExp + (8 - shift));
		    } else {
			newExp = (byte) (newExp - (shift - 8));
			yman = yman << (shift - 8);
		    }
		    newMan = (int) (yman & MMASK);
		} else
		    return new MyXfloat((byte) 0, (byte) 0, 0);
	    } else if (y.sign == 1) {
		if (xman > yman) {
		    newSign = 0;
		    xman -= yman;
        // beep boop you know the drill
		    while ((xman & MYMASK) == 0) {
			MYMASK = MYMASK >> 1;
			shift++;
		    }
		    if (shift <= 8) {
			xman = xman >> (8 - shift);
			newExp = (byte) (newExp + (8 - shift));
		    } else {
			newExp = (byte) (newExp - (shift - 8));
			xman = xman << (shift - 8);
		    }
		    newMan = (int) (xman & MMASK);
		} else if (xman < yman) {
		    newSign = 1;
		    yman -= xman;
        // 1 more time!
		    while ((yman & MYMASK) == 0) {
			MYMASK = MYMASK >> 1;
			shift++;
		    }
		    if (shift <= 8) {
			yman = yman >> (8 - shift);
			newExp = (byte) (newExp + (8 - shift));
		    } else {
			newExp = (byte) (newExp - (shift - 8));
			yman = yman << (shift - 8);
		    }
		    newMan = (int) (yman & MMASK);
		} else
		    return new MyXfloat((byte) 0, (byte) 0, 0);
	    } else {
		newSign = (byte) 0;
		newExp = (byte) 0;
		newMan = 0;
	    }
	    return new MyXfloat(newSign, newExp, newMan);
	}
    }

    public Xfloat xmult(Xfloat y) {
	byte newSign, newExp;
	int newMan, shift;
	long xman, yman;
  //check for zeros
	if (this.exp == 0 || y.exp == 0)
	    return new MyXfloat((byte) 0, (byte) 0, 0);
	else {
    // if signs are equal, sign is 0; else it is 1
	    if (this.sign == y.sign)
		newSign = (byte) 0;
	    else
		newSign = (byte) 1;
    //bias exponent by 127
	    newExp = (byte) ((this.exp - 127) + (y.exp - 127) + 127);
      // & MSB with mantissa
	    xman = (long) (BMASK | this.man);
	    yman = (long) (BMASK | y.man);
	    xman *= yman; //mult mantissas
	    shift = 0;
	    while ((xman & MYMASK) == 0) {
		MYMASK = MYMASK >> 1;
		shift++;
	    }
      // add to mantissa for single precision
	    xman = xman >> (shift + 24);
	    newExp = (byte) (newExp + (shift + 1));
	    if ((xman & BMASK) == 0) {
		xman = xman << 1;
		newExp = (byte) (newExp - 1);
	    }
	}
	newMan = (int) xman;
	newMan = (MMASK & newMan);
	return new MyXfloat(newSign, newExp, newMan);
    }

    public static void main(String arg[]) {
	float x = Float.valueOf(arg[0]).floatValue(), y = Float.valueOf(arg[1]).floatValue();
	Xfloat xf, yf, zf, wf;
	if (arg.length < 2)
	    return;
	xf = new MyXfloat(x);
	yf = new MyXfloat(y);
	zf = xf.xmult(yf);
	wf = xf.xadd(yf);
	System.out.println("x:   " + xf + " " + x);
	System.out.println("y:   " + yf + " " + y);
	System.out.println("x*y: " + zf + " " + zf.toFloat());
	System.out.println("x+y: " + wf + " " + wf.toFloat());
    }
}
