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
import static org.codice.usng4j.NSIndicator.SOUTH;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codice.usng4j.CoordinatePrecision;
import org.codice.usng4j.NSIndicator;
import org.codice.usng4j.UtmUpsCoordinate;

public class UtmUpsCoordinateImpl implements UtmUpsCoordinate {

  public static final double NORTHING_OFFSET = 10_000_000; // (meters)

  private static final String UTMUPS_REGEXP =
      "\\s*(\\d*)([A-Z]?)\\s+(\\d+)(?:[mM][eE])?\\s+(\\d+)(?:[mM][nN])?\\s*((\\s+[nNsS])?)\\s*";
  private static final Pattern utmUpsRegexp = Pattern.compile(UTMUPS_REGEXP);
  private static final int ZONE_NUMBER_RE_GROUP = 1;
  private static final int LATITUDE_BAND_RE_GROUP = 2;
  private static final int EASTING_RE_GROUP = 3;
  private static final int NORTHING_RE_GROUP = 4;
  private static final int NS_INDICATOR_RE_GROUP = 5;
  private static final Set<Character> upsNorthenBands = new HashSet<>(Arrays.asList('Y', 'Z'));
  private static final Set<Character> upsSothernBands = new HashSet<>(Arrays.asList('A', 'B'));
  private static final Set<Character> utmNorthernBands =
      new HashSet<>(Arrays.asList('C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M'));
  private static final Set<Character> utmSothernBands =
      new HashSet<>(Arrays.asList('N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X'));
  // The following static sets are added for performance and readability
  private static final Set<Character> allValidUpsBands;
  private static final Set<Character> allValidUtmBands;
  private static final Set<Character> allAcceptableUpsBands;
  private static final Set<Character> allAcceptableUtmBands;

  static {
    allValidUpsBands = unifiedBandSet(upsNorthenBands, upsSothernBands);
    allAcceptableUpsBands = new HashSet<>(allValidUpsBands);
    allAcceptableUpsBands.add(null);
    allValidUtmBands = unifiedBandSet(utmNorthenBands, utmSothernBands);
    allAcceptableUtmBands = new HashSet<>(allValidUtmBands);
    allAcceptableUtmBands.add(null);
  }

  private final int zone;
  private final Character latitudeBand;
  private final double easting;
  private final double northing;
  private final CoordinatePrecision precision;
  private final NSIndicator nsIndicator;

  private UtmUpsCoordinateImpl(
      final int zone,
      @Nullable final Character latitudeBand,
      final double easting,
      final double northing,
      @Nullable final NSIndicator nsIndicator) {
    this.zone = zone;
    this.latitudeBand = latitudeBand;
    this.easting = easting;
    this.northing = northing;
    this.nsIndicator = nsIndicator;
    this.precision = CoordinatePrecision.forEastNorth((int) easting, (int) northing);
  }

