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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codice.usng4j.CoordinatePrecision;
import org.codice.usng4j.UsngCoordinate;

final class UsngCoordinateImpl implements UsngCoordinate {
  private static final String REGEX_GROUPING_FORMAT = "(%s)%s%s";
  private static final String USNG_REGEXP =
      String.format(
          REGEX_GROUPING_FORMAT,
          ZONE_REGEX_STRING,
          LATITUDE_BAND_REGEX_STRING,
          USNG_COORDINATE_PART_REGEX_STRING);
  private static final String MGRS_REGEXP =
      String.format(
          REGEX_GROUPING_FORMAT,
          ZONE_REGEX_STRING,
          LATITUDE_BAND_REGEX_STRING,
          MGRS_COORDINATE_PART_REGEX_STRING);

  private int zoneNumber;

  private char latitudeBandLetter;

  private Character columnLetter;

  private Character rowLetter;

  private Integer easting;

  private Integer northing;

  private CoordinatePrecision precision;

  /**
   * @param zoneNumber the zone number of this USNG coordinate.
   * @param latitudeBandLetter the latitude band letter of this USNG coordinate.
   */
  UsngCoordinateImpl(final int zoneNumber, final char latitudeBandLetter) {
    this.zoneNumber = zoneNumber;
    this.latitudeBandLetter = latitudeBandLetter;
    this.precision = CoordinatePrecision.SIX_BY_EIGHT_DEGREES;
  }

  /**
   * @param zoneNumber the zone number of this USNG coordinate.
   * @param latitudeBandLetter the latitude band letter of this USNG coordinate.
   * @param columnLetter the grid column letter of this USNG coordinate.
   * @param rowLetter the grid row letter of this USNG coordinate.
   */
  UsngCoordinateImpl(
      final int zoneNumber,
      final char latitudeBandLetter,
      final char columnLetter,
      char rowLetter) {
    this(zoneNumber, latitudeBandLetter);
    this.columnLetter = columnLetter;
    this.rowLetter = rowLetter;
    this.precision = CoordinatePrecision.ONE_HUNDRED_KILOMETERS;
  }

  /**
   * @param zoneNumber the zone number of this USNG coordinate.
   * @param latitudeBandLetter the latitude band letter of this USNG coordinate.
   * @param columnLetter the grid column letter of this USNG coordinate.
   * @param rowLetter the grid row letter of this USNG coordinate.
   * @param easting - the easting value of this USNG coordinate.
   * @param northing - the northing value of this USNG coordinate.
   */
  UsngCoordinateImpl(
      final int zoneNumber,
      final char latitudeBandLetter,
      final char columnLetter,
      char rowLetter,
      int easting,
      int northing) {
    this(zoneNumber, latitudeBandLetter, columnLetter, rowLetter);
    this.easting = easting;
    this.northing = northing;
    this.precision =
        CoordinatePrecision.forEastNorth(Integer.toString(easting), Integer.toString(northing));
  }

  /**
   * @param zoneNumber the zone number of this USNG coordinate.
   * @param latitudeBandLetter the latitude band letter of this USNG coordinate.
   * @param columnLetter the grid column letter of this USNG coordinate.
   * @param rowLetter the grid row letter of this USNG coordinate.
   * @param easting - the easting value of this USNG coordinate.
   * @param northing - the northing value of this USNG coordinate.
   * @param precision - the precision value of this USNG coordinate.
   */
  UsngCoordinateImpl(
      final int zoneNumber,
      final char latitudeBandLetter,
      final char columnLetter,
      char rowLetter,
      int easting,
      int northing,
      CoordinatePrecision precision) {
    this(zoneNumber, latitudeBandLetter, columnLetter, rowLetter);
    this.easting = easting;
    this.northing = northing;
    this.precision = precision;
  }

  /** {@inheritDoc} */
  @Override
  public int getZoneNumber() {
    return this.zoneNumber;
  }

  /** {@inheritDoc} */
  @Override
  public char getLatitudeBandLetter() {
    return this.latitudeBandLetter;
  }

  /** {@inheritDoc} */
  @Override
  public Character getColumnLetter() {
    return this.columnLetter;
  }

  /** {@inheritDoc} */
  @Override
  public Character getRowLetter() {
    return this.rowLetter;
  }

  /** {@inheritDoc} */
  @Override
  public Integer getEasting() {
    return this.easting;
  }

  /** {@inheritDoc} */
  @Override
  public Integer getNorthing() {
    return this.northing;
  }

  /** {@inheritDoc} */
  @Override
  public CoordinatePrecision getPrecision() {
    return this.precision;
  }

