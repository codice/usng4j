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

package org.codice.usng4j;

import java.text.NumberFormat;

/**
 * This enum represents the valid precision ranges that can be specified in the Unites States Grid
 * System.
 */
public enum CoordinatePrecision {
  SIX_BY_EIGHT_DEGREES(-1),
  ONE_HUNDRED_KILOMETERS(0),
  TEN_KILOMETERS(1),
  ONE_KILOMETER(2),
  ONE_HUNDRED_METERS(3),
  TEN_METERS(4),
  ONE_METER(5);

  private int precisionValue;

  private NumberFormat numberFormat;

  private CoordinatePrecision(int precisionValue) {
    this.precisionValue = precisionValue;

    if (precisionValue > 0) {
      this.numberFormat = NumberFormat.getIntegerInstance();
      this.numberFormat.setGroupingUsed(false);
      this.numberFormat.setMinimumIntegerDigits(precisionValue);
    }
  }

  public String format(int value) {
    if (numberFormat != null) {
      return numberFormat.format(value);
    }

    return "";
  }

  public int getIntValue() {
    return this.precisionValue;
  }

  public static CoordinatePrecision forEastNorth(String easting, String northing) {
    int maxLength = Math.max(easting.trim().length(), northing.trim().length());

    if (maxLength > 4) {
      return CoordinatePrecision.ONE_METER;
    } else if (maxLength > 3) {
      return CoordinatePrecision.TEN_METERS;
    } else if (maxLength > 2) {
      return CoordinatePrecision.ONE_HUNDRED_METERS;
    } else if (maxLength > 1) {
      return CoordinatePrecision.ONE_KILOMETER;
    }

    return CoordinatePrecision.TEN_KILOMETERS;
  }
}
