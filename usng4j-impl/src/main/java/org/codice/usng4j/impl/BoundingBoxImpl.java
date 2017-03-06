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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codice.usng4j.BoundingBox;

/**
 * {@inheritDoc}
 */
final class BoundingBoxImpl implements BoundingBox {
    private double north;

    private double south;

    private double east;

    private double west;

    /**
     *
     * @param north the northern line of latitude for this bounding box.
     * @param south ths southern line of latitude for this bounding box.
     * @param east the eastern line of longitude for this bounding box.
     * @param west the western line of longitude for this bounding box.
     */
    BoundingBoxImpl(final double north, final double south, final double east, final double west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNorth() {
        return this.north;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSouth() {
        return this.south;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEast() {
        return this.east;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWest() {
        return this.west;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("north", this.north)
                .append("south", this.south)
                .append("east", this.east)
                .append("west", this.west)
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

        if (!(object instanceof UsngCoordinateImpl)) {
            return false;
        }

        BoundingBox other = (BoundingBox) object;

        return new EqualsBuilder()
                .append(this.north, other.getNorth())
                .append(this.south, other.getSouth())
                .append(this.east, other.getEast())
                .append(this.west, other.getWest())
                .build();
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.north)
                .append(this.south)
                .append(this.east)
                .append(this.west)
                .build();
    }
}
