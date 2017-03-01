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

package org.codice.usng;

/**
 * This interface models a point in the United States National Grid coordinate system.
 * There are several valid formats for USNG coordinates.  A fully specified coordinate is formatted
 * like this:
 *
 * {@code <zone number><latitude band letter><space><grid column><grid row><space><easting><space><northing>}
 *   e.g. 18T WL 85628 11322
 *
 * Only {@code <zone number>} and {@code <latitude band letter>} are required. The grid letters are
 * required if easting and northing values are supplied. The easting and northing values have a
 * maximum length of 5 characters each (with an optional '-').
 *
 * Default implementations of this class are immutable and therefore threadsafe.
 *
 */
public interface UsngCoordinate {

    /**
     *
     * @return the zone number of this USNG coordinate.
     */
    int getZoneNumber();

    /**
     *
     * @return the latitude band for this USNG coordinate or null if not specified.
     */
    char getLatitudeBandLetter();

    /**
     *
     * @return the grid column letter for this USNG coordinate or null if not specified.
     */
    Character getColumnLetter();

    /**
     *
     * @return the grid row letter for this USNG coordinate or null if not specified.
     */
    Character getRowLetter();

    /**
     *
     * @return the easting value for this USNG coordinate or null if not specified.
     */
    Integer getEasting();

    /**
     *
     * @return the northing for this USNG coordinate or null if not specified.
     */
    Integer getNorthing();

    /**
     *
     * @return the precision level of the supplied easting/northing values.
     */
    CoordinatePrecision getPrecision();

    /**
     *
     * @return an MGRS coordinate formatted representation of this coordinate. This is the same as
     * the USNG formatted version with spaces removed.
     */
    String toMgrsString();
}
