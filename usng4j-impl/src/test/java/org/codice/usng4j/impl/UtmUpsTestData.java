package org.codice.usng4j.impl;

import org.codice.usng4j.NSIndicator;

class UtmUpsTestData {
  final double latitude;
  final double longitude;
  final double easting;
  final double northing;
  final NSIndicator nsIndicator;
  final String upsString;
  final String utmString;
  final String latLonString;
  final String mgrsString;

  UtmUpsTestData(
      final double latitude,
      final double longitude,
      final double easting,
      final double northing,
      final NSIndicator nsIndicator,
      final String upsString,
      final String utmString,
      final String latLonString,
      final String mgrsString) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.easting = easting;
    this.northing = northing;
    this.nsIndicator = nsIndicator;
    this.upsString = upsString;
    this.utmString = utmString;
    this.latLonString = latLonString;
    this.mgrsString = mgrsString;
  }

  @Override
  public String toString() {
    return String.format(
        "Lat: %f,  Lon: %f,  E: %f,  N: %f,  NSI: %s",
        latitude, longitude, easting, northing, nsIndicator);
  }
}
