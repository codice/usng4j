/* Copyright (c) 2009 Larry Moore, larmoor@gmail.com
 *               2014 Mike Adair, Richard Greenwood, Didier Richard, Stephen Irons, Olivier Terral and Calvin Metcalf (proj4js)
 *               2017 Codice Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package org.codice.usng4j.impl;

import java.text.ParseException;
import java.util.Optional;
import org.codice.usng4j.BoundingBox;
import org.codice.usng4j.CoordinatePrecision;
import org.codice.usng4j.CoordinateSystemTranslator;
import org.codice.usng4j.DecimalDegreesCoordinate;
import org.codice.usng4j.NSIndicator;
import org.codice.usng4j.UpsCoordinate;
import org.codice.usng4j.UsngCoordinate;
import org.codice.usng4j.UtmCoordinate;
import org.codice.usng4j.UtmUpsCoordinate;

public final class CoordinateSystemTranslatorImpl implements CoordinateSystemTranslator {

  public static final double NORTHING_OFFSET = 10000000.0; // (meters)

  private static final double DEG_2_RAD = Math.PI / 180.0;

  private static final double RAD_2_DEG = 180.0 / Math.PI;

  private static final int BLOCK_SIZE = 100000;

  private static final double EPSILON = Math.ulp(1.0);

  private static final double RHO_ADJUSTER_VALUE = 12_637_275.1116;

  private static final double ES = 0.08181918271;

  // For diagram of zone sets; please see the "United States National Grid" white paper.
  private static final int GRIDSQUARE_SET_COL_SIZE = 8; // column width of grid square set

  private static final int GRIDSQUARE_SET_ROW_SIZE = 20; // row height of grid square set

  // UTM offsets
  private static final double EASTING_OFFSET = 500000.0; // (meters)

  // UPS offsets
  private static final int FALSE_UPS_NORTHING = 2_000_000;

  private static final int FALSE_UPS_EASTING = 2_000_000;

  // scale factor of central meridian
  private static final double K0 = 0.9996;

  private static final String USNG_SQ_LET_ODD = "ABCDEFGHJKLMNPQRSTUV";

  private static final String USNG_SQ_LET_EVEN = "FGHJKLMNPQRSTUVABCDE";

  private double equatorialRadius;

  private double eccPrimeSquared;

  private double eccSquared;

  private double e1;

  /**
   * @param isNad83Datum if 'true' then the class will be initialized with North American Datum 1983
   *     values.
   */
  public CoordinateSystemTranslatorImpl(boolean isNad83Datum) {
    // check for NAD83
    if (isNad83Datum) {
      this.equatorialRadius = 6378137.0; // GRS80 ellipsoid (meters)
      this.eccSquared = 0.006694380023;
    }
    // else NAD27 datum is assumed
    else {
      this.equatorialRadius = 6378206.4; // Clarke 1866 ellipsoid (meters)
      this.eccSquared = 0.006768658;
    }

    this.eccPrimeSquared = this.eccSquared / (1 - this.eccSquared);

    this.e1 = (1 - Math.sqrt(1 - this.eccSquared)) / (1 + Math.sqrt(1 - this.eccSquared));
  }

  /** A convenience constructor that uses NAD 83 datum. */
  public CoordinateSystemTranslatorImpl() {
    this(true);
  }

  int getZoneNumber(double lat, double lon) {
    // sanity check on input
    if (lon > 360 || lon < -180 || lat > 84 || lat < -80) {
      throw new IllegalArgumentException(
          String.format("Invalid input - lat: %f, lon: %f", lat, lon));
    }

    // convert 0-360 to [-180 to 180] range
    double lonTemp = (lon + 180) - ((int) ((lon + 180) / 360)) * 360 - 180;
    int zoneNumber = ((int) (lonTemp + 180) / 6) + 1;

    // Handle special case of west coast of Norway
    if (lat >= 56.0 && lat < 64.0 && lonTemp >= 3.0 && lonTemp < 12.0) {
      zoneNumber = 32;
    }

    // Special zones for Svalbard
    if (lat >= 72.0 && lat < 84.0) {
      if (lonTemp >= 0.0 && lonTemp < 9.0) {
        zoneNumber = 31;
      } else if (lonTemp >= 9.0 && lonTemp < 21.0) {
        zoneNumber = 33;
      } else if (lonTemp >= 21.0 && lonTemp < 33.0) {
        zoneNumber = 35;
      } else if (lonTemp >= 33.0 && lonTemp < 42.0) {
        zoneNumber = 37;
      }
    }

    return zoneNumber;
  }

  @Override
  public UsngCoordinate toUsng(final BoundingBox latLonCoordinate) {
    // calculate midpoints for use in USNG string calculation
    double lat = (latLonCoordinate.getNorth() + latLonCoordinate.getSouth()) / 2;
    double lon = (latLonCoordinate.getEast() + latLonCoordinate.getWest()) / 2;

    // round down edge cases
    if (lon >= 180) {
      lon = 179.9;
    } else if (lon <= -180) {
      lon = -179.9;
    }

    // round down edge cases
    if (lat >= 90) {
      lat = 89.9;
    } else if (lat <= -90) {
      lat = -89.9;
    }

    // calculate distance between two points (North, West) and (South, East)
    int R = 6371000; // metres
    double phi1 = latLonCoordinate.getNorth() * CoordinateSystemTranslatorImpl.DEG_2_RAD;
    double phi2 = latLonCoordinate.getSouth() * CoordinateSystemTranslatorImpl.DEG_2_RAD;
    double deltaPhi =
        (latLonCoordinate.getSouth() - latLonCoordinate.getNorth())
            * CoordinateSystemTranslatorImpl.DEG_2_RAD;
    double deltaLlamda =
        (latLonCoordinate.getWest() - latLonCoordinate.getEast())
            * CoordinateSystemTranslatorImpl.DEG_2_RAD;

    // trigonometry calculate distance

    double height = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2);
    height = R * 2 * Math.atan2(Math.sqrt(height), Math.sqrt(1 - height));
    double length =
        Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLlamda / 2) * Math.sin(deltaLlamda / 2);
    length = R * 2 * Math.atan2(Math.sqrt(length), Math.sqrt(1 - length));

    double dist = Math.max(height, length);
    // divide distance by square root of two

    if (lon == 0
        && (latLonCoordinate.getEast() > 90 || latLonCoordinate.getEast() < -90)
        && (latLonCoordinate.getWest() > 90 || latLonCoordinate.getWest() < -90)) {
      lon = 180;
    }
    // calculate a USNG string with a precision based on distance
    // precision is defined in toUsng declaration
    CoordinatePrecision precision = CoordinatePrecision.ONE_METER;

    if (dist > 100000) {
      precision = CoordinatePrecision.SIX_BY_EIGHT_DEGREES;
    } else if (dist > 10000) {
      precision = CoordinatePrecision.ONE_HUNDRED_KILOMETERS;
    } else if (dist > 1000) {
      precision = CoordinatePrecision.TEN_KILOMETERS;
    } else if (dist > 100) {
      precision = CoordinatePrecision.ONE_KILOMETER;
    } else if (dist > 10) {
      precision = CoordinatePrecision.ONE_HUNDRED_METERS;
    } else if (dist > 1) {
      precision = CoordinatePrecision.TEN_METERS;
    }

    // result is a USNG string of the form DDL LL DDDDD DDDDD
    // length of string will be based on the precision variable
    return this.toUsng(new DecimalDegreesCoordinateImpl(lat, lon), precision);
  }

  @Override
  public UtmCoordinate toUtm(final DecimalDegreesCoordinate decimalDegreesCoordinate) {
    double lat = decimalDegreesCoordinate.getLat();
    double lon = decimalDegreesCoordinate.getLon();

    // note: input of lon = 180 or -180 with zone 60 not allowed; use 179.9999

    // Constrain reporting USNG coords to the latitude range [80S .. 84N]
    /////////////////
    if (lat > 84.0 || lat < -80.0) {
      throw new IllegalArgumentException(
          String.format("valid range for lat parameter is -80<lat>84. Supplied value: %f.", lat));
    }
    //////////////////////

    // sanity check on input - turned off when testing with Generic Viewer
    if (lon > 360 || lon < -180 || lat > 90 || lat < -90) {
      throw new IllegalArgumentException(
          String.format("Invalid input - lat: %f, lon: %f", lat, lon));
    }

    // Make sure the longitude is between -180.00 .. 179.99..
    // Convert values on 0-360 range to this range.
    double lonTemp = (lon + 180) - (int) ((lon + 180) / 360) * 360 - 180;
    double latRad = lat * CoordinateSystemTranslatorImpl.DEG_2_RAD;
    double lonRad = lonTemp * CoordinateSystemTranslatorImpl.DEG_2_RAD;
    // user-supplied zone number will force coordinates to be computed in a particular zone
    int zoneNumber = this.getZoneNumber(lat, lon);
    double lonOrigin = (zoneNumber - 1) * 6 - 180 + 3; // +3 puts origin in middle of zone
    double lonOriginRad = lonOrigin * CoordinateSystemTranslatorImpl.DEG_2_RAD;

    double N =
        this.equatorialRadius
            / Math.sqrt(1 - this.eccSquared * Math.sin(latRad) * Math.sin(latRad));
    double T = Math.tan(latRad) * Math.tan(latRad);
    double C = this.eccPrimeSquared * Math.cos(latRad) * Math.cos(latRad);
    double A = Math.cos(latRad) * (lonRad - lonOriginRad);

    // Note that the term Mo drops out of the "M" equation, because phi
    // (latitude crossing the central meridian, lambda0, at the origin of the
    //  x,y coordinates), is equal to zero for UTM.
    double M =
        this.equatorialRadius
            * ((1
                        - this.eccSquared / 4
                        - 3 * (this.eccSquared * this.eccSquared) / 64
                        - 5 * (this.eccSquared * this.eccSquared * this.eccSquared) / 256)
                    * latRad
                - (3 * this.eccSquared / 8
                        + 3 * this.eccSquared * this.eccSquared / 32
                        + 45 * this.eccSquared * this.eccSquared * this.eccSquared / 1024)
                    * Math.sin(2 * latRad)
                + (15 * this.eccSquared * this.eccSquared / 256
                        + 45 * this.eccSquared * this.eccSquared * this.eccSquared / 1024)
                    * Math.sin(4 * latRad)
                - (35 * this.eccSquared * this.eccSquared * this.eccSquared / 3072)
                    * Math.sin(6 * latRad));

    double UTMEasting =
        (CoordinateSystemTranslatorImpl.K0
                * N
                * (A
                    + (1 - T + C) * (A * A * A) / 6
                    + (5 - 18 * T + T * T + 72 * C - 58 * this.eccPrimeSquared)
                        * (A * A * A * A * A)
                        / 120)
            + CoordinateSystemTranslatorImpl.EASTING_OFFSET);

    double UTMNorthing =
        (CoordinateSystemTranslatorImpl.K0
            * (M
                + N
                    * Math.tan(latRad)
                    * ((A * A) / 2
                        + (5 - T + 9 * C + 4 * C * C) * (A * A * A * A) / 24
                        + (61 - 58 * T + T * T + 600 * C - 330 * this.eccPrimeSquared)
                            * (A * A * A * A * A * A)
                            / 720)));

    return new UtmCoordinateImpl(zoneNumber, UTMEasting, UTMNorthing);
  }

  @Override
  public UpsCoordinate toUps(final DecimalDegreesCoordinate decimalDegreesCoordinate) {
    // TODO:  implement
    throw new RuntimeException("NEEDS IMPLEMENTATION!");
  }

  @Override
  public UtmUpsCoordinate toUtmUps(final DecimalDegreesCoordinate decimalDegreesCoordinate) {
    // TODO:  implement
    throw new RuntimeException("NEEDS IMPLEMENTATION!");
  }

  @Override
  public UsngCoordinate toUsng(final DecimalDegreesCoordinate decimalDegreesCoordinate) {
    return this.toUsng(decimalDegreesCoordinate, CoordinatePrecision.ONE_METER);
  }

  @Override
  public UsngCoordinateImpl toUsng(
      final DecimalDegreesCoordinate decimalDegreesCoordinate,
      final CoordinatePrecision coordinatePrecision) {
    double lat = decimalDegreesCoordinate.getLat();
    double lon = decimalDegreesCoordinate.getLon();
    int precision = coordinatePrecision.getIntValue() + 1;

    // make lon between -180 & 180
    if (lon < -180) {
      lon += 360;
    } else if (lon > 180) {
      lon -= 360;
    }

    // convert lat/lon to UTM coordinates
    UtmCoordinate utmCoordinate = this.toUtm(new DecimalDegreesCoordinateImpl(lat, lon));
    double UTMEasting = utmCoordinate.getEasting();
    double UTMNorthing = utmCoordinate.getNorthing();
    // ...then convert UTM to USNG

    // southern hemisphere case
    if (lat < 0) {
      // Use offset for southern hemisphere
      UTMNorthing += CoordinateSystemTranslatorImpl.NORTHING_OFFSET;
    }

    int zoneNumber = this.getZoneNumber(lat, lon);

    // UTM northing and easting is the analogue of USNG letters + USNG northing and easting
    // so remove the component of UTM northing and easting that corresponds with the USNG letters
    double USNGNorthing = Math.round(UTMNorthing) % CoordinateSystemTranslatorImpl.BLOCK_SIZE;
    double USNGEasting = Math.round(UTMEasting) % CoordinateSystemTranslatorImpl.BLOCK_SIZE;

    // truncate USNG string digits to achieve specified precision
    USNGNorthing = Math.floor(USNGNorthing / Math.pow(10, (5 - coordinatePrecision.getIntValue())));
    USNGEasting = Math.floor(USNGEasting / Math.pow(10, (5 - coordinatePrecision.getIntValue())));
    char utmLetterDesignator = this.getUtmLetterDesignator(lat).charAt(0);

    // begin building USNG string "DDL"

    // add 100k meter grid letters to USNG string "DDL LL"
    if (precision < 1) {
      return new UsngCoordinateImpl(zoneNumber, utmLetterDesignator);
    }

    String USNGLetters = this.findGridLetters(zoneNumber, UTMNorthing, UTMEasting);
    char columnLetter = USNGLetters.charAt(0);
    char rowLetter = USNGLetters.charAt(1);
    // REVISIT: Modify to incorporate dynamic precision ?

    // if requested precision is higher than USNG northing or easting, pad front
    // with zeros

    // add easting and northing to USNG string "DDL LL D+ D+"
    if (coordinatePrecision.getIntValue() < 1) {
      return new UsngCoordinateImpl(zoneNumber, utmLetterDesignator, columnLetter, rowLetter);
    }

    return new UsngCoordinateImpl(
        zoneNumber,
        utmLetterDesignator,
        columnLetter,
        rowLetter,
        (int) USNGEasting,
        (int) USNGNorthing);
  }

  @Override
  public BoundingBox toBoundingBox(final UtmCoordinate utmCoordinate) {
    final UtmUpsCoordinate suppliedCoordinate = (UtmUpsCoordinate) utmCoordinate;
    if (suppliedCoordinate.isUTM()) {
      return toBoundingBox((UtmUpsCoordinate) utmCoordinate);
    } else {
      throw new IllegalArgumentException(utmCoordinate + " is not a UTM coordinate");
    }
  }

  String getUtmLetterDesignator(double lat) {
    if (lat > 84 || lat < -80) {
      return "Z";
    } else {
      double index = (lat + 80) / 8;
      if (index >= 6) {
        index++; // skip 'I'
      }
      if (index >= 12) {
        index++; // skip 'O'
      }
      if (index >= 22) {
        index--; // adjust for 80 to 84, which should be 'X'
      }

      return String.valueOf((char) (67 + index));
    }
  }

  /**
   * **************** Find the set for a given zone. ************************
   *
   * <p>There are six unique sets, corresponding to individual grid numbers in sets 1-6, 7-12,
   * 13-18, etc. Set 1 is the same as sets 7, 13, ..; Set 2 is the same as sets 8, 14, ..
   *
   * <p>See p. 10 of the "United States National Grid" white paper.
   *
   * <p>*************************************************************************
   */
  private int findSet(int zoneNum) {
    int z = zoneNum % 6;

    if (z < 0) {
      return -1;
    }

    if (z == 0) {
      return 6;
    }

    return z;
  }

  /**
   * ************************************************************************ Retrieve the square
   * identification for a given coordinate pair & zone See "lettersHelper" function documentation
   * for more details.
   *
   * <p>*************************************************************************
   */
  private String findGridLetters(int zoneNum, double northing, double easting) {
    int row = 1;

    // northing coordinate to single-meter precision
    long north_1m = Math.round(northing);

    // Get the row position for the square identifier that contains the point
    while (north_1m >= CoordinateSystemTranslatorImpl.BLOCK_SIZE) {
      north_1m = north_1m - CoordinateSystemTranslatorImpl.BLOCK_SIZE;
      row++;
    }

    // cycle repeats (wraps) after 20 rows
    row = row % CoordinateSystemTranslatorImpl.GRIDSQUARE_SET_ROW_SIZE;
    int col = 0;

    // easting coordinate to single-meter precision
    long east_1m = Math.round(easting);

    // Get the column position for the square identifier that contains the point
    while (east_1m >= CoordinateSystemTranslatorImpl.BLOCK_SIZE) {
      east_1m = east_1m - CoordinateSystemTranslatorImpl.BLOCK_SIZE;
      col++;
    }

    // cycle repeats (wraps) after 8 columns
    col = col % CoordinateSystemTranslatorImpl.GRIDSQUARE_SET_COL_SIZE;

    int set = findSet(zoneNum);
    return this.lettersHelper(set, row, col);
  }

  /**
   * ************************************************************************ Retrieve the Square
   * Identification (two-character letter code), for the given row, column and set identifier (set
   * refers to the zone set: zones 1-6 have a unique set of square identifiers; these identifiers
   * are repeated for zones 7-12, etc.)
   *
   * <p>See p. 10 of the "United States National Grid" white paper for a diagram of the zone sets.
   *
   * <p>*************************************************************************
   */
  private String lettersHelper(int setter, int row, int col) {

    // handle case of last row
    if (row == 0) {
      row = CoordinateSystemTranslatorImpl.GRIDSQUARE_SET_ROW_SIZE - 1;
    } else {
      row--;
    }

    // handle case of last column
    if (col == 0) {
      col = CoordinateSystemTranslatorImpl.GRIDSQUARE_SET_COL_SIZE - 1;
    } else {
      col--;
    }

    String l1 = null;
    String l2 = null;

    switch (setter) {
      case 1:
        l1 = "ABCDEFGH"; // column ids
        l2 = CoordinateSystemTranslatorImpl.USNG_SQ_LET_ODD; // row ids
        break;

      case 2:
        l1 = "JKLMNPQR";
        l2 = CoordinateSystemTranslatorImpl.USNG_SQ_LET_EVEN;
        break;

      case 3:
        l1 = "STUVWXYZ";
        l2 = CoordinateSystemTranslatorImpl.USNG_SQ_LET_ODD;
        break;

      case 4:
        l1 = "ABCDEFGH";
        l2 = CoordinateSystemTranslatorImpl.USNG_SQ_LET_EVEN;
        break;

      case 5:
        l1 = "JKLMNPQR";
        l2 = CoordinateSystemTranslatorImpl.USNG_SQ_LET_ODD;
        break;

      case 6:
        l1 = "STUVWXYZ";
        l2 = CoordinateSystemTranslatorImpl.USNG_SQ_LET_EVEN;
        break;
    }

    return l1.substring(col, col + 1) + l2.substring(row, row + 1);
  }

  public BoundingBox toBoundingBox(final UtmUpsCoordinate utmUpsCoordinate) {
    return toBoundingBox(utmUpsCoordinate, null);
  }

  BoundingBox toBoundingBox(final UtmCoordinate utmCoordinate, final Integer accuracy) {
    /**
     * ************ convert UTM coords to decimal degrees *********************
     *
     * <p>Equations from USGS Bulletin 1532 (or USGS Professional Paper 1395) East Longitudes are
     * positive, West longitudes are negative. North latitudes are positive, South latitudes are
     * negative.
     *
     * <p>Expected Input args: DecimalDegreesCoordinate : northing-m (numeric), eg. 432001.8
     * southern hemisphere NEGATIVE from equator ('real' value - 10,000,000) UTMEasting : easting-m
     * (numeric), eg. 4000000.0 UTMZoneNumber : 6-deg longitudinal zone (numeric), eg. 18
     *
     * <p>lat-lon coordinates are turned in the object 'ret' : ret.lat and ret.lon
     *
     * <p>*************************************************************************
     */

    // remove 500,000 meter offset for longitude

    BoundingBox result = null;

    DecimalDegreesCoordinate southWest = toLatLon(utmCoordinate);

    if (accuracy <= 100000) {
      UtmCoordinate tempUtmCoordinate =
          new UtmCoordinateImpl(
              utmCoordinate.getZoneNumber(),
              utmCoordinate.getEasting() + accuracy,
              utmCoordinate.getNorthing() + accuracy);
      DecimalDegreesCoordinate northEast = this.toLatLon(tempUtmCoordinate);
      result =
          new BoundingBoxImpl(
              northEast.getLat(), southWest.getLat(), northEast.getLon(), southWest.getLon());
    } else {
      String zoneLetter = this.getUtmLetterDesignator(southWest.getLat());
      double[] lats = this.getZoneLetterLats(zoneLetter);
      double[] lons = this.getZoneNumberLons(utmCoordinate.getZoneNumber());

      if (lats != null) {
        result = new BoundingBoxImpl(lats[0], lats[1], lons[0], lons[1]);
      }
    }

    return result;
  }

  @Override
  public DecimalDegreesCoordinate toLatLon(final UtmUpsCoordinate utmUpsCoordinate) {
    if (utmUpsCoordinate.isUTM()) {
      return toLatLon((UtmCoordinate) utmUpsCoordinate);
    } else {
      return toLatLon((UpsCoordinate) utmUpsCoordinate);
    }
  }

  @Override
  public DecimalDegreesCoordinate toLatLon(final UpsCoordinate upsCoordinate) {
    final double northing = upsCoordinate.getNorthing() - FALSE_UPS_NORTHING;
    final double easting = upsCoordinate.getEasting() - FALSE_UPS_EASTING;

    final boolean isNorth = upsCoordinate.getLattitudeBand() >= 'Y';

    final double lat;
    if (northing == 0.0 && easting == 0.0) {
      lat = isNorth ? 90.0 : -90.0;
    } else {
      final double rho = Math.hypot(easting, northing);
      final double t = rho != 0.0 ? rho / RHO_ADJUSTER_VALUE : Math.pow(EPSILON, 2.0);
      final double taup = (1.0 / t - t) / 2.0;
      final double tau = tauf(taup);
      lat = (isNorth ? 1 : -1) * Math.atan(tau) * RAD_2_DEG;
    }

    final double lon = Math.atan2(easting, isNorth ? -northing : northing) * RAD_2_DEG;
    return new DecimalDegreesCoordinateImpl(lat, lon);
  }

  @Override
  public DecimalDegreesCoordinate toLatLon(final UtmCoordinate utmCoordinate) {
    return utmToLatLonNsNormalized(utmCoordinate);
  }

  private static double atanh(final double x) {
    return Math.log((1.0 + x) / (1.0 - x)) / 2.0;
  }

  private static double eatanhe(final double x) {
    return ES * atanh(ES * x);
  }

  private static double taupf(final double tauValue) {
    final double tau1 = Math.hypot(1.0, tauValue);
    final double sig = Math.sinh(eatanhe(tauValue / tau1));
    return Math.hypot(1.0, sig) * tauValue - sig * tau1;
  }

  private static double tauf(final double taupValue) {
    final double e2m = 1.0 - Math.pow(ES, 2.0);
    // To lowest order in e^2, taup = (1 - e^2) * tau = _e2m * tau; so use
    // tau = taup/_e2m as a starting guess.  (This starting guess is the
    // geocentric latitude which, to first order in the flattening, is equal
    // to the conformal latitude.)  Only 1 iteration is needed for |lat| <
    // 3.35 deg, otherwise 2 iterations are needed.  If, instead, tau = taup
    // is used the mean number of iterations increases to 1.99 (2 iterations
    // are needed except near tau = 0).
    double tau = taupValue / e2m;
    final double stol = Math.sqrt(EPSILON) / 10.0 * Math.max(1.0, Math.abs(taupValue));
    // min iterations = 1, max iterations = 2; mean = 1.94; 5 iterations panic
    for (int i = 0; i < 5; i++) {
      final double taupa = taupf(tau);
      final double dtau = (taupValue - taupa) *
          (1.0 + e2m * Math.pow(tau, 2.0)) /
          (e2m * Math.hypot(1.0, tau) * Math.hypot(1.0, taupa));
      tau += dtau;
      if (!(Math.abs(dtau) >= stol)) {
        break;
      }
    }
    return tau;
  }

  private DecimalDegreesCoordinate utmToLatLonNsNormalized(UtmCoordinate utmCoordinate) {
    double xUTM = utmCoordinate.getEasting() - CoordinateSystemTranslatorImpl.EASTING_OFFSET;
    double yUTM = utmCoordinate.getNorthingWithOffset();

    // origin longitude for the zone (+3 puts origin in zone center)
    int lonOrigin = (utmCoordinate.getZoneNumber() - 1) * 6 - 180 + 3;
    // M is the "true distance along the central meridian from the Equator to phi
    // (latitude)
    double M = yUTM / CoordinateSystemTranslatorImpl.K0;
    double mu =
        M
            / (this.equatorialRadius
                * (1
                    - this.eccSquared / 4.0
                    - 3 * this.eccSquared * this.eccSquared / 64.0
                    - 5 * this.eccSquared * this.eccSquared * this.eccSquared / 256.0));
    // phi1 is the "footprint latitude" or the latitude at the central meridian which
    // has the same y coordinate as that of the point (phi (lat), lambda (lon) ).
    double phi1Rad =
        mu
            + (3 * this.e1 / 2 - 27 * this.e1 * this.e1 * this.e1 / 32) * Math.sin(2 * mu)
            + (21 * this.e1 * this.e1 / 16 - 55 * this.e1 * this.e1 * this.e1 * this.e1 / 32)
                * Math.sin(4 * mu)
            + (151 * this.e1 * this.e1 * this.e1 / 96) * Math.sin(6 * mu);

    // Terms used in the conversion equations
    double N1 =
        this.equatorialRadius
            / Math.sqrt(1 - this.eccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad));
    double T1 = Math.tan(phi1Rad) * Math.tan(phi1Rad);
    double C1 = this.eccPrimeSquared * Math.cos(phi1Rad) * Math.cos(phi1Rad);
    double R1 =
        this.equatorialRadius
            * (1 - this.eccSquared)
            / Math.pow(1 - this.eccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad), 1.5);
    double D = xUTM / (N1 * CoordinateSystemTranslatorImpl.K0);
    // Calculate latitude, in decimal degrees
    double lat =
        phi1Rad
            - (N1 * Math.tan(phi1Rad) / R1)
                * (D * D / 2
                    - (5 + 3 * T1 + 10 * C1 - 4 * C1 * C1 - 9 * this.eccPrimeSquared)
                        * D
                        * D
                        * D
                        * D
                        / 24
                    + (61
                            + 90 * T1
                            + 298 * C1
                            + 45 * T1 * T1
                            - 252 * this.eccPrimeSquared
                            - 3 * C1 * C1)
                        * D
                        * D
                        * D
                        * D
                        * D
                        * D
                        / 720.0);
    lat = lat * CoordinateSystemTranslatorImpl.RAD_2_DEG;

    if (lat == 0) {
      lat = 0.001;
    }

    // Calculate longitude, in decimal degrees
    double lon =
        (D
                - (1 + 2 * T1 + C1) * D * D * D / 6
                + (5 - 2 * C1 + 28 * T1 - 3 * C1 * C1 + 8 * this.eccPrimeSquared + 24 * T1 * T1)
                    * D
                    * D
                    * D
                    * D
                    * D
                    / 120)
            / Math.cos(phi1Rad);

    lon = lonOrigin + lon * CoordinateSystemTranslatorImpl.RAD_2_DEG;
    return new DecimalDegreesCoordinateImpl(lat, lon);
  }

  private double[] getZoneNumberLons(int zone) {
    double east = -180.0 + (6 * zone);
    double west = east - 6;

    return new double[] {east, west};
  }

  private double[] getZoneLetterLats(String letter) {
    switch (letter) {
      case "C":
        return new double[] {-72.0, -80.0};
      case "D":
        return new double[] {-64.0, -72.0};
      case "E":
        return new double[] {-56.0, -64.0};
      case "F":
        return new double[] {-48.0, -56.0};
      case "G":
        return new double[] {-40.0, -48.0};
      case "H":
        return new double[] {-32.0, -40.0};
      case "J":
        return new double[] {-24.0, -32.0};
      case "K":
        return new double[] {-16.0, -24.0};
      case "L":
        return new double[] {-8.0, -16.0};
      case "M":
        return new double[] {-0.01, -8.0};
      case "N":
        return new double[] {8.0, 0.01};
      case "P":
        return new double[] {16.0, 8.0};
      case "Q":
        return new double[] {24.0, 16.0};
      case "R":
        return new double[] {32.0, 24.0};
      case "S":
        return new double[] {40.0, 32.0};
      case "T":
        return new double[] {48.0, 40.0};
      case "U":
        return new double[] {56.0, 48.0};
      case "V":
        return new double[] {64.0, 56.0};
      case "W":
        return new double[] {72.0, 64.0};
      case "X":
        return new double[] {84.0, 72.0};
    }

    return null;
  }

  @Override
  public UtmCoordinate toUtm(final UsngCoordinate usngCoordinate) {

    int zone = usngCoordinate.getZoneNumber();
    char letter = usngCoordinate.getLatitudeBandLetter();
    Character sq1 = Optional.ofNullable(usngCoordinate.getColumnLetter()).orElse((char) 0);
    Character sq2 = Optional.ofNullable(usngCoordinate.getRowLetter()).orElse((char) 0);
    Integer east = usngCoordinate.getEasting();
    Integer north = usngCoordinate.getNorthing();

    // easting goes from 100,000 - 800,000 and repeats across zones
    // A,J,S correspond with 100,000, B,K,T correspond with 200,000 etc
    String[] eastingArray = {"", "AJS", "BKT", "CLU", "DMV", "ENW", "FPX", "GQY", "HRZ"};

    // zoneBase - southern edge of N-S zones of millions of meters
    double[] zoneBase = {
      1.1, 2.0, 2.8, 3.7, 4.6, 5.5, 6.4, 7.3, 8.2, 9.1, 0, 0.8, 1.7, 2.6, 3.5, 4.4, 5.3, 6.2, 7.0,
      7.9
    };

    // multiply zone bases by 1 million to get the proper length for each
    for (int i = 0; i < zoneBase.length; i++) {
      zoneBase[i] = zoneBase[i] * 1000000;
    }

    // northing goes from 0 - 1,900,000. A corresponds with 0, B corresponds with 200,000, V
    // corresponds with 1,900,000
    String northingArrayOdd = "ABCDEFGHJKLMNPQRSTUV";

    // even numbered zones have the northing letters offset from the odd northing. So, F corresponds
    // with 0, G corresponds
    // with 100,000 and E corresponds with 1,900,000
    String northingArrayEven = "FGHJKLMNPQRSTUVABCDE";

    double easting = -1.0;

    for (int i = 0; i < eastingArray.length; i++) {

      // loop through eastingArray until sq1 is found
      // the index of the string the letter is in will be the base easting, as explained in the
      // declaration
      // of eastingArray
      if (eastingArray[i].indexOf(sq1) != -1) {

        // multiply by 100,000 to get the proper base easting
        easting = i * 100000;

        // add the east parameter to get the total easting
        easting = easting + east * Math.pow(10, 5 - usngCoordinate.getPrecision().getIntValue());
        break;
      }
    }

    double northing = 0;

    if (sq2 != 0) {
      // if zone number is even, use northingArrayEven, if odd, use northingArrayOdd
      // similar to finding easting, the index of sq2 corresponds with the base easting
      if (zone % 2 == 0) {
        northing = northingArrayEven.indexOf(sq2) * 100000;
      } else if (zone % 2 == 1) {
        northing = northingArrayOdd.indexOf(sq2) * 100000;
      }

      // we can exploit the repeating behavior of northing to find what the total northing should be
      // iterate through the horizontal zone bands until our northing is greater than the zoneBase
      // of our zone

      while (northing < zoneBase["CDEFGHJKLMNPQRSTUVWX".indexOf(letter)]) {
        northing = northing + 2000000;
      }

      // add the north parameter to get the total northing
      northing = northing + north * Math.pow(10, 5 - usngCoordinate.getPrecision().getIntValue());
    } else {
      // add approximately half of the height of one large region to ensure we're in the right zone
      northing = zoneBase["CDEFGHJKLMNPQRSTUVWX".indexOf(letter)] + 499600;
    }

    // set return object
    return new UtmCoordinateImpl(zone, letter, easting, northing);
  }

  @Override
  public DecimalDegreesCoordinate toLatLon(UsngCoordinate usngCoordinate) {

    // convert USNG coords to UTM; this routine counts digits and sets precision
    UtmCoordinate coords =
        this.toUtm(
            new UsngCoordinateImpl(
                usngCoordinate.getZoneNumber(),
                usngCoordinate.getLatitudeBandLetter(),
                Optional.ofNullable(usngCoordinate.getColumnLetter()).orElse((char) 0),
                Optional.ofNullable(usngCoordinate.getRowLetter()).orElse((char) 0),
                Optional.ofNullable(usngCoordinate.getEasting()).orElse(0),
                Optional.ofNullable(usngCoordinate.getNorthing()).orElse(0)));

    double northing = coords.getNorthing();

    // southern hemisphere case
    if (usngCoordinate.getLatitudeBandLetter() < 'N') {
      northing -= CoordinateSystemTranslatorImpl.NORTHING_OFFSET;
    }

    UtmCoordinate tempUtmCoordinate =
        new UtmCoordinateImpl(
            usngCoordinate.getZoneNumber(), (int) coords.getEasting(), (int) northing);
    return this.toLatLon(tempUtmCoordinate);
  }

  @Override
  public BoundingBox toBoundingBox(UsngCoordinate usngCoordinate) {

    // convert USNG coords to UTM; this routine counts digits and sets precision
    UtmCoordinate coords =
        this.toUtm(
            new UsngCoordinateImpl(
                usngCoordinate.getZoneNumber(),
                usngCoordinate.getLatitudeBandLetter(),
                Optional.ofNullable(usngCoordinate.getColumnLetter()).orElse((char) 0),
                Optional.ofNullable(usngCoordinate.getRowLetter()).orElse((char) 0),
                Optional.ofNullable(usngCoordinate.getEasting()).orElse(0),
                Optional.ofNullable(usngCoordinate.getNorthing()).orElse(0)));

    double northing = coords.getNorthing();

    // southern hemisphere case
    if (usngCoordinate.getLatitudeBandLetter() < 'N') {
      northing -= CoordinateSystemTranslatorImpl.NORTHING_OFFSET;
    }

    int accuracy = (int) (100000 / Math.pow(10, usngCoordinate.getPrecision().getIntValue()));

    UtmCoordinate tempUtmCoordinate =
        new UtmCoordinateImpl(
            usngCoordinate.getZoneNumber(), (int) coords.getEasting(), (int) northing);
    return this.toBoundingBox(tempUtmCoordinate, accuracy);
  }

  @Override
  public UtmUpsCoordinate parseUtmUpsString(String utmString) throws ParseException {
    return UtmUpsCoordinateImpl.parseUtmUpsString(utmString);
  }

  @Override
  public UtmCoordinate parseUtmString(String utmString) throws ParseException {
    return UtmUpsCoordinateImpl.parseUtmUpsString(utmString);
  }

  @Override
  public UsngCoordinate parseUsngString(String usngString) throws ParseException {
    return UsngCoordinateImpl.parseUsngString(usngString);
  }

  @Override
  public UsngCoordinate parseMgrsString(String mgrsString) throws ParseException {
    return UsngCoordinateImpl.parseMgrsString(mgrsString);
  }
}
