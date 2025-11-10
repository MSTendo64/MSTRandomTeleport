package it.unimi.dsi.util;

import it.unimi.dsi.Util;
import java.util.Random;

public class XoRoShiRo128PlusRandom extends Random {
  private static final long serialVersionUID = 1L;
  
  private long s0;
  
  private long s1;
  
  protected XoRoShiRo128PlusRandom(long s0, long s1) {
    this.s0 = s0;
    this.s1 = s1;
  }
  
  public XoRoShiRo128PlusRandom() {
    this(Util.randomSeed());
  }
  
  public XoRoShiRo128PlusRandom(long seed) {
    setSeed(seed);
  }
  
  public XoRoShiRo128PlusRandom copy() {
    return new XoRoShiRo128PlusRandom(this.s0, this.s1);
  }
  
  public long nextLong() {
    long s0 = this.s0;
    long s1 = this.s1;
    long result = s0 + s1;
    s1 ^= s0;
    this.s0 = Long.rotateLeft(s0, 24) ^ s1 ^ s1 << 16L;
    this.s1 = Long.rotateLeft(s1, 37);
    return result;
  }
  
  public int nextInt() {
    return (int)(nextLong() >>> 32L);
  }
  
  public int nextInt(int n) {
    return (int)nextLong(n);
  }
  
  public long nextLong(long n) {
    if (n <= 0L)
      throw new IllegalArgumentException("illegal bound " + n + " (must be positive)"); 
    long t = nextLong();
    long nMinus1 = n - 1L;
    if ((n & nMinus1) == 0L)
      return t >>> Long.numberOfLeadingZeros(nMinus1) & nMinus1; 
    long u;
    for (u = t >>> 1L; u + nMinus1 - (t = u % n) < 0L; u = nextLong() >>> 1L);
    return t;
  }
  
  public double nextDouble() {
    return (nextLong() >>> 11L) * 1.1102230246251565E-16D;
  }
  
  public double nextDoubleFast() {
    return Double.longBitsToDouble(0x3FF0000000000000L | nextLong() >>> 12L) - 1.0D;
  }
  
  public float nextFloat() {
    return (float)(nextLong() >>> 40L) * 5.9604645E-8F;
  }
  
  public boolean nextBoolean() {
    return (nextLong() < 0L);
  }
  
  public void nextBytes(byte[] bytes) {
    int i = bytes.length, n = 0;
    while (i != 0) {
      n = Math.min(i, 8);
      long bits;
      for (bits = nextLong(); n-- != 0; ) {
        bytes[--i] = (byte)(int)bits;
        bits >>= 8L;
      } 
    } 
  }
  
  protected XoRoShiRo128PlusRandom jump(long[] jump) {
    long s0 = 0L;
    long s1 = 0L;
    for (long element : jump) {
      for (int b = 0; b < 64; b++) {
        if ((element & 1L << b) != 0L) {
          s0 ^= this.s0;
          s1 ^= this.s1;
        } 
        nextLong();
      } 
    } 
    this.s0 = s0;
    this.s1 = s1;
    return this;
  }
  
  private static final long[] JUMP = new long[] { -2337365368286915419L, 1659688472399708668L };
  
  public XoRoShiRo128PlusRandom jump() {
    return jump(JUMP);
  }
  
  private static final long[] LONG_JUMP = new long[] { -3266927057705177477L, -2459076376072127807L };
  
  public XoRoShiRo128PlusRandom longJump() {
    return jump(LONG_JUMP);
  }
  
  public XoRoShiRo128PlusRandom split() {
    nextLong();
    XoRoShiRo128PlusRandom split = copy();
    long h0 = this.s0;
    long h1 = this.s1;
    long h2 = this.s0 + 6171709007915041769L;
    long h3 = this.s1 + -5504830798508614797L;
    h2 = Long.rotateLeft(h2, 50);
    h2 += h3;
    h0 ^= h2;
    h3 = Long.rotateLeft(h3, 52);
    h3 += h0;
    h1 ^= h3;
    h0 = Long.rotateLeft(h0, 30);
    h0 += h1;
    h2 ^= h0;
    h1 = Long.rotateLeft(h1, 41);
    h1 += h2;
    h3 ^= h1;
    h2 = Long.rotateLeft(h2, 54);
    h2 += h3;
    h0 ^= h2;
    h3 = Long.rotateLeft(h3, 48);
    h3 += h0;
    h1 ^= h3;
    h0 = Long.rotateLeft(h0, 38);
    h0 += h1;
    h2 ^= h0;
    h1 = Long.rotateLeft(h1, 37);
    h1 += h2;
    h3 ^= h1;
    h2 = Long.rotateLeft(h2, 62);
    h2 += h3;
    h0 ^= h2;
    h3 = Long.rotateLeft(h3, 34);
    h3 += h0;
    h1 ^= h3;
    h0 = Long.rotateLeft(h0, 5);
    h0 += h1;
    h2 ^= h0;
    h1 = Long.rotateLeft(h1, 36);
    h1 += h2;
    split.s0 = h0;
    split.s1 = h1;
    return split;
  }
  
  public void setSeed(long seed) {
    SplitMix64RandomGenerator r = new SplitMix64RandomGenerator(seed);
    this.s0 = r.nextLong();
    this.s1 = r.nextLong();
  }
  
  public void setState(long[] state) {
    if (state.length != 2)
      throw new IllegalArgumentException("The argument array contains " + state.length + " longs instead of " + '\002'); 
    this.s0 = state[0];
    this.s1 = state[1];
  }
}
