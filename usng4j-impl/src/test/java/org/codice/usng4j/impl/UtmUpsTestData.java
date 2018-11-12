package org.codice.usng4j.impl;

class UtmUpsTestData {
  final double latitude;
  final double longitude;
  final double easting;
  final double northing;
  final boolean northPole;

  UtmUpsTestData(
      final double latitude,
      final double longitude,
      final double easting,
      final double northing,
      final boolean northPole) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.easting = easting;
    this.northing = northing;
    this.northPole = northPole;
  }

  @Override
  public String toString() {
    return String.format(
        "Lat: %f,  Lon: %f,  E: %f,  N: %f,  NP: %b",
        latitude, longitude, easting, northing, northPole);
  }
}
