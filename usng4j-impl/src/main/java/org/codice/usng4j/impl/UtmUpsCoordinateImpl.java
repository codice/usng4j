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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.codice.usng4j.CoordinatePrecision;
import org.codice.usng4j.NSIndicator;
import org.codice.usng4j.UtmUpsCoordinate;

public class UtmUpsCoordinateImpl implements UtmUpsCoordinate {

  public static final double NORTHING_OFFSET = 10_000_000; // (meters)
  private static final Set<Character> upsNorthenBands = new HashSet<>(Arrays.asList('Y', 'Z'));
  private static final Set<Character> upsSothernBands = new HashSet<>(Arrays.asList('A', 'B'));
  private static final Set<Character> utmNorthenBands =
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
    allValidUtmBands = unifiedBandSet(utmNorthenBands, upsSothernBands);
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
      final Character latitudeBand,
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

  static UtmUpsCoordinate fromZoneBandNorthingEastingNSI(
      final int zone,
      final Character latitudeBand,
      final double easting,
      final double northing,
      @Nullable final NSIndicator nsIndicator) {
    return fromZoneBandNorthingEastingNSIIfPossible(
            zone, latitudeBand, easting, northing, nsIndicator)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    new UtmUpsCoordinateImpl(zone, latitudeBand, easting, northing, nsIndicator)
                        + " is neither UTM nor UPS coordinate"));
  }

  static Optional<UtmUpsCoordinate> fromZoneBandNorthingEastingNSIIfPossible(
      final int zone,
      final Character latitudeBand,
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
                coordinate.getLattitudeBand() != null || coordinate.getNSIndicator() != null)
        .filter(coordinate -> coordinate.getEasting() >= 0)
        .filter(coordinate -> coordinate.getEasting() <= 3_200_000)
        .filter(coordinate -> coordinate.getNorthing() >= 0)
        .filter(coordinate -> coordinate.getNorthing() <= 10_000_000)
        .filter(
            (coordinate ->
                (coordinate.getZoneNumber() == 0
                        && allAcceptableUpsBands.contains(coordinate.getLattitudeBand())
                    || (coordinate.getZoneNumber() >= 1 && coordinate.getZoneNumber() <= 60)
                        && allAcceptableUtmBands.contains(coordinate.getLattitudeBand()))));
  }

  @SafeVarargs
  private static Set<Character> unifiedBandSet(final Set<Character>... bandSets) {
    return Stream.of(bandSets).flatMap(Collection::stream).collect(Collectors.toSet());
  }

  public static UtmUpsCoordinate fromZoneBandNorthingEasting(
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
                || utmNorthenBands.contains(getLattitudeBand()))
        ? getNorthing()
        : getNorthing() - NORTHING_OFFSET;
  }

  @Override
  public int getZoneNumber() {
    return zone;
  }

  @Override
  public Character getLattitudeBand() {
    return latitudeBand;
  }

  @Override
  public NSIndicator getNSIndicator() {
    return null;
  }

  @Override
  public CoordinatePrecision getPrecision() {
    return precision;
  }

  @Override
  public double getEasting() {
    return easting;
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
  public boolean isUTM() {
    // TODO implement
    return false;
  }

  @Override
  public boolean isUPS() {
    // TODO implement
    return false;
  }
}
