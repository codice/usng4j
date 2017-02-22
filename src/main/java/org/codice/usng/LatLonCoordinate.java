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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class is a representation of a point on the globe represented by latitude/longitude in
 * decimal degrees.
 *
 * Instances of this class are immutable and therefore threadsafe.
 *
 */
public final class LatLonCoordinate {
    private double lat;

    private double lon;

    /**
     *
     * @param lat the latitude value for this geographic point.
     * @param lon the longitude value fo this geographic point.
     */
    public LatLonCoordinate(final double lat, final double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     *
     * @return the latitude value for this geographic point.
     */
    public double getLat() {
        return lat;
    }

    /**
     *
     * @return the longitude value fo this geographic point.
     */
    public double getLon() {
        return lon;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("lat", this.lat)
                .append("lon", this.lon)
                .toString();
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof UsngCoordinate)) {
            return false;
        }

        LatLonCoordinate other = (LatLonCoordinate) object;

        return new EqualsBuilder()
                .append(this.lat, other.lat)
                .append(this.lon, other.lon)
                .build();
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.lat)
                .append(this.lon)
                .build();
    }
}
