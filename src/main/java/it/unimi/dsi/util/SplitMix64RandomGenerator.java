package it.unimi.dsi.util;

import it.unimi.dsi.Util;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.Serializable;
import org.apache.commons.math3.random.AbstractRandomGenerator;

public class SplitMix64RandomGenerator extends AbstractRandomGenerator implements Serializable {
  private static final long serialVersionUID = 0L;
  
  private static final long PHI = -7046029254386353131L;
  
  private long x;
  
  public SplitMix64RandomGenerator() {
    this(Util.randomSeed());
  }
  
  public SplitMix64RandomGenerator(long seed) {
    setSeed(seed);
  }
  
  private static long staffordMix13(long z) {
    z = (z ^ z >>> 30L) * -4658895280553007687L;
    z = (z ^ z >>> 27L) * -7723592293110705685L;
    return z ^ z >>> 31L;
  }
  
  private static int staffordMix4Upper32(long z) {
    z = (z ^ z >>> 33L) * 7109453100751455733L;
    return (int)((z ^ z >>> 28L) * -3808689974395783757L >>> 32L);
  }
  
  public long nextLong() {
    return staffordMix13(this.x += -7046029254386353131L);
  }
  
  public int nextInt() {
    return staffordMix4Upper32(this.x += -7046029254386353131L);
  }
  
  public int nextInt(int n) {
    return (int)nextLong(n);
  }
  
  public long nextLong(long n) {
    // Byte code:
    //   0: lload_1
    //   1: lconst_0
    //   2: lcmp
    //   3: ifgt -> 38
    //   6: new java/lang/IllegalArgumentException
    //   9: dup
    //   10: new java/lang/StringBuilder
    //   13: dup
    //   14: invokespecial <init> : ()V
    //   17: ldc 'illegal bound '
    //   19: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: lload_1
    //   23: invokevirtual append : (J)Ljava/lang/StringBuilder;
    //   26: ldc ' (must be positive)'
    //   28: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: invokevirtual toString : ()Ljava/lang/String;
    //   34: invokespecial <init> : (Ljava/lang/String;)V
    //   37: athrow
    //   38: aload_0
    //   39: dup
    //   40: getfield x : J
    //   43: ldc2_w -7046029254386353131
    //   46: ladd
    //   47: dup2_x1
    //   48: putfield x : J
    //   51: invokestatic staffordMix13 : (J)J
    //   54: lstore_3
    //   55: lload_1
    //   56: lconst_1
    //   57: lsub
    //   58: lstore #5
    //   60: lload_1
    //   61: lload #5
    //   63: land
    //   64: lconst_0
    //   65: lcmp
    //   66: ifne -> 74
    //   69: lload_3
    //   70: lload #5
    //   72: land
    //   73: lreturn
    //   74: lload_3
    //   75: iconst_1
    //   76: lushr
    //   77: lstore #7
    //   79: lload #7
    //   81: lload #5
    //   83: ladd
    //   84: lload #7
    //   86: lload_1
    //   87: lrem
    //   88: dup2
    //   89: lstore_3
    //   90: lsub
    //   91: lconst_0
    //   92: lcmp
    //   93: ifge -> 119
    //   96: aload_0
    //   97: dup
    //   98: getfield x : J
    //   101: ldc2_w -7046029254386353131
    //   104: ladd
    //   105: dup2_x1
    //   106: putfield x : J
    //   109: invokestatic staffordMix13 : (J)J
    //   112: iconst_1
    //   113: lushr
    //   114: lstore #7
    //   116: goto -> 79
    //   119: lload_3
    //   120: lreturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #107	-> 0
    //   #108	-> 38
    //   #109	-> 55
    //   #111	-> 60
    //   #113	-> 74
    //   #114	-> 119
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   79	40	7	u	J
    //   0	121	0	this	Lit/unimi/dsi/util/SplitMix64RandomGenerator;
    //   0	121	1	n	J
    //   55	66	3	t	J
    //   60	61	5	nMinus1	J
    if (n <= 0L)
      throw new IllegalArgumentException("illegal bound " + n + " (must be positive)");
    long t = staffordMix13(this.x += -7046029254386353131L);
    long nMinus1 = n - 1L;
    if ((n & nMinus1) == 0L)
      return t & nMinus1;
    long u = t >>> 1;
    t = (u + nMinus1) - (u % n);
    while (t < 0L) {
      u = staffordMix13(this.x += -7046029254386353131L) >>> 1;
      t = (u + nMinus1) - (u % n);
    }
    return t;
  }
  
  public double nextDouble() {
    return (staffordMix13(this.x += -7046029254386353131L) >>> 11L) * 1.1102230246251565E-16D;
  }
  
  public float nextFloat() {
    return (staffordMix4Upper32(this.x += -7046029254386353131L) >>> 8) * 5.9604645E-8F;
  }
  
  public boolean nextBoolean() {
    return (staffordMix4Upper32(this.x += -7046029254386353131L) < 0);
  }
  
  public void nextBytes(byte[] bytes) {
    int i = bytes.length, n = 0;
    while (i != 0) {
      n = Math.min(i, 8);
      long bits;
      for (bits = staffordMix13(this.x += -7046029254386353131L); n-- != 0; ) {
        bytes[--i] = (byte)(int)bits;
        bits >>= 8L;
      } 
    } 
  }
  
  public void setSeed(long seed) {
    this.x = HashCommon.murmurHash3(seed);
  }
  
  public void setState(long state) {
    this.x = state;
  }
}
