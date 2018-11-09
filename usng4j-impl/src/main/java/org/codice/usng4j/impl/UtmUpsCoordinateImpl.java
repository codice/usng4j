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

import org.codice.usng4j.CoordinatePrecision;
import org.codice.usng4j.NSIndicator;
import org.codice.usng4j.UtmUpsCoordinate;

public class UtmUpsCoordinateImpl implements UtmUpsCoordinate {

  private final int zone;
  private final Character latitudeBand;
  private final int northing;
  private final int easting;

  private UtmUpsCoordinateImpl(
      final int zone, final Character latitudeBand, final int northing, final int easting) {
    this.zone = zone;
    this.latitudeBand = latitudeBand;
    this.northing = northing;
    this.easting = easting;
  }

  public static UtmUpsCoordinate fromLatLon(final double lat, final double lon) {
    // TODO: implement
    return null;
  }

  public static UtmUpsCoordinate fromZoneBandNorthingEasting(
      final int zone, final Character latitudeBand, final int northing, final int easting) {
    return new UtmUpsCoordinateImpl(zone, latitudeBand, northing, easting);
  }

  @Override
  public double getNorthing() {
    return northing;
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
    return null;
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
        + Math.round(easting)
        + "mE "
        + Math.round(northing)
        + "mN";
  }

  @Override
  public boolean isUTM() {
    return false;
  }

  @Override
  public boolean isUPS() {
    return false;
  }
}
