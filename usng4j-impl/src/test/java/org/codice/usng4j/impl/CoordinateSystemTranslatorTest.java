package org.codice.usng4j.impl;

import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static org.codice.usng4j.impl.CoordinateSystemTranslatorImpl.NORTHING_OFFSET;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Optional;

import org.codice.usng4j.BoundingBox;
import org.codice.usng4j.CoordinatePrecision;
import org.codice.usng4j.DecimalDegreesCoordinate;
import org.codice.usng4j.NSIndicator;
import org.codice.usng4j.UpsCoordinate;
import org.codice.usng4j.UsngCoordinate;
import org.codice.usng4j.UtmCoordinate;
import org.codice.usng4j.UtmUpsCoordinate;
import org.junit.Test;

public class CoordinateSystemTranslatorTest extends BaseClassForUsng4jTest {
  private static final double LAT_LON_DEVIATION = 0.5;

  private CoordinateSystemTranslatorImpl coordinateSystemTranslator =
      new CoordinateSystemTranslatorImpl(true);

  @Test
  public void testGetZoneNumber() {
    // around Arizona in the United States
    assertEquals(12, coordinateSystemTranslator.getZoneNumber(34, -111));
    // around Prescott/Chino Valley in Arizona
    assertEquals(12, coordinateSystemTranslator.getZoneNumber(34.5, -112.5));
    // immediately around Prescott city in Arizona
    assertEquals(12, coordinateSystemTranslator.getZoneNumber(34.545, -112.465));
    // around Uruguay
    assertEquals(21, coordinateSystemTranslator.getZoneNumber(-32.5, -55.5));
    // around Buenos Aires city in Argentina
    assertEquals(21, coordinateSystemTranslator.getZoneNumber(-34.5, -58.5));
    // around Merlo town in Buenos Aires
    assertEquals(21, coordinateSystemTranslator.getZoneNumber(-34.66, -58.73));
    // around Madagascar
    assertEquals(38, coordinateSystemTranslator.getZoneNumber(-18.5, 46.5));
    // around Toliara city in Madagascar
    assertEquals(38, coordinateSystemTranslator.getZoneNumber(-22.5, 43.5));
    // around Toliara city center in Madagascar
    assertEquals(38, coordinateSystemTranslator.getZoneNumber(-23.355, 43.67));
    // around Central Japan
    assertEquals(54, coordinateSystemTranslator.getZoneNumber(37, 140.5));
    // around Tokyo city in Japan
    assertEquals(54, coordinateSystemTranslator.getZoneNumber(35.5, 139.5));
    // around Tokyo city center in Japan
    assertEquals(54, coordinateSystemTranslator.getZoneNumber(35.69, 139.77));
    // around the international date line
    assertEquals(60, coordinateSystemTranslator.getZoneNumber(28, 179));
    // to the immediate east
    assertEquals(1, coordinateSystemTranslator.getZoneNumber(28, -179));
    // with midpoint directly on it (-180)
    assertEquals(1, coordinateSystemTranslator.getZoneNumber(28, -180));
    // with midpoint directly on it (+180)
    assertEquals(1, coordinateSystemTranslator.getZoneNumber(28, 180));
    // around the equator
    assertEquals(54, coordinateSystemTranslator.getZoneNumber(1, 141));
    // to the immediate south
    assertEquals(54, coordinateSystemTranslator.getZoneNumber(-1, 141));
    // with midpoint directly on it
    assertEquals(54, coordinateSystemTranslator.getZoneNumber(0, 141));
    // around the international date line and equator
    assertEquals(60, coordinateSystemTranslator.getZoneNumber(1, 179));
    // to the immediate west and south
    assertEquals(60, coordinateSystemTranslator.getZoneNumber(-1, 179));
    // to the immediate east and north
    assertEquals(1, coordinateSystemTranslator.getZoneNumber(1, -179));
    // to the immediate east and south
    assertEquals(1, coordinateSystemTranslator.getZoneNumber(-1, -179));
    // with midpoint directly on it (0, -180)
    assertEquals(1, coordinateSystemTranslator.getZoneNumber(0, -180));
    // with midpoint directly on it (0, +180)
    assertEquals(1, coordinateSystemTranslator.getZoneNumber(0, 180));
  }

