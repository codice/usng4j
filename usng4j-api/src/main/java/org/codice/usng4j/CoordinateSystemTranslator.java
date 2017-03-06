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

import java.text.ParseException;

/**
 * A utility for converting between coordinate systems.
 *
 * The default implementation of this class is immutable and therefore threadsafe.
 */
public interface CoordinateSystemTranslator {
    /**
     *
     * @param latLonCoordinate the bounding box to be converted.
     * @return a UsngCoordinate that represents the supplied DecimalDegreesCoordinate.
     */
    UsngCoordinate toUsng(BoundingBox latLonCoordinate);

    /**
     * Converts from decimal degrees to UTM.
     *
     * @param decimalDegreesCoordinate the Lat/Lon coordinate to be converted.
     * @return the UTM equivalent of decimalDegreesCoordinate.
     */
    UtmCoordinate toUtm(DecimalDegreesCoordinate decimalDegreesCoordinate);

    /**
     * Converts from decimal degrees to USNG.
     *
     * @param decimalDegreesCoordinate the lat/lon coordinate to be converted.
     * @return a USNG equivalent of decimalDegreesCoordinate.
     */
    UsngCoordinate toUsng(DecimalDegreesCoordinate decimalDegreesCoordinate);

    /**
     * Converts from decimal degrees to USNG.
     *
     * @param decimalDegreesCoordinate the lat/lon coordinate to be converted.
     * @param coordinatePrecision the requested precision of the returned UsngCoordinate.
     * @return a USNG equivalent of decimalDegreesCoordinate.
     */
    UsngCoordinate toUsng(DecimalDegreesCoordinate decimalDegreesCoordinate,
            CoordinatePrecision coordinatePrecision);

    /**
     * Converts from UTM to a bounding box.
     *
     * @param utmCoordinate the UTM coordinate to be converted.
     * @return the lat/lon equiavlent of utmCoordinate.
     */
    BoundingBox toBoundingBox(UtmCoordinate utmCoordinate);

    /**
     * Converts from UTM to decimal degrees.
     *
     * @param utmCoordinate
     * @return
     */
    DecimalDegreesCoordinate toLatLon(UtmCoordinate utmCoordinate);

    /**
     * Converts from USNG to UTM.
     * @param usngCoordinate the USNG coordinate to be converted.
     * @return the UTM equivalent of usngCoordinate.
     */
    UtmCoordinate toUtm(UsngCoordinate usngCoordinate);

    /**
     * Convert from USNG to lat/lon.
     * @param usngCoordinate the USNG coordinate to be converted.
     * @return the lat/lon equivalent of usngp.
     */
    DecimalDegreesCoordinate toLatLon(UsngCoordinate usngCoordinate);

    /**
     * Convert from USNG to lat/lon.
     * @param usngCoordinate the USNG coordinate to be converted.
     * @return the lat/lon equivalent of usngp.
     */
    BoundingBox toBoundingBox(UsngCoordinate usngCoordinate);

    /**
     *
     * @param utmString a UTM formatted string. e.g. {@code 10Q 123456 -0123456}
     * @return an object representation of 'utmString'
     * @throws ParseException when 'utmString' isn't correctly formatted.
     */
    UtmCoordinate parseUtmString(String utmString) throws ParseException;

    /**
     * @param usngString a properly formatted USNG string.
     * @return a fully parsed UsngCoordinate object.
     * @throws ParseException when 'usngStr' isn't in USNG format.
     */
    UsngCoordinate parseUsngString(String usngString) throws ParseException;

    /**
     * @param mgrsString a properly formatted MGRS string.
     * @return a fully parsed UsngCoordinate object.
     * @throws ParseException when 'msgrsString' isn't in MGRS format.
     */
    UsngCoordinate parseMgrsString(String mgrsString) throws ParseException;
}
