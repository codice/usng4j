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

/**
 * This interface models a point in the Universal Transverse Mercator coordinate system.
 * There are two valid formats for UTM coordinates.  Those are:
 *
 * {@code <zone number><latitude band letter><space><easting><space><northing>}
 *   e.g. 10Q -204832 302043
 *
 * or
 *
 * {@code <zone number><space><easting><space><northing>}
 *   e.g. 10 -204832 302043
 *
 * The default implementation of this class are immutable and therefore threadsafe.
 *
 */
public interface UtmCoordinate {
    /**
     *
     * @return the easting value of this UTM coordinate.
     */
    double getEasting();

    /**
     *
     * @return the northing value of this UTM coordinate.
     */
    double getNorthing();

    /**
     *
     * @return the zone number of this UTM coordinate.
     */
    int getZoneNumber();

    /**
     *
     * @return the latitude band for this UTM coordinate or null if not specified.
     */
    Character getLattitudeBand();

    /**
     *
     * @return the precision level of the supplied easting/northing values.
     */
    CoordinatePrecision getPrecision();
}
