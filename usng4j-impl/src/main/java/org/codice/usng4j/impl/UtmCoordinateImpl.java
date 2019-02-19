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

import static org.codice.usng4j.NSIndicator.NORTH;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codice.usng4j.CoordinatePrecision;
import org.codice.usng4j.NSIndicator;
import org.codice.usng4j.UtmCoordinate;

final class UtmCoordinateImpl implements UtmCoordinate {

  private static final double NORTHING_OFFSET = 10_000_000; // (meters)

  private double easting;

  private double northing;

  private int zoneNumber;

  private CoordinatePrecision precision;

  private Character lattitudeBand;

  private NSIndicator nsIndicator;

  /**
   * @param zoneNumber the zone number for this UTM coordinate.
   * @param easting the easting value for the UTM coordinate.
   * @param northing the northing value of the UTM coordinate.
   */
  UtmCoordinateImpl(int zoneNumber, double easting, double northing) {
    this.zoneNumber = zoneNumber;
    this.easting = easting;
    this.northing = northing;
    this.precision =
        CoordinatePrecision.forEastNorth(Double.toString(easting), Double.toString(northing));
    this.nsIndicator = NORTH;
  }

  /**
   * @param zoneNumber the zone number for this UTM coordinate.
   * @param easting the easting value for the UTM coordinate.
   * @param northing the northing value of the UTM coordinate.
   * @param nsIndicator N/S or null depending on hemisphere and whether it is used
   */
  UtmCoordinateImpl(int zoneNumber, double easting, double northing, NSIndicator nsIndicator) {
    this(zoneNumber, easting, northing);
    this.nsIndicator = nsIndicator;
  }

  /**
   * @param zoneNumber the zone number for this UTM coordinate.
   * @param easting the easting value for the UTM coordinate.
   * @param northing the northing value of the UTM coordinate.
   * @param lattitudeBand the MGRS latitude band for this UTM coordinate.
   */
  UtmCoordinateImpl(int zoneNumber, char lattitudeBand, double easting, double northing) {
    this(zoneNumber, easting, northing);
    this.lattitudeBand = lattitudeBand;
  }

  @Override
  public double getEasting() {
    return easting;
  }

  @Override
  public double getNorthing() {
    return northing;
  }

  @Override
  public double getNorthingWithOffset() {
    return getNSIndicator().equals(NORTH) ? getNorthing() : getNorthing() - NORTHING_OFFSET;
  }

  @Override
  public int getZoneNumber() {
    return zoneNumber;
  }

  @Override
  public Character getLattitudeBand() {
    return getLatitudeBand();
  }

  @Override
  public Character getLatitudeBand() {
    return this.lattitudeBand;
  }

  @Override
  public NSIndicator getNSIndicator() {
    return this.nsIndicator;
  }

  @Override
  public CoordinatePrecision getPrecision() {
    return this.precision;
  }

  /**
   * @param utmString a UTM formatted string. e.g. {@code 10Q 123456 -0123456}
   * @return an object representation of 'utmString'
   * @throws ParseException when 'utmString' isn't correctly formatted.
   */
  static UtmCoordinate parseUtmString(String utmString) throws ParseException {
    Pattern utmRegexp =
        Pattern.compile("(\\d\\d?)(-?[CDEFGHJKLMNPQRSTUVWX]?)(\\W?-?\\d{6})(\\W?-?\\d{7})");

    Matcher m = utmRegexp.matcher(utmString);

    if (!m.matches()) {
      String message =
          String.format("Supplied argument '%s' is not a valid UTM formatted String.", utmString);
      throw new ParseException(message, 0);
    }

    int zoneNumber = Integer.parseInt(m.group(1));
    String latitudeBandString = m.group(2);
    int easting = Integer.parseInt(m.group(3).trim());
    int northing = Integer.parseInt(m.group(4).trim());

    if (latitudeBandString.length() > 0) {
      return new UtmCoordinateImpl(zoneNumber, latitudeBandString.charAt(0), easting, northing);
    } else {
      return new UtmCoordinateImpl(zoneNumber, easting, northing);
    }
  }

  /**
   * @return a String representation of this UTM coordinate. The returned String is parseable by
   *     'parseUtmString'. Calling
   *     coordinate.equals(UtmCoordinate.parseUtmString(coordinate.toString()) will return true.
   */
  @Override
  public String toString() {
    return new StringBuilder()
        .append(zoneNumber)
        .append(lattitudeBand == null ? "" : lattitudeBand)
        .append(" ")
        .append(precision.format((int) easting))
        .append(" ")
        .append(precision.format((int) northing))
        .toString();
  }

  @Override
  public boolean equals(final Object object) {
    if (object == null) {
      return false;
    }

    if (!(object instanceof UtmCoordinateImpl)) {
      return false;
    }

    UtmCoordinateImpl other = (UtmCoordinateImpl) object;

    return new EqualsBuilder()
        .append(this.zoneNumber, other.zoneNumber)
        .append(this.easting, other.easting)
        .append(this.northing, other.northing)
        .append(this.lattitudeBand, other.lattitudeBand)
        .build();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(this.zoneNumber)
        .append(this.easting)
        .append(this.northing)
        .append(this.lattitudeBand)
        .build();
  }
}
