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
 * Represents a coordinate that falls in either UTM or UPS space.
 *
 * <p>The UTM and UPS systems together cover the entire globe, but there does exist some overlap.
 * The purpose of this interface is to help the client choose what format receives priority when
 * input data falls into the shared space.
 *
 * <p>Thus it is possible for {@code isUTM() && isUPS()} to evaluate to {@code true}.
 */
public interface UtmUpsCoordinate extends UtmCoordinate, UpsCoordinate {
  /**
   * Determines if the current coordinate data is described in terms of UTM (where the zone number is positive).
   *
   * @return true if the data is in UTM space, false otherwise.
   */
  boolean isUTM();

  /**
   * Determines if the current coordinate data is described in terms of UPS (where the zone number is 0 and the latitude band is 'A', 'B', 'Y', or 'Z').
   *
   * @return true if the data is in UPS space, false otherwise.
   */
  boolean isUPS();
}
