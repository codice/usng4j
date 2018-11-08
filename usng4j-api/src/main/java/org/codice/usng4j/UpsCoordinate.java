/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.usng4j;

/**
 * This interface models a point in the Universal Polar Stereographic coordinate system. The format
 * of a UPS coordinate is similar to a UTM coordinate, but without the zone number and the band
 * letter is limited to A, B, Y, or Z.
 *
 * <p>{@code <latitude band letter><space><easting><space><northing>} e.g. B 2029070mE 2554696mN
 */
public interface UpsCoordinate extends UtmUpsCapabilities {}
