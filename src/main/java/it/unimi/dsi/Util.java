package it.unimi.dsi;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.util.XoRoShiRo128PlusRandomGenerator;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

public final class Util {
  private static final NumberFormat FORMAT_DOUBLE = NumberFormat.getInstance(Locale.US);
  
  static {
    if (FORMAT_DOUBLE instanceof DecimalFormat)
      ((DecimalFormat)FORMAT_DOUBLE).applyPattern("#,##0.00"); 
  }
  
  private static final NumberFormat FORMAT_LONG = NumberFormat.getInstance(Locale.US);
  
  static {
    if (FORMAT_DOUBLE instanceof DecimalFormat)
      ((DecimalFormat)FORMAT_LONG).applyPattern("#,###"); 
  }
  
  private static final FieldPosition UNUSED_FIELD_POSITION = new FieldPosition(0);
  
  public static synchronized String format(double d) {
    return FORMAT_DOUBLE.format(d, new StringBuffer(), UNUSED_FIELD_POSITION).toString();
  }
  
  public static synchronized String format(long l) {
    return FORMAT_LONG.format(l, new StringBuffer(), UNUSED_FIELD_POSITION).toString();
  }
  
  public static String format(double d, NumberFormat format) {
    StringBuffer s = new StringBuffer();
    return format.format(d, s, UNUSED_FIELD_POSITION).toString();
  }
  
  public static String format(long l, NumberFormat format) {
    StringBuffer s = new StringBuffer();
    return format.format(l, s, UNUSED_FIELD_POSITION).toString();
  }
  
  public static String formatSize(long l) {
    if (l >= 1000000000000L)
      return format(l / 1.0E12D) + "T"; 
    if (l >= 1000000000L)
      return format(l / 1.0E9D) + "G"; 
    if (l >= 1000000L)
      return format(l / 1000000.0D) + "M"; 
    if (l >= 1000L)
      return format(l / 1000.0D) + "K"; 
    return Long.toString(l);
  }
  
  public static String formatBinarySize(long l) {
    if ((l & -l) != l)
      throw new IllegalArgumentException("Not a power of 2: " + l); 
    if (l >= 1099511627776L)
      return format(l >> 40L) + "Ti"; 
    if (l >= 1073741824L)
      return format(l >> 30L) + "Gi"; 
    if (l >= 1048576L)
      return format(l >> 20L) + "Mi"; 
    if (l >= 1024L)
      return format(l >> 10L) + "Ki"; 
    return Long.toString(l);
  }
  
  public static String formatSize2(long l) {
    if (l >= 1099511627776L)
      return format(l / 1.099511627776E12D) + "Ti"; 
    if (l >= 1073741824L)
      return format(l / 1.073741824E9D) + "Gi"; 
    if (l >= 1048576L)
      return format(l / 1048576.0D) + "Mi"; 
    if (l >= 1024L)
      return format(l / 1024.0D) + "Ki"; 
    return Long.toString(l);
  }
  
  public static String formatSize(long l, NumberFormat format) {
    if (l >= 1000000000000L)
      return format(l / 1.0E12D) + "T"; 
    if (l >= 1000000000L)
      return format(l / 1.0E9D) + "G"; 
    if (l >= 1000000L)
      return format(l / 1000000.0D) + "M"; 
    if (l >= 1000L)
      return format(l / 1000.0D) + "K"; 
    return Long.toString(l);
  }
  
  public static String formatBinarySize(long l, NumberFormat format) {
    if ((l & -l) != l)
      throw new IllegalArgumentException("Not a power of 2: " + l); 
    if (l >= 1099511627776L)
      return format(l >> 40L) + "Ti"; 
    if (l >= 1073741824L)
      return format(l >> 30L) + "Gi"; 
    if (l >= 1048576L)
      return format(l >> 20L) + "Mi"; 
    if (l >= 1024L)
      return format(l >> 10L) + "Ki"; 
    return Long.toString(l);
  }
  
  public static String formatSize2(long l, NumberFormat format) {
    if (l >= 1099511627776L)
      return format(l / 1.099511627776E12D) + "Ti"; 
    if (l >= 1073741824L)
      return format(l / 1.073741824E9D) + "Gi"; 
    if (l >= 1048576L)
      return format(l / 1048576.0D) + "Mi"; 
    if (l >= 1024L)
      return format(l / 1024.0D) + "Ki"; 
    return Long.toString(l);
  }
  
  public static final Runtime RUNTIME = Runtime.getRuntime();
  
  public static boolean memoryIsLow() {
    return (availableMemory() * 100L < RUNTIME.totalMemory() * 5L);
  }
  
  public static long availableMemory() {
    return RUNTIME.freeMemory() + RUNTIME.maxMemory() - RUNTIME.totalMemory();
  }
  
  public static int percAvailableMemory() {
    return (int)(availableMemory() * 100L / Runtime.getRuntime().maxMemory());
  }
  
  public static void compactMemory() {
    try {
      byte[][] unused = new byte[128][];
      for (int i = unused.length; i-- != 0; unused[i] = new byte[2000000000]);
    } catch (OutOfMemoryError outOfMemoryError) {}
    System.gc();
  }
  
  private static final XoRoShiRo128PlusRandomGenerator seedUniquifier = new XoRoShiRo128PlusRandomGenerator(System.nanoTime());
  
