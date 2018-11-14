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
 * This interface models the operations that can be performed on both a Universal Transverse
 * Mercator coordinate and a Universal Polar Stereographic coordinate.
 */
interface UtmUpsCapabilities {
  /** @return the easting value of this UTM coordinate. */
  double getEasting();

  /** @return the northing value of this UTM coordinate. */
  double getNorthing();

  /** @return the northing value with the NORTHING_OFFSET subtracted if NSIndicator equals SOUTH */
  double getNorthingWithOffset();

  /** @return the zone number of this UTM coordinate. */
  int getZoneNumber();

  /** @return the latitude band for this UTM coordinate or null if not specified. */
  @Deprecated
  Character getLattitudeBand();

  /** @return the latitude band for this UTM coordinate or null if not specified. */
  Character getLatitudeBand();

  /** @return the N/S indicator if using northing values with hemisphere indicator */
  NSIndicator getNSIndicator();

  /** @return the precision level of the supplied easting/northing values. */
  CoordinatePrecision getPrecision();
}
