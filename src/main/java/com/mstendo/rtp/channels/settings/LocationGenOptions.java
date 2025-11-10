package com.mstendo.rtp.channels.settings;

public final class LocationGenOptions {
  private final Shape shape;
  private final GenFormat genFormat;
  private final int minX;
  private final int maxX;
  private final int minZ;
  private final int maxZ;
  private final int nearRadiusMin;
  private final int nearRadiusMax;
  private final int centerX;
  private final int centerZ;
  private final int maxLocationAttempts;

  public LocationGenOptions(Shape shape, GenFormat genFormat, int minX, int maxX, int minZ, int maxZ, 
                            int nearRadiusMin, int nearRadiusMax, int centerX, int centerZ, int maxLocationAttempts) {
    this.shape = shape;
    this.genFormat = genFormat;
    this.minX = minX;
    this.maxX = maxX;
    this.minZ = minZ;
    this.maxZ = maxZ;
    this.nearRadiusMin = nearRadiusMin;
    this.nearRadiusMax = nearRadiusMax;
    this.centerX = centerX;
    this.centerZ = centerZ;
    this.maxLocationAttempts = maxLocationAttempts;
  }

  @Override
  public final String toString() {
    return "LocationGenOptions[shape=" + this.shape + 
           ", genFormat=" + this.genFormat + 
           ", minX=" + this.minX + 
           ", maxX=" + this.maxX + 
           ", minZ=" + this.minZ + 
           ", maxZ=" + this.maxZ + 
           ", nearRadiusMin=" + this.nearRadiusMin + 
           ", nearRadiusMax=" + this.nearRadiusMax + 
           ", centerX=" + this.centerX + 
           ", centerZ=" + this.centerZ + 
           ", maxLocationAttempts=" + this.maxLocationAttempts + "]";
  }

  @Override
  public final int hashCode() {
    return java.util.Objects.hash(this.shape, this.genFormat, this.minX, this.maxX, this.minZ, 
                                  this.maxZ, this.nearRadiusMin, this.nearRadiusMax, 
                                  this.centerX, this.centerZ, this.maxLocationAttempts);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LocationGenOptions that = (LocationGenOptions) o;
    return this.minX == that.minX &&
           this.maxX == that.maxX &&
           this.minZ == that.minZ &&
           this.maxZ == that.maxZ &&
           this.nearRadiusMin == that.nearRadiusMin &&
           this.nearRadiusMax == that.nearRadiusMax &&
           this.centerX == that.centerX &&
           this.centerZ == that.centerZ &&
           this.maxLocationAttempts == that.maxLocationAttempts &&
           this.shape == that.shape &&
           this.genFormat == that.genFormat;
  }

  public Shape shape() {
    return this.shape;
  }

  public GenFormat genFormat() {
    return this.genFormat;
  }

  public int minX() {
    return this.minX;
  }

  public int maxX() {
    return this.maxX;
  }

  public int minZ() {
    return this.minZ;
  }

  public int maxZ() {
    return this.maxZ;
  }

  public int nearRadiusMin() {
    return this.nearRadiusMin;
  }

  public int nearRadiusMax() {
    return this.nearRadiusMax;
  }

  public int centerX() {
    return this.centerX;
  }

  public int centerZ() {
    return this.centerZ;
  }

  public int maxLocationAttempts() {
    return this.maxLocationAttempts;
  }

  public enum Shape {
    SQUARE,
    RECTANGULAR,
    RADIAL
  }

  public enum GenFormat {
    RECTANGULAR,
    RADIAL
  }
}
