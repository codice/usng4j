package org.codice.usng4j.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import org.codice.usng4j.UtmUpsCoordinate;
import org.junit.Ignore;
import org.junit.Test;

public class UtmUpsCoordinateImplTest extends BaseClassForUsng4jTest {

  // UTM parsing tests

  @Test
  public void testParsingUtmAsUtm() {
    fail();
  }

  @Test
  public void testParsingInvalidUtm() {
    fail();
  }

  @Test
  public void testParsingUpsAsUtm() {
    fail();
  }

  @Test
  public void testParsingUtmOverlappingUpsAsUtm() {
    fail();
  }

  // UPS parsing tests

  @Test
  public void testParsingUpsAsUps() throws IOException {
    fail();
  }

  @Test
  public void testParsingInvalidUps() {
    fail();
  }

  @Test
  public void testParsingUtmAsUps() {
    fail();
  }

  @Test
  public void testParsingUpsOverlappingUtmAsUps() {
    fail();
  }

  // UTM/UPS parsing tests

  @Test
  public void testParsingUtmAsUtmUps() {
    fail();
  }

  @Test
  public void testParsingUpsAsUtmUps() {
    fail();
  }

  @Test
  public void testParsingInvalidUtmUps() {
    fail();
  }

  @Test
  public void testAlwaysFalseIsOverlappingException() {
    // TODO:  remove this test once the overlap exception functionality is implemented
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEasting(32, 'X', 425_945, 8_931_452);
    // When the overlap exception functionality is implemented the following should fail
    assertThat(UtmUpsCoordinateImpl.isUpsOverlapException(testCoordinate), is(false));
  }

  @Ignore
  @Test
  public void testParsingValidUtmOverlappingUpsAsUtmUps() {
    // This functionality is not fully implemented yet.
    // TODO:  update this test once it's implemented 35X 425945mE 8931452mN
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEasting(32, 'V', 425_945, 8_931_452);
    assertThat(testCoordinate.isUTM() && testCoordinate.isUPS(), is(true));
  }

  @Ignore
  @Test
  public void testParsingValidUpsOverlappingUtmAsUtmUps() {
    // This functionality is not fully implemented yet.
    // TODO:  update this test once it's implemented, and the test data updated from UTM to UPS
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEasting(32, 'V', 425_945, 8_931_452);
    assertThat(testCoordinate.isUTM() && testCoordinate.isUPS(), is(true));
  }
}