  public static long randomSeed() {
    long x;
    synchronized (seedUniquifier) {
      x = seedUniquifier.nextLong();
    } 
    return x ^ System.nanoTime();
  }
  
  public static byte[] randomSeedBytes() {
    long seed = randomSeed();
    byte[] s = new byte[8];
    for (int i = 8; i-- != 0; s[i] = (byte)(int)(seed >>> i));
    return s;
  }
  
  public static int[] invertPermutationInPlace(int[] perm) {
    int n;
    label19: for (n = perm.length; n-- != 0; ) {
      int i = perm[n];
      if (i < 0) {
        perm[n] = -i - 1;
        continue;
      } 
      if (i != n) {
        int k = n;
        while (true) {
          int j = perm[i];
          perm[i] = -k - 1;
          if (j == n) {
            perm[n] = i;
            continue label19;
          } 
          k = i;
          i = j;
        } 
      } 
    } 
    return perm;
  }
  
  public static int[] invertPermutation(int[] perm, int[] inv) {
    for (int i = perm.length; i-- != 0; inv[perm[i]] = i);
    return inv;
  }
  
  public static int[] invertPermutation(int[] perm) {
    return invertPermutation(perm, new int[perm.length]);
  }
  
  public static int[] identity(int[] perm) {
    for (int i = perm.length; i-- != 0; perm[i] = i);
    return perm;
  }
  
  public static int[] identity(int n) {
    return identity(new int[n]);
  }
  
  public static int[] composePermutations(int[] p, int[] q, int[] r) {
    int length = p.length;
    for (int i = 0; i < length; ) {
      r[i] = q[p[i]];
      i++;
    } 
    return r;
  }
  
  public static int[] composePermutations(int[] p, int[] q) {
    int[] r = (int[])p.clone();
    composePermutations(p, q, r);
    return r;
  }
  
  public static int[] composePermutationsInPlace(int[] p, int[] q) {
    int length = p.length;
    int i;
    for (i = 0; i < length; i++) {
      if (q[i] >= 0) {
        int firstIndex = i;
        int firstElement = q[i];
        assert firstElement >= 0;
        int j = i;
        while (p[j] != firstIndex) {
          assert q[p[j]] >= 0;
          q[j] = -q[p[j]] - 1;
          j = p[j];
        } 
        q[j] = -firstElement - 1;
      } 
    } 
    for (i = 0; i < length; ) {
      q[i] = -q[i] - 1;
      i++;
    } 
    return q;
  }
  
  public static long[][] invertPermutationInPlace(long[][] perm) {
    long n;
    label20: for (n = BigArrays.length(perm); n-- != 0L; ) {
      long i = BigArrays.get(perm, n);
      if (i < 0L) {
        BigArrays.set(perm, n, -i - 1L);
        continue;
      } 
      if (i != n) {
        long k = n;
        while (true) {
          long j = BigArrays.get(perm, i);
          BigArrays.set(perm, i, -k - 1L);
          if (j == n) {
            BigArrays.set(perm, n, i);
            continue label20;
          } 
          k = i;
          i = j;
        } 
      } 
    } 
    return perm;
  }
  
  public static long[][] invertPermutation(long[][] perm, long[][] inv) {
    for (int i = perm.length; i-- != 0; ) {
      long[] t = perm[i];
      for (int d = t.length; d-- != 0; BigArrays.set(inv, t[d], BigArrays.index(i, d)));
    } 
    return inv;
  }
  
  public static long[][] invertPermutation(long[][] perm) {
    return invertPermutation(perm, LongBigArrays.newBigArray(BigArrays.length(perm)));
  }
  
  public static long[][] identity(long[][] perm) {
    for (int i = perm.length; i-- != 0; ) {
      long[] t = perm[i];
      for (int d = t.length; d-- != 0; t[d] = BigArrays.index(i, d));
    } 
    return perm;
  }
  
  public static long[][] identity(long n) {
    return identity(LongBigArrays.newBigArray(n));
  }
  
  public static long[][] composePermutations(long[][] p, long[][] q, long[][] r) {
    long length = BigArrays.length(p);
    long i;
    for (i = 0L; i < length; ) {
      BigArrays.set(r, i, BigArrays.get(q, BigArrays.get(p, i)));
      i++;
    } 
    return r;
  }
  
  public static long[][] composePermutations(long[][] p, long[][] q) {
    long[][] r = LongBigArrays.newBigArray(BigArrays.length(p));
    composePermutations(p, q, r);
    return r;
  }
  
  public static long[][] composePermutationsInPlace(long[][] p, long[][] q) {
    long length = BigArrays.length(p);
    long i;
    for (i = 0L; i < length; i++) {
      if (BigArrays.get(q, i) >= 0L) {
        long firstIndex = i;
        long firstElement = BigArrays.get(q, i);
        assert firstElement >= 0L;
        long j = i;
        while (BigArrays.get(p, j) != firstIndex) {
          assert BigArrays.get(q, BigArrays.get(p, j)) >= 0L;
          BigArrays.set(q, j, -BigArrays.get(q, BigArrays.get(p, j)) - 1L);
          j = BigArrays.get(p, j);
        } 
        BigArrays.set(q, j, -firstElement - 1L);
      } 
    } 
    for (long[] element : q) {
      long[] t = element;
      int l = t.length;
      for (int d = 0; d < l; d++)
        t[d] = -t[d] - 1L; 
    } 
    return q;
  }
}