  /**
   * @param usngStr a properly formatted USNG string.
   * @return a fully parsed UsngCoordinate object.
   * @throws ParseException when 'usngStr' isn't in USNG format.
   */
  static UsngCoordinate parseUsngString(final String usngStr) throws ParseException {
    return UsngCoordinateImpl.parseCoordinateString(usngStr, USNG_REGEXP);
  }

  /**
   * @param mgrsStr a properly formatted MGRS string.
   * @return a fully parsed UsngCoordinate object.
   * @throws ParseException when 'msgrsStr' isn't in MGRS format.
   */
  static UsngCoordinate parseMgrsString(final String mgrsStr) throws ParseException {
    return UsngCoordinateImpl.parseCoordinateString(mgrsStr, MGRS_REGEXP);
  }

  private static UsngCoordinate parseCoordinateString(
      final String coordinateString, final String regexp) throws ParseException {
    UsngCoordinate result = null;

    Pattern pattern = Pattern.compile(regexp);
    Matcher m = pattern.matcher(coordinateString.toUpperCase());

    if (!m.matches()) {
      String message =
          String.format(
              "Supplied argument '%s' is not a valid USNG formatted String.", coordinateString);
      throw new ParseException(message, 0);
    }

    int captureGroupsCount = m.groupCount();
    int zoneNumber = Integer.parseInt(m.group(1));
    char latitudeBandLetter = m.group(2).toCharArray()[0];

    if (m.group(3) != null) {
      char columnLetter = m.group(3).toCharArray()[0];
      char rowLetter = m.group(3).toCharArray()[1];

      if (captureGroupsCount > 4
          && !(StringUtils.isEmpty(m.group(4)) || StringUtils.isEmpty(m.group(5)))) {
        String easting = m.group(4).trim();
        String northing = m.group(5).trim();
        int eastingInt = Integer.parseInt(easting);
        int northingInt = Integer.parseInt(northing);

        result =
            new UsngCoordinateImpl(
                zoneNumber, latitudeBandLetter, columnLetter, rowLetter, eastingInt, northingInt);
      } else if (!StringUtils.isEmpty(m.group(4))) {
        // full numerical location with easting and northing values given as n+n digits
        String numericalLocation = m.group(4).trim();
        int splitIndex = numericalLocation.length() / 2;
        // easting value is first n digits and northing value is last n digits
        String easting = numericalLocation.substring(0, splitIndex);
        String northing = numericalLocation.substring(splitIndex);
        int eastingInt = Integer.parseInt(easting);
        int northingInt = Integer.parseInt(northing);
        CoordinatePrecision precision = CoordinatePrecision.forEastNorth(easting, northing);

        result =
            new UsngCoordinateImpl(
                zoneNumber,
                latitudeBandLetter,
                columnLetter,
                rowLetter,
                eastingInt,
                northingInt,
                precision);
      } else {
        result = new UsngCoordinateImpl(zoneNumber, latitudeBandLetter, columnLetter, rowLetter);
      }
    } else {
      result = new UsngCoordinateImpl(zoneNumber, latitudeBandLetter);
    }

    return result;
  }

  /**
   * @return a String representation of this USNG coordinate. The returned String is parseable by
   *     'parseUsngString' and calling
   *     coordinate.equals(UsngCoordinate.parseUsngString(coordinate.toString()) will return true.
   */
  @Override
  public String toString() {
    return this.toString(true);
  }

  /** {@inheritDoc} */
  @Override
  public String toMgrsString() {
    return this.toString(false);
  }

  private String toString(final boolean includeSpaces) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(zoneNumber).append(latitudeBandLetter);

    if (columnLetter != null && rowLetter != null) {
      if (includeSpaces) {
        stringBuilder.append(" ");
      }

      stringBuilder.append(columnLetter).append(rowLetter);

      if (easting != null && northing != null) {
        if (includeSpaces) {
          stringBuilder.append(" ");
        }

        stringBuilder.append(precision.format(easting));

        if (includeSpaces) {
          stringBuilder.append(" ");
        }

        stringBuilder.append(precision.format(northing));
      }
    }

    return stringBuilder.toString();
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object object) {
    if (object == null) {
      return false;
    }

    if (!(object instanceof UsngCoordinateImpl)) {
      return false;
    }

    UsngCoordinateImpl other = (UsngCoordinateImpl) object;

    return new EqualsBuilder()
        .append(this.zoneNumber, other.zoneNumber)
        .append(this.latitudeBandLetter, other.latitudeBandLetter)
        .append(this.columnLetter, other.columnLetter)
        .append(this.rowLetter, other.rowLetter)
        .append(this.easting, other.easting)
        .append(this.northing, other.northing)
        .append(this.precision, other.precision)
        .build();
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(this.zoneNumber)
        .append(this.latitudeBandLetter)
        .append(this.columnLetter)
        .append(this.rowLetter)
        .append(this.easting)
        .append(this.northing)
        .append(this.precision)
        .build();
  }
}
