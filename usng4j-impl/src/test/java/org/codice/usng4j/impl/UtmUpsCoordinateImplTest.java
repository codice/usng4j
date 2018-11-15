package org.codice.usng4j.impl;

import static org.codice.usng4j.NSIndicator.NORTH;
import static org.codice.usng4j.NSIndicator.SOUTH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.util.concurrent.ThreadLocalRandom;
import org.codice.usng4j.CoordinatePrecision;
import org.codice.usng4j.UtmCoordinate;
import org.codice.usng4j.UtmUpsCoordinate;
import org.junit.Ignore;
import org.junit.Test;

public class UtmUpsCoordinateImplTest extends BaseClassForUsng4jTest {

  // UPS/UTM instantiating tests

  @Test
  public void testCreatingValidUtmUpsCoordinateInstanceWithAllFieldsSupplied() {
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
            0,
            'Z',
            expectedTestDataSingleCoordinate.easting,
            expectedTestDataSingleCoordinate.northing,
            expectedTestDataSingleCoordinate.nsIndicator);
    testFullUpsUtmProlertiesSet(
        testCoordinate, expectedTestDataSingleCoordinate, true, false, 0, 'Z');
  }

  private static void testFullUpsUtmProlertiesSet(
      final UtmUpsCoordinate testedCoordinate,
      final UtmUpsTestData expectedData,
      final boolean expectedIsUps,
      final boolean expectedIsUtm,
      final int expectedZoneNumber,
      final Character expectedLatitudeBand) {
    assertThat(testedCoordinate.isUPS(), is(expectedIsUps));
    assertThat(testedCoordinate.isUTM(), is(expectedIsUtm));
    assertThat(testedCoordinate.getZoneNumber(), is(expectedZoneNumber));
    assertThat(testedCoordinate.getLatitudeBand(), is(expectedLatitudeBand));
    assertThat(testedCoordinate.getEasting(), is(expectedData.easting));
    assertThat(testedCoordinate.getNorthing(), is(expectedData.northing));
    assertThat(testedCoordinate.getNSIndicator(), is(expectedData.nsIndicator));
    assertThat(testedCoordinate.getPrecision(), is(CoordinatePrecision.ONE_METER));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailingCreatingUtmUpsCoordinateInstanceWithNoLatBandAndNoNSISupplied() {
    UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(0, null, 1, 1, null);
  }

  @Test
  public void testCreatingUtmUpsCoordinateInstanceWithNoLatBandAndWithNSISupplied() {
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
            0,
            null,
            expectedTestDataSingleCoordinate.easting,
            expectedTestDataSingleCoordinate.northing,
            expectedTestDataSingleCoordinate.nsIndicator);
    testFullUpsUtmProlertiesSet(
        testCoordinate, expectedTestDataSingleCoordinate, true, false, 0, null);
  }

  @Test
  public void testCreatingUtmUpsCoordinateInstanceWithLatBandAndNoNSISupplied() {
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
            0,
            'Z',
            expectedTestDataSingleCoordinate.easting,
            expectedTestDataSingleCoordinate.northing,
            null);
    testFullUpsUtmProlertiesSet(
        testCoordinate, expectedTestDataSingleCoordinate, true, false, 0, 'Z');
  }

  @Test
  public void testCreatingUtmUpsCoordinateInstanceWithLatBandAndNoNSIFactoryMethod() {
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEasting(
            0,
            'Z',
            expectedTestDataSingleCoordinate.easting,
            expectedTestDataSingleCoordinate.northing);
    testFullUpsUtmProlertiesSet(
        testCoordinate, expectedTestDataSingleCoordinate, true, false, 0, 'Z');
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailingCreatingUtmUpsCoordinateInstanceWithIllegalLatBandSupplied() {
    UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
        0,
        'I',
        expectedTestDataSingleCoordinate.easting,
        expectedTestDataSingleCoordinate.northing,
        null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailingCreatingUtmUpsCoordinateInstanceWithIllegalEastingSupplied() {
    UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
        0,
        'Z',
        0 - expectedTestDataSingleCoordinate.easting,
        expectedTestDataSingleCoordinate.northing,
        null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailingCreatingUtmUpsCoordinateInstanceWithIllegalNorthingSupplied() {
    UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
        0,
        'Z',
        expectedTestDataSingleCoordinate.easting,
        10_000_000 + expectedTestDataSingleCoordinate.northing,
        null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailingCreatingUtmUpsCoordinateInstanceWithIllegalZoneSupplied() {
    UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
        66,
        'M',
        expectedTestDataSingleCoordinate.easting,
        expectedTestDataSingleCoordinate.northing,
        null);
  }

  // UTM parsing tests
  // TODO: implement the UTM parsing tests,
  //       some of the tests ignored for now as no UTM changes were made to the library.

  @Test
  public void testParsingUtmAsUtm() throws ParseException {
    final UtmCoordinate utmCoordinateWithLatitudeBand =
        UtmUpsCoordinateImpl.parseUtmUpsString("5Q 000001 2199600");
    assertThat(utmCoordinateWithLatitudeBand.getZoneNumber(), is(5));
    assertThat(utmCoordinateWithLatitudeBand.getLatitudeBand(), is('Q'));
    assertThat(utmCoordinateWithLatitudeBand.getEasting(), is(1.0));
    assertThat(utmCoordinateWithLatitudeBand.getNorthing(), is(2199600.0));
    assertThat(utmCoordinateWithLatitudeBand.getNSIndicator(), is(SOUTH));
    assertThat(((UtmUpsCoordinate) utmCoordinateWithLatitudeBand).isUTM(), is(true));
    assertThat(utmCoordinateWithLatitudeBand.getPrecision(), is(CoordinatePrecision.ONE_METER));

    final UtmCoordinate utmCoordinateWithNSI =
        UtmUpsCoordinateImpl.parseUtmUpsString("5 000001 2199600 N");
    assertThat(utmCoordinateWithNSI.getZoneNumber(), is(5));
    assertThat(utmCoordinateWithNSI.getLatitudeBand(), is(nullValue()));
    assertThat(utmCoordinateWithNSI.getEasting(), is(1.0));
    assertThat(utmCoordinateWithNSI.getNorthing(), is(2199600.0));
    assertThat(utmCoordinateWithNSI.getNSIndicator(), is(NORTH));
    assertThat(((UtmUpsCoordinate) utmCoordinateWithNSI).isUTM(), is(true));
    assertThat(utmCoordinateWithNSI.getPrecision(), is(CoordinatePrecision.ONE_METER));
  }

  @Test(expected = ParseException.class)
  public void testParsingInvalidUtm() throws ParseException {
    UtmUpsCoordinateImpl.parseUtmUpsString("5Q");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingUpsAsUtm() throws ParseException {
    UtmUpsCoordinateImpl.parseUtmUpsString("5Z 000001 2199600");
  }

  // UPS parsing tests

  @Test
  public void testParsingUpsAsUps() throws ParseException {
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.parseUtmUpsString(expectedTestDataSingleCoordinate.upsString);
    testFullUpsUtmProlertiesSet(
        testCoordinate, expectedTestDataSingleCoordinate, true, false, 0, 'Z');
  }

  @Test(expected = ParseException.class)
  public void testParsingInvalidUps() throws ParseException {
    UtmUpsCoordinateImpl.parseUtmUpsString(
        expectedTestDataSingleCoordinate.upsString.substring(
            0, ThreadLocalRandom.current().nextInt(1, 5)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingUtmAsUps() throws ParseException {
    UtmUpsCoordinateImpl.parseUtmUpsString("L 000001 2199600");
  }

  @Test
  public void testNorthingOffset() throws ParseException {
    final UtmUpsCoordinate testCoordinateSouth =
        UtmUpsCoordinateImpl.parseUtmUpsString("5 000001 2199600 S");
    assertThat(
        testCoordinateSouth.getNorthingWithOffset(),
        is(testCoordinateSouth.getNorthing() - UtmUpsCoordinateImpl.NORTHING_OFFSET));
    final UtmUpsCoordinate testCoordinateNorth =
        UtmUpsCoordinateImpl.parseUtmUpsString("5 000001 2199600 N");
    assertThat(testCoordinateNorth.getNorthingWithOffset(), is(testCoordinateNorth.getNorthing()));
  }

  // Object overrides tests

  @Test
  public void testEquals() throws ParseException {
    final UtmUpsCoordinate testCoordinateOne =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
            0,
            'Z',
            expectedTestDataSingleCoordinate.easting,
            expectedTestDataSingleCoordinate.northing,
            null);
    final UtmUpsCoordinate testCoordinateTwo =
        UtmUpsCoordinateImpl.parseUtmUpsString(expectedTestDataSingleCoordinate.upsString);
    assertThat(testCoordinateOne, is(testCoordinateTwo));
  }

  @Test
  public void testHashcode() throws ParseException {
    final UtmUpsCoordinate testCoordinateOne =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEastingNSI(
            0,
            'Z',
            expectedTestDataSingleCoordinate.easting,
            expectedTestDataSingleCoordinate.northing,
            null);
    final UtmUpsCoordinate testCoordinateTwo =
        UtmUpsCoordinateImpl.parseUtmUpsString(expectedTestDataSingleCoordinate.upsString);
    assertThat(testCoordinateOne.hashCode(), is(testCoordinateTwo.hashCode()));
  }

  @Ignore("FUNCTIONALITY NOT IMPLEMENTED")
  @Test
  public void testParsingValidOverlappingUpsAsUtmUps() {
    // This functionality is not fully implemented yet.
    // TODO:  update this test once it's implemented 35X 425945mE 8931452mN
    final UtmUpsCoordinate testCoordinate =
        UtmUpsCoordinateImpl.fromZoneBandNorthingEasting(32, 'V', 425_945, 8_931_452);
    assertThat(testCoordinate.isUTM() && testCoordinate.isUPS(), is(true));
  }
}