  @Test
  public void testGetZoneLetterFromLat() {
    // around Arizona in the United States
    assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(34));
    // around Prescott/Chino Valley in Arizona
    assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(34.5));
    // immediately around Prescott city in Arizona
    assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(34.545));
    // around Uruguay
    assertEquals("H", coordinateSystemTranslator.getUtmLetterDesignator(-32.5));
    // around Buenos Aires city in Argentina
    assertEquals("H", coordinateSystemTranslator.getUtmLetterDesignator(-34.5));
    // around Merlo town in Buenos Aires
    assertEquals("H", coordinateSystemTranslator.getUtmLetterDesignator(-34.66));
    // around Madagascar
    assertEquals("K", coordinateSystemTranslator.getUtmLetterDesignator(-18.5));
    // around Toliara city in Madagascar
    assertEquals("K", coordinateSystemTranslator.getUtmLetterDesignator(-22.5));
    // around Toliara city center in Madagascar
    assertEquals("K", coordinateSystemTranslator.getUtmLetterDesignator(-23.355));
    // around Central Japan
    assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(37));
    // around Tokyo city in Japan
    assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(35.5));
    // around Tokyo city center in Japan
    assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(35.69));
    // around the equator
    assertEquals("N", coordinateSystemTranslator.getUtmLetterDesignator(1));
    // to the immediate south
    assertEquals("M", coordinateSystemTranslator.getUtmLetterDesignator(-1));
    // with midpoint directly on it
    assertEquals("N", coordinateSystemTranslator.getUtmLetterDesignator(0));
    // imediately south of north polar maximum
    assertEquals("X", coordinateSystemTranslator.getUtmLetterDesignator(83));
    // imediately north of north polar maximum
    assertEquals("Z", coordinateSystemTranslator.getUtmLetterDesignator(85));
    // directly on north polar maximum
    assertEquals("X", coordinateSystemTranslator.getUtmLetterDesignator(84));
    // imediately north of south polar minimum
    assertEquals("C", coordinateSystemTranslator.getUtmLetterDesignator(-79));
    // imediately south of south polar minimum
    assertEquals("Z", coordinateSystemTranslator.getUtmLetterDesignator(-81));
    // directly on south polar minimum
    assertEquals("C", coordinateSystemTranslator.getUtmLetterDesignator(-80));
  }

  @Test
  public void testParseUsng() throws ParseException {
    // should return zone=5; letter=Q
    UsngCoordinate parts = UsngCoordinateImpl.parseUsngString("5Q");
    assertEquals(5, parts.getZoneNumber());
    assertEquals('Q', parts.getLatitudeBandLetter());

    // should return zone=12; letter=S
    parts = UsngCoordinateImpl.parseUsngString("12S");
    assertEquals(12, parts.getZoneNumber());
    assertEquals('S', parts.getLatitudeBandLetter());

    // should return zone=5; letter=Q; square1=K; square2=B

    parts = UsngCoordinateImpl.parseUsngString("5Q KB");
    assertEquals(5, parts.getZoneNumber());
    assertEquals('Q', parts.getLatitudeBandLetter());
    assertEquals('K', parts.getColumnLetter().charValue());
    assertEquals('B', parts.getRowLetter().charValue());

    // should return zone=12; letter=S; square1=V; square2=C

    parts = UsngCoordinateImpl.parseUsngString("12S VC");
    assertEquals(12, parts.getZoneNumber());
    assertEquals('S', parts.getLatitudeBandLetter());
    assertEquals('V', parts.getColumnLetter().charValue());
    assertEquals('C', parts.getRowLetter().charValue());

    // should return zone=5; letter=Q; square1=K; square2=B; easting=42785; northing=31517

    parts = UsngCoordinateImpl.parseUsngString("5Q KB 42785 31517");
    assertEquals(5, parts.getZoneNumber());
    assertEquals('Q', parts.getLatitudeBandLetter());
    assertEquals('K', parts.getColumnLetter().charValue());
    assertEquals('B', parts.getRowLetter().charValue());
    assertEquals(5, parts.getPrecision().getIntValue());
    assertEquals(42785.0, parts.getEasting(), 0);
    assertEquals(31517.0, parts.getNorthing(), 0);

    // should return zone=12; letter=S; square1=V; square2=C; easting=12900; northing=43292

    parts = UsngCoordinateImpl.parseUsngString("12S VC 12900 43292");
    assertEquals(12, parts.getZoneNumber());
    assertEquals('S', parts.getLatitudeBandLetter());
    assertEquals('V', parts.getColumnLetter().charValue());
    assertEquals('C', parts.getRowLetter().charValue());
    assertEquals(5, parts.getPrecision().getIntValue());
    assertEquals(12900, parts.getEasting(), 0);
    assertEquals(43292, parts.getNorthing(), 0);
  }

  @Test
  public void testParseMgrs() throws ParseException {
    // should return zone=5; letter=Q
    UsngCoordinate parts = UsngCoordinateImpl.parseMgrsString("5Q");
    assertEquals(5, parts.getZoneNumber());
    assertEquals('Q', parts.getLatitudeBandLetter());

    // should return zone=12; letter=S
    parts = UsngCoordinateImpl.parseMgrsString("12S");
    assertEquals(12, parts.getZoneNumber());
    assertEquals('S', parts.getLatitudeBandLetter());

    // should return zone=5; letter=Q; square1=K; square2=B

    parts = UsngCoordinateImpl.parseMgrsString("5QKB");
    assertEquals(5, parts.getZoneNumber());
    assertEquals('Q', parts.getLatitudeBandLetter());
    assertEquals('K', parts.getColumnLetter().charValue());
    assertEquals('B', parts.getRowLetter().charValue());

    // should return zone=12; letter=S; square1=V; square2=C

    parts = UsngCoordinateImpl.parseMgrsString("12SVC");
    assertEquals(12, parts.getZoneNumber());
    assertEquals('S', parts.getLatitudeBandLetter());
    assertEquals('V', parts.getColumnLetter().charValue());
    assertEquals('C', parts.getRowLetter().charValue());

    // should return zone=5; letter=Q; square1=K; square2=B; easting=42785; northing=31517

    parts = UsngCoordinateImpl.parseMgrsString("5QKB4278531517");
    assertEquals(5, parts.getZoneNumber());
    assertEquals('Q', parts.getLatitudeBandLetter());
    assertEquals('K', parts.getColumnLetter().charValue());
    assertEquals('B', parts.getRowLetter().charValue());
    assertEquals(5, parts.getPrecision().getIntValue());
    assertEquals(42785.0, parts.getEasting(), 0);
    assertEquals(31517.0, parts.getNorthing(), 0);

    // should return zone=12; letter=S; square1=V; square2=C; easting=12900; northing=43292

    parts = UsngCoordinateImpl.parseMgrsString("12SVC1290043292");
    assertEquals(12, parts.getZoneNumber());
    assertEquals('S', parts.getLatitudeBandLetter());
    assertEquals('V', parts.getColumnLetter().charValue());
    assertEquals('C', parts.getRowLetter().charValue());
    assertEquals(5, parts.getPrecision().getIntValue());
    assertEquals(12900, parts.getEasting(), 0);
    assertEquals(43292, parts.getNorthing(), 0);
  }

  @Test
  public void testParseUtm() throws ParseException {
    // should return zone=5; letter=null; easting=00001; northing=2199600;
    // nsIndicator=NSIndicator.NORTH
    String utmCoordinateString = "5 000001 2199600 N";
    UtmCoordinate utmCoordinate = coordinateSystemTranslator.parseUtmString(utmCoordinateString);
    assertEquals(5, utmCoordinate.getZoneNumber());
    assertNull(utmCoordinate.getLattitudeBand());
    assertEquals(1, utmCoordinate.getEasting(), 0);
    assertEquals(NSIndicator.NORTH, utmCoordinate.getNSIndicator());
    assertEquals(2199600.0, utmCoordinate.getNorthing(), 0);
    assertEquals(CoordinatePrecision.ONE_METER, utmCoordinate.getPrecision());

    // should return zone=5; letter=Q; easting=-00001; northing=2199600; nsIndicator=null
    utmCoordinateString = "5Q 000001 2199600";
    utmCoordinate = coordinateSystemTranslator.parseUtmString(utmCoordinateString);
    assertEquals(5, utmCoordinate.getZoneNumber());
    assertEquals('Q', utmCoordinate.getLattitudeBand().charValue());
    assertEquals(1, utmCoordinate.getEasting(), 0);
    assertEquals(2199600.0, utmCoordinate.getNorthing(), 0);
    assertEquals(CoordinatePrecision.ONE_METER, utmCoordinate.getPrecision());
  }

  @Test(expected = ParseException.class)
  public void testParseUtmInvalid() throws ParseException {
    UtmCoordinateImpl.parseUtmString("5Q");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseUtmBadInput() throws IllegalArgumentException, ParseException {
    coordinateSystemTranslator.parseUtmString("5 000001 2199600");
  }

  @Test
  public void testConvertUsngtoUtm() {
    // with single digit zone
    // should return north=2131517; east=242785; zone=5; letter=Q
    int zone = 18;
    char letter = 'S';
    char sq1 = 'U';
    char sq2 = 'J';
    int easting = 23487;
    int northing = 6483;
    UtmCoordinate coords =
        coordinateSystemTranslator.toUtm(
            new UsngCoordinateImpl(zone, letter, sq1, sq2, easting, northing));
    assertEquals(4306483, Math.floor(coords.getNorthing()), 0);
    assertEquals(323487, Math.floor(coords.getEasting()), 0);
    assertEquals(18, coords.getZoneNumber());
    assertEquals('S', coords.getLattitudeBand().charValue());

    // with two digit zone
    // should return north=43292; east=12900; zone=12; letter=S
    zone = 12;
    letter = 'S';
    sq1 = 'V';
    sq2 = 'C';
    easting = 12900;
    northing = 43292;
    coords =
        coordinateSystemTranslator.toUtm(
            new UsngCoordinateImpl(zone, letter, sq1, sq2, easting, northing));
    assertEquals(3743292, Math.floor(coords.getNorthing()), 0);
    assertEquals(412900, Math.floor(coords.getEasting()), 0);
    assertEquals(12, coords.getZoneNumber());
    assertEquals('S', coords.getLattitudeBand().charValue());
  }

  @Test
  public void testConvertUtmToLatLon() {
    // with single digit zone and specifying accuracy
    int northing = 42785;
    int easting = 131517;
    int zone = 5;
    int accuracy = 100000;
    UtmCoordinate utmCoordinate = new UtmCoordinateImpl(zone, easting, northing);
    BoundingBox boundingBox = coordinateSystemTranslator.toBoundingBox(utmCoordinate, accuracy);
    assertEquals(1, Math.floor(boundingBox.getNorth()), 0);
    assertEquals(-156, Math.floor(boundingBox.getEast()), 0);
    assertEquals(0, Math.floor(boundingBox.getSouth()), 0);
    assertEquals(-157, Math.floor(boundingBox.getWest()), 0);

    // should return lat=0; east=-158
    northing = 42785;
    easting = 131517;
    zone = 5;
    utmCoordinate = new UtmCoordinateImpl(zone, easting, northing);
    DecimalDegreesCoordinate latLon = coordinateSystemTranslator.toLatLon(utmCoordinate);
    assertEquals(0, Math.floor(latLon.getLat()), 0);
    assertEquals(-157, Math.floor(latLon.getLon()), 0);

    // should return north=1; east=-115; south=0; west=-116
    northing = 12900;
    easting = 43292;
    zone = 12;
    accuracy = 100000;
    utmCoordinate = new UtmCoordinateImpl(zone, easting, northing);
    boundingBox = coordinateSystemTranslator.toBoundingBox(utmCoordinate, accuracy);
    assertEquals(1, Math.floor(boundingBox.getNorth()), 0);
    assertEquals(-115, Math.floor(boundingBox.getEast()), 0);
    assertEquals(0, Math.floor(boundingBox.getSouth()), 0);
    assertEquals(-116, Math.floor(boundingBox.getWest()), 0);

    // should return lat=0; lon=-116
    northing = 12900;
    easting = 43292;
    zone = 12;
    utmCoordinate = new UtmCoordinateImpl(zone, easting, northing);
    latLon = coordinateSystemTranslator.toLatLon(utmCoordinate);
    assertEquals(0, Math.floor(latLon.getLat()), 0);
    assertEquals(-116, Math.floor(latLon.getLon()), 0);

    // should return lat=-35; lon=-59
    northing = 6168016;
    easting = 341475;
    zone = 21;
    utmCoordinate = new UtmCoordinateImpl(zone, easting, northing - NORTHING_OFFSET);
    latLon = coordinateSystemTranslator.toLatLon(utmCoordinate);
    assertEquals(-35.0, Math.floor(latLon.getLat()), 0);
    assertEquals(-59.0, Math.floor(latLon.getLon()), 0);

    northing = 6168016;
    easting = 341475;
    zone = 21;
    utmCoordinate = new UtmCoordinateImpl(zone, easting, northing, NSIndicator.SOUTH);
    latLon = coordinateSystemTranslator.toLatLon(utmCoordinate);
    assertEquals(-35.0, Math.floor(latLon.getLat()), 0);
    assertEquals(-59.0, Math.floor(latLon.getLon()), 0);
  }

  private void assertLatOrLonIsClose(final boolean latitude, final double actual, final double expected) throws AssertionError {
    final String latOrLonCapitalized = latitude ? "Latitude" : "Longitude";
    final int maxAbsValue = latitude ? 90 : 180;

    final String withinMaxError = String.format("%ss must be <= %d!", latOrLonCapitalized, maxAbsValue);
    assertThat(withinMaxError, actual <= maxAbsValue, is(true));

    final String withinMinError = String.format("%ss must be >= -%d!", latOrLonCapitalized, maxAbsValue);
    assertThat(withinMinError, actual >= -maxAbsValue, is(true));

    final String nearExpectedError = String.format(latOrLonCapitalized + " %f must be within %f of %f!", actual, LAT_LON_DEVIATION, expected);
    assertThat(nearExpectedError, actual <= expected + LAT_LON_DEVIATION && actual >= expected - LAT_LON_DEVIATION, is(true));
  }

  private void testConvertValidUpsToLatLon(UtmUpsTestData testData) throws AssertionError {
    final char upsZone = testData.upsString.charAt(0);
    final Optional<UtmUpsCoordinate> utmUpsCoordinateOptional =
        UtmUpsCoordinateImpl.fromZoneBandEastingNorthingNSIIfPossible(
            0 /* UPS zone = 0 */,
            upsZone,
            testData.easting,
            testData.northing,
            testData.nsIndicator);
    if (!utmUpsCoordinateOptional.isPresent()) {
      fail(
          String.format(
              "The UPS coordinate with band='%c', northing=%f, easting=%f, and north/south "
                  + "indicator=%s could not be constructed!",
              upsZone,
              testData.northing,
              testData.easting,
              testData.nsIndicator));
    }
    final UtmUpsCoordinate utmUpsCoordinate = utmUpsCoordinateOptional.get();

    final DecimalDegreesCoordinate latLon = coordinateSystemTranslator.toLatLon((UpsCoordinate) utmUpsCoordinate);
    assertLatOrLonIsClose(true /* latitude */, latLon.getLat(), testData.latitude);
    if (Math.abs(testData.latitude) != 90.0) {
      assertLatOrLonIsClose(false /* longitude */, latLon.getLon(), testData.longitude);
    }
  }

  @Test
  public void testConvertAllValidUpsToLatLon() {
    final String upsConversionError =
        validUpsCoordinatesTests
            .stream()
            .map(
                upsTestData -> {
                  try {
                    testConvertValidUpsToLatLon(upsTestData);
                    return Optional.<String>empty();
                  } catch (AssertionError assertionFailure) {
                    return of(assertionFailure.getMessage());
                  }
                })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(joining("\n"));
    if (!upsConversionError.isEmpty()) {
      fail("Some valid UPS coordinates were not correctly converted to lat-lon: " + upsConversionError);
    }
  }

  @Test
  public void testConvertUsngToLatLon() throws ParseException {
    // should return north=19; east=-155; south=19; west=-155
    UsngCoordinate usng = UsngCoordinateImpl.parseUsngString("5Q KB 42785 31517");
    BoundingBox boundingBox = coordinateSystemTranslator.toBoundingBox(usng);
    assertEquals(19, Math.floor(boundingBox.getNorth()), 0);
    assertEquals(-156, Math.floor(boundingBox.getEast()), 0);
    assertEquals(19, Math.floor(boundingBox.getSouth()), 0);
    assertEquals(-156, Math.floor(boundingBox.getWest()), 0);

    // should return north=33; east=-111; south=33; west=-111
    usng = UsngCoordinateImpl.parseUsngString("12S VC 12900 43292");
    boundingBox = coordinateSystemTranslator.toBoundingBox(usng);
    assertEquals(33, Math.floor(boundingBox.getNorth()), 0);
    assertEquals(-112, Math.floor(boundingBox.getEast()), 0);
    assertEquals(33, Math.floor(boundingBox.getSouth()), 0);
    assertEquals(-112, Math.floor(boundingBox.getWest()), 0);
  }

  @Test
  public void testConvertBoundingBoxToUsng() throws ParseException {
    // should return 12S
    String usngString = "12S";
    UsngCoordinate usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(37, 31, -108, -114)));

    // should return 12S UD
    usngString = "12S UD";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34.55, 34.45, -112.4, -112.3)));

    // should return 12S UD 7 1
    usngString = "12S UD 7 1";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34.50, 34.45, -112.4, -112.4)));

    // should return 12S UD 65 24
    usngString = "12S UD 65 24";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34.55, 34.55, -112.465, -112.47)));

    // should return 12S UD 649 241
    usngString = "12S UD 649 241";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34.55, 34.55, -112.471, -112.472)));

    // should return 12S UD 6494 2412
    usngString = "12S UD 6494 2412";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(
            new BoundingBoxImpl(34.55, 34.55, -112.47200, -112.47190)));

    // should return 12S UD 649 241
    usngString = "12S UD 64941 24126";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(
            new BoundingBoxImpl(34.55, 34.55, -112.47200, -112.47199)));

    // should return 21H
    usngString = "21H";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-30, -35, -53, -58)));

    // should return 21H UB
    usngString = "21H UB";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-34.5, -35, -58.5, -58.5)));

    // should return 21H UB 41 63
    usngString = "21H UB 41 63";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-34.665, -34.66, -58.73, -58.73)));

    // should return 38K
    usngString = "38K";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-11, -26, 51, 42)));

    // should return 38K LA
    usngString = "38K LA";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-21.9, -22, 43.7, 43.6)));

    // should return 38K LA
    usngString = "38K LA 6 6";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-22, -22, 43.7, 43.65)));

    // should return 38K LV 64 17
    usngString = "38K LV 66 12";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-23.395, -23.39, 43.70, 43.695)));

    // should return 54S
    usngString = "54S";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(41, 33, 143, 138)));

    // should return 54S UD
    usngString = "54S UD";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(35, 35, 140, 139)));

    // should return 54S UE 41 63
    usngString = "54S UE 86 51";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(35.7, 35.7, 139.75, 139.745)));
    // should return 60R
    usngString = "60R";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34, 23, 179, 172)));

    // should return 1R
    usngString = "1R";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34, 23, -179, -172)));

    // should return 1R BM
    usngString = "1R BM";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(28, 28, 179.9, -179.9)));

    // should return 58N
    usngString = "58N";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(8, 1, 166, 159)));

    // should return 58M
    usngString = "58M";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-1, -8, 166, 159)));

    // should return 58N
    usngString = "58N";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(8, -8, 166, 159)));

    // should return 60N
    usngString = "60N";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(8, 1, 179, 172)));

    // should return 60M
    usngString = "60M";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-1, -8, 179, 172)));

    // should return 1N
    usngString = "1N";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(8, 1, -179, -172)));

    // should return 1M
    usngString = "1M";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-1, -8, -179, -172)));

    // should return 1N AA
    usngString = "1N AA";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate,
        coordinateSystemTranslator.toUsng(new BoundingBoxImpl(0, 0, -179.9, 179.9)));

    // should return 30R
    usngString = "30R";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34, 23, -1, -8)));

    // should return 31R
    usngString = "31R";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34, 23, 1, 8)));

    // should return 31R
    usngString = "31R";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(34, 23, -1, 1)));

    // should return 30M
    usngString = "30M";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-1, -8, -1, -8)));

    // should return 31N
    usngString = "31N";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(8, 1, 8, 1)));

    // should return 31M
    usngString = "31M";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(-1, -8, 8, 1)));

    // should return 31N
    usngString = "31N";
    usngCoordinate = UsngCoordinateImpl.parseUsngString(usngString);
    assertEquals(
        usngCoordinate, coordinateSystemTranslator.toUsng(new BoundingBoxImpl(8, -8, 1, -1)));
  }

  @Test
  public void testConvertLatLonToUsng() {
    // around Arizona in the United States
    // should return 12S WC 0 6
    assertEquals(
        "12S WC 0 6",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(34, -111), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around Prescott/Chino Valley in Arizona
    // should return 12S UD 0 0
    assertEquals(
        "12S UD 6 1",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(34.5, -112.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // immediately around Prescott city in Arizona
    // should return 12S UD 65 23
    assertEquals(
        "12S UD 65 23",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(34.545, -112.465),
                CoordinatePrecision.ONE_KILOMETER)
            .toString());

    // around Uruguay
    // should return 21H XE 4 0
    assertEquals(
        "21H XE 4 0",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-32.5, -55.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around Buenos Aires city in Argentina
    // should return 21H UB 6 8
    assertEquals(
        "21H UB 6 8",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-34.5, -58.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around Merlo town in Buenos Aires
    // should return 21H UB 41 63
    assertEquals(
        "21H UB 41 63",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-34.66, -58.73), CoordinatePrecision.ONE_KILOMETER)
            .toString());

    // around Madagascar
    // should return 38K PE 5 5
    assertEquals(
        "38K PE 5 5",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-18.5, 46.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around Toliara city in Madagascar
    // should return 38K LA 4 1
    assertEquals(
        "38K LA 4 1",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-22.5, 43.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around Toliara city center in Madagascar
    // should return 38K LA 64 17
    assertEquals(
        "38K LA 45 11",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-22.5, 43.5), CoordinatePrecision.ONE_KILOMETER)
            .toString());

    // around Central Japan
    // should return 54S VF 5 9
    assertEquals(
        "54S VF 5 9",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(37, 140.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around Tokyo city in Japan
    // should return 54S UE 6 2
    assertEquals(
        "54S UE 6 2",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(35.5, 139.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around Tokyo city center in Japan
    // should return 54S UE 41 63
    assertEquals(
        "54S UE 88 50",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(35.69, 139.77), CoordinatePrecision.ONE_KILOMETER)
            .toString());

    // around the international date line'
    // to the immediate west
    // should return 60R US 5 5
    assertEquals(
        "60R US 5 5",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(28.5, 175.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate east
    // should return 1R FM 4 5
    assertEquals(
        "1R FM 4 5",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(28.5, -175.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // with date line crossing the middle
    // should return 1R BM 0 5
    assertEquals(
        "1R BM 0 5",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(28.5, 180), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around the equator
    // to the immediate north
    // should return 58N BK 2 9
    assertEquals(
        "58N BK 2 9",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(4.5, 162.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate south
    // should return 58M BA 2 0
    assertEquals(
        "58M BA 2 0",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-4.5, 162.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // with equator crossing the middle
    // should return 58N BF 2 0
    assertEquals(
        "58N BF 2 0",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(0, 162.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around the international date line and equator
    // to the immediate west and north
    // should return 60N UK 3 9
    assertEquals(
        "60N UK 3 9",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(4.5, 175.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate west and south
    // should return 60M UA 3 0
    assertEquals(
        "60M UA 3 0",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-4.5, 175.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate east and north
    // should return 1N FE 6 9
    assertEquals(
        "1N FE 6 9",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(4.5, -175.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate east and south
    // should return 1M FR 6 0
    assertEquals(
        "1M FR 6 0",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-4.5, -175.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // with crossing of date line and equator at center point
    // should return 1N AA 6 0
    assertEquals(
        "1N AA 6 0",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(0, 180), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around the prime meridian
    // to the immediate west
    // should return 30R US 5 5
    assertEquals(
        "30R US 5 5",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(28.5, -4.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate east
    // should return 31R FM 4 5
    assertEquals(
        "31R FM 4 5",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(28.5, 4.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // with date line crossing the middle
    // should return 31R BM 0 5
    assertEquals(
        "31R BM 0 5",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(28.5, 0), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // around the prime meridian and equator
    // to the immediate west and north
    // should return 30N UK 3 9
    assertEquals(
        "30N UK 3 9",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(4.5, -4.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate west and south
    // should return 30M UA 3 0
    assertEquals(
        "30M UA 3 0",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(-4.5, -4.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate east and north
    // should return 31N FE 6 9
    assertEquals(
        "31N FE 6 9",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(4.5, 4.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // to the immediate east and south
    // should return 31M FR 6 0
    assertEquals(
        "31M FR 6 0",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(-4.5, 4.5), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // with crossing of prime meridian and equator at center point
    // should return 31N AA 6 0
    assertEquals(
        "31N AA 6 0",
        coordinateSystemTranslator
            .toUsng(new DecimalDegreesCoordinateImpl(0, 0), CoordinatePrecision.TEN_KILOMETERS)
            .toString());

    // with crossing of prime meridian and equator at center point
    // should return 31N
    assertEquals(
        "31N",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(0, 0), CoordinatePrecision.SIX_BY_EIGHT_DEGREES)
            .toString());

    // with crossing of prime meridian and equator at center point
    // should return 31N AA
    assertEquals(
        "31N AA",
        coordinateSystemTranslator
            .toUsng(
                new DecimalDegreesCoordinateImpl(0, 0), CoordinatePrecision.ONE_HUNDRED_KILOMETERS)
            .toString());
  }

  @Test
  public void testConvertLatLonToUtm() {
    double[][] latLons = {
      {34, -111},
      {34.5, -112.5},
      {34.545, -112.465},
      {-32.5, -55.5},
      {-34.5, -58.5},
      {-34.66, -58.73},
      {-18.5, 46.5},
      {-22.5, 43.5},
      {-23.355, 43.67},
      {37, 140.5},
      {35.5, 139.5},
      {35.69, 139.77},
      {28.5, 175.5},
      {28.5, -175.5},
      {28.5, 180},
      {4.5, 162.5},
      {-4.5, 162.5},
      {0, 162.5},
      {4.5, 175.5},
      {-4.5, 175.5},
      {4.5, -175.5},
      {-4.5, -175.5},
      {0, 180},
      {28.5, -4.5},
      {28.5, 4.5},
      {28.5, 0},
      {4.5, -4.5},
      {-4.5, -4.5},
      {4.5, 4.5},
      {-4.5, 4.5},
      {0, 0}
    };

    int[][] eastNorthZones = {
      {500000, 3762155, 12},
      {362289, 3818618, 12},
      {365575, 3823561, 12},
      {640915, -3596850, 21},
      {362289, -3818618, 21},
      {341475, -3836700, 21},
      {658354, -2046162, 38},
      {345704, -2488944, 38},
      {364050, -2583444, 38},
      {455511, 4094989, 54},
      {363955, 3929527, 54},
      {388708, 3950262, 54},
      {353193, 3153509, 60},
      {646806, 3153509, 1},
      {206331, 3156262, 1},
      {222576, 497870, 58},
      {222576, -497870, 58},
      {221723, 0, 58},
      {333579, 497566, 60},
      {333579, -497566, 60},
      {666420, 497566, 1},
      {666420, -497566, 1},
      {166021, 0, 1},
      {353193, 3153509, 30},
      {646806, 3153509, 31},
      {206331, 3156262, 31},
      {333579, 497566, 30},
      {333579, -497566, 30},
      {666420, 497566, 31},
      {666420, -497566, 31},
      {166021, 0, 31}
    };

    for (int i = 0; i < latLons.length; i++) {
      UtmCoordinate utmCoordinate =
          coordinateSystemTranslator.toUtm(
              new DecimalDegreesCoordinateImpl(latLons[i][0], latLons[i][1]));
      assertEquals(eastNorthZones[i][0], (int) utmCoordinate.getEasting());
      assertEquals(eastNorthZones[i][1], (int) utmCoordinate.getNorthing());
      assertEquals(eastNorthZones[i][2], utmCoordinate.getZoneNumber());
    }
  }

  @Test
  public void testGithubData() throws ParseException {

    double[][] latLons = {
      {39.9489, -75.15},
      {39.277881, -76.622639},
      {38.88, -77.07},
      {40.7484, -73.9857},
      {34.1341, -118.3217},
      {27.9881, 86.9253},
      {38.8977, -77.0366},
      {38.8895, -77.0352},
      {36.8206, -76.0333},
      {34.2364, -77.9542},
      {-36.0872, -72.8078},
      {-36.1333, -72.7833},
      {-36.1222, -72.8044}
    };

    int[][] eastNorthZones = {
      {4422096, 487186, 18},
      {4348868, 360040, 18},
      {4305496, 320444, 18},
      {4511322, 585628, 18},
      {3777813, 378131, 11},
      {3095886, 492654, 45},
      {4307395, 323385, 18},
      {4306483, 323486, 18},
      {4075469, 407844, 18},
      {3792316, 227899, 18},
      {6004156, 697374, 18},
      {5998991, 699464, 18},
      {6000266, 697593, 18}
    };

    String[] usngStrings = {
      "18S VK 87187 22096", "18S UJ 60040 48869", "18S UJ 20444 05497",
      "18T WL 85628 11322", "11S LT 78132 77814", "45R VL 92654 95886",
      "18S UJ 23386 07396", "18S UJ 23487 06483", "18S VF 07844 75469",
      "18S TC 27900 92317", "18H XF 97375 04155", "18H XE 99464 98991",
      "18H XF 97593 00265"
    };

    for (int i = 0; i < usngStrings.length; i++) {
      UsngCoordinate usngCoordinate = UsngCoordinateImpl.parseUsngString(usngStrings[i]);
      executeGithubDataTest(
          latLons[i][0],
          latLons[i][1],
          eastNorthZones[i][0],
          eastNorthZones[i][1],
          eastNorthZones[i][2],
          usngCoordinate);
    }
  }

  private void executeGithubDataTest(
      double lat, double lon, int utmNorthing, int utmEasting, int zoneNum, UsngCoordinate usng) {
    if (lat < 0) {
      utmNorthing -= 10000000.0;
    }

    UtmCoordinate utmCoordinate;
    DecimalDegreesCoordinate utmToLL;
    DecimalDegreesCoordinate usngToLL;
    utmCoordinate = coordinateSystemTranslator.toUtm(new DecimalDegreesCoordinateImpl(lat, lon));

    assertEquals(utmEasting, (int) utmCoordinate.getEasting());
    assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
    assertEquals(zoneNum, utmCoordinate.getZoneNumber());

    utmCoordinate = new UtmCoordinateImpl(zoneNum, utmEasting, utmNorthing);
    utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

    assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
    assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

    assertEquals(
        usng,
        coordinateSystemTranslator.toUsng(
            new DecimalDegreesCoordinateImpl(lat, lon), CoordinatePrecision.ONE_METER));

    usngToLL = coordinateSystemTranslator.toLatLon(usng);

    assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
    assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));
  }

  @Test
  public void testLLBoxToUSNG() throws ParseException {
    // should return 18S UJ 2348 0648
    String usngString = "18S UJ 2349 0648";
    UsngCoordinate expected = UsngCoordinateImpl.parseUsngString(usngString);
    double lat = 38.8895;
    double lon = -77.0352;
    double lon2 = -77.0351;
    BoundingBox boundingBox = new BoundingBoxImpl(lat, lat, lon, lon2);
    UsngCoordinate actual = coordinateSystemTranslator.toUsng(boundingBox);
    assertEquals(expected, actual);

    // should return 18S UJ 234 064
    usngString = "18S UJ 234 064";
    expected = UsngCoordinateImpl.parseUsngString(usngString);
    lat = 38.8895;
    lon = -77.0352;
    lon2 = -77.035;
    boundingBox = new BoundingBoxImpl(lat, lat, lon, lon2);
    actual = coordinateSystemTranslator.toUsng(boundingBox);
    assertEquals(expected, actual);
  }

  @Test
  public void testLLPointtoUSNG() throws ParseException {

    String[] usngStrings = {"18S UJ 23487 06483", "18S UJ 23 06", "18S UJ 2 0", "18S UJ", "17S"};

    CoordinatePrecision[] precisions = {
      null,
      CoordinatePrecision.ONE_KILOMETER,
      CoordinatePrecision.TEN_KILOMETERS,
      CoordinatePrecision.ONE_HUNDRED_KILOMETERS,
      CoordinatePrecision.SIX_BY_EIGHT_DEGREES
    };

    double[][] expectedValues = {
      {38.8895, -77.0352}, {38.8895, -77.033}, {38.8895, -77.06}, {38.8895, -77.2}, {38.8895, -80}
    };

    for (int i = 0; i < usngStrings.length; i++) {
      UsngCoordinate expected = UsngCoordinateImpl.parseUsngString(usngStrings[i]);
      DecimalDegreesCoordinate decimalDegreesCoordinate =
          new DecimalDegreesCoordinateImpl(expectedValues[i][0], expectedValues[i][1]);
      UsngCoordinate actual = null;

      if (precisions[i] == null) {
        actual = coordinateSystemTranslator.toUsng(decimalDegreesCoordinate);
      } else {
        actual = coordinateSystemTranslator.toUsng(decimalDegreesCoordinate, precisions[i]);
      }

      assertEquals(expected, actual);
    }
  }

  @Test
  public void testUsngToLatLon() throws ParseException {
    // should return 38.8895 -77.0352
    UsngCoordinate usng = UsngCoordinateImpl.parseUsngString("18S UJ 23487 06483");
    double lat = 38.8895;
    double lon = -77.0352;
    DecimalDegreesCoordinate llResult = coordinateSystemTranslator.toLatLon(usng);
    assertEquals(lat, llResult.getLat(), 0.0001);
    assertEquals(lon, llResult.getLon(), 0.0001);

    String[] inputValues =
        new String[] {
          "18S UJ 2349 0648", "18S UJ 234 064", "18S UJ 23 06", "18S UJ 2 0", "18S UJ", "17S", "14R"
        };

    double[][] expectedValues = {
      {38.8895, -77.0352, -77.0351, 38.8895},
      {38.8896, -77.0361, -77.0350, 38.8887},
      {38.8942, -77.0406, -77.0294, 38.8850},
      {38.9224, -77.0736, -76.9610, 38.8304},
      {39.7440, -77.3039, -76.1671, 38.8260},
      {40, -84, -78, 32},
      {32, -102, -96, 24}
    };

    for (int i = 0; i < inputValues.length; i++) {
      usng = UsngCoordinateImpl.parseUsngString(inputValues[i]);
      BoundingBox result = coordinateSystemTranslator.toBoundingBox(usng);
      validateUsngToLatLonResult(expectedValues[i], result);
    }
  }

  private void validateUsngToLatLonResult(double[] expectedValues, BoundingBox result) {
    assertEquals(expectedValues[0], result.getNorth(), 0.0001);
    assertEquals(expectedValues[1], result.getWest(), 0.0001);
    assertEquals(expectedValues[2], result.getEast(), 0.0001);
    assertEquals(expectedValues[3], result.getSouth(), 0.0001);
  }
}