  private static UtmUpsCoordinate fromZoneBandNorthingEastingNSI(
      final int zone,
      @Nullable final Character latitudeBand,
      final double easting,
      final double northing,
      @Nullable final NSIndicator nsIndicator) {
    return fromZoneBandEastingNorthingNSIIfPossible(
            zone, latitudeBand, easting, northing, nsIndicator)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    new UtmUpsCoordinateImpl(zone, latitudeBand, easting, northing, nsIndicator)
                        + " is neither UTM nor UPS coordinate"));
  }

  static Optional<UtmUpsCoordinate> fromZoneBandEastingNorthingNSIIfPossible(
      final int zone,
      @Nullable final Character latitudeBand,
      final double easting,
      final double northing,
      @Nullable final NSIndicator nsIndicator) {
    final UtmUpsCoordinate coordinateCandidate =
        new UtmUpsCoordinateImpl(zone, latitudeBand, easting, northing, nsIndicator);
    return validateCoordinate(coordinateCandidate);
  }

  private static Optional<UtmUpsCoordinate> validateCoordinate(
      final UtmUpsCoordinate utmUpsCoordinate) {
    return Optional.of(utmUpsCoordinate)
        .filter(
            coordinate ->
              coordinate.getLatitudeBand() != null || coordinate.getNSIndicator() != null)
        .filter(coordinate -> coordinate.getEasting() >= 0)
        .filter(coordinate -> coordinate.getEasting() <= 3_200_000)
        .filter(coordinate -> coordinate.getNorthing() >= 0)
        .filter(coordinate -> coordinate.getNorthing() <= 10_000_000)
        .filter(
            (coordinate ->
                (coordinate.getZoneNumber() == 0
                        && allAcceptableUpsBands.contains(coordinate.getLatitudeBand())
                    || (coordinate.getZoneNumber() >= 1
                        && coordinate.getZoneNumber() <= 60
                        && allAcceptableUtmBands.contains(coordinate.getLatitudeBand())))));
  }

  @SafeVarargs
  private static Set<Character> unifiedBandSet(final Set<Character>... bandSets) {
    return Stream.of(bandSets).flatMap(Collection::stream).collect(Collectors.toSet());
  }

  static UtmUpsCoordinate fromZoneBandNorthingEasting(
      final int zone, final Character latitudeBand, final double easting, final double northing) {
    return fromZoneBandNorthingEastingNSI(zone, latitudeBand, easting, northing, null);
  }

  @Override
  public double getNorthing() {
    return northing;
  }

  @Override
  public double getNorthingWithOffset() {
    return isUTM()
            && ((getNSIndicator() != null && getNSIndicator().equals(NORTH))
                || utmNorthernBands.contains(getLattitudeBand()))
        ? getNorthing()
        : getNorthing() - NORTHING_OFFSET;
  }

  @Override
  public int getZoneNumber() {
    return zone;
  }

  @Override
  public Character getLattitudeBand() {
    return getLatitudeBand();
  }

  @Override
  public Character getLatitudeBand() {
    return latitudeBand;
  }

  @Override
  public NSIndicator getNSIndicator() {
    return nsIndicator == null && getLattitudeBand()!= null
      ? calculateNSIndicatorFromLatBand(getLattitudeBand())
      : nsIndicator;
  }

  private static NSIndicator calculateNSIndicatorFromLatBand(
      @Nonnull final Character latitudeBand) {
    return utmNorthenBands.contains(latitudeBand) || upsNorthenBands.contains(latitudeBand)
        ? NORTH
        : SOUTH;
  }

  @Override
  public CoordinatePrecision getPrecision() {
    return precision;
  }

  @Override
  public double getEasting() {
    return easting;
  }

  /**
   * @param utmUpsString a UTM/UPS formatted string. e.g. {@code 10Q 123456 0123456}
   * @return an object representation of 'utmUpsString'
   * @throws ParseException when 'utmUpsString' isn't correctly formatted.
   */
  static UtmUpsCoordinate parseUtmUpsString(final String utmUpsString) throws ParseException {
    final Matcher matcher = utmUpsRegexp.matcher(utmUpsString);
    if (!matcher.matches()) {
      handleUnsuccessfulParsing(utmUpsString);
    }
    final String zoneNumber = matcher.group(ZONE_NUMBER_RE_GROUP);
    final String latitudeBandString = matcher.group(LATITUDE_BAND_RE_GROUP);
    final int easting = Integer.parseInt(matcher.group(EASTING_RE_GROUP));
    final int northing = Integer.parseInt(matcher.group(NORTHING_RE_GROUP));
    final String nsIndicatorString = matcher.group(NS_INDICATOR_RE_GROUP).trim();
    final NSIndicator nsIndicator =
        nsIndicatorString.length() > 0 ? nsIndicatorString.charAt(0) == 'N' ? NORTH : SOUTH : null;
    return fromZoneBandNorthingEastingNSI(
        zoneNumber.length() > 0 ? Integer.parseInt(zoneNumber) : 0,
        latitudeBandString.length() > 0 ? latitudeBandString.charAt(0) : null,
        easting,
        northing,
        nsIndicator);
  }

  private static void handleUnsuccessfulParsing(final String invalidInput) throws ParseException {
    throw new ParseException(
        String.format(
            "Supplied argument '%s' is not a valid UTM/UPS formatted String.", invalidInput),
        0);
  }

  @Override
  public boolean isUTM() {
    return !isUPS() || isUpsOverlapException(this);
  }

  static boolean isUpsOverlapException(final UtmUpsCoordinate candidateCoordinate) {
    // TODO:  implement overlap checking
    return false;
  }

  @Override
  public boolean isUPS() {
    return allValidUpsBands.contains(getLattitudeBand()) || isUpsOverlapException(this);
  }

  @Override
  public String toString() {
    return (zone == 0 ? "" : String.valueOf(zone))
        + (latitudeBand == null ? "" : latitudeBand)
        + " "
        + precision.format((int) easting)
        + "mE "
        + precision.format((int) northing)
        + "mN";
  }

  @Override
  public boolean equals(final Object suppliedObject) {
    return Optional.ofNullable(suppliedObject)
        .filter(UtmUpsCoordinateImpl.class::isInstance)
        .map(UtmUpsCoordinateImpl.class::cast)
        .map(
            other ->
                new EqualsBuilder()
                    .append(zone, other.zone)
                    .append(easting, other.easting)
                    .append(northing, other.northing)
                    .append(latitudeBand, other.latitudeBand)
                    .build())
        .orElse(false);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(zone)
        .append(easting)
        .append(northing)
        .append(latitudeBand)
        .build();
  }
}
