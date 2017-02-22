package org.codice.usng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.junit.Test;

public class CoordinateSystemTranslatorTest {
    private CoordinateSystemTranslator coordinateSystemTranslator = new CoordinateSystemTranslator(true);

    @Test
    public void testGetZoneNumber() {
        //around Arizona in the United States
        assertEquals(12, coordinateSystemTranslator.getZoneNumber(34, -111));
        //around Prescott/Chino Valley in Arizona
        assertEquals(12, coordinateSystemTranslator.getZoneNumber(34.5, -112.5));
        //immediately around Prescott city in Arizona
        assertEquals(12, coordinateSystemTranslator.getZoneNumber(34.545, -112.465));
        //around Uruguay
        assertEquals(21, coordinateSystemTranslator.getZoneNumber(-32.5, -55.5));
        //around Buenos Aires city in Argentina
        assertEquals(21, coordinateSystemTranslator.getZoneNumber(-34.5, -58.5));
        //around Merlo town in Buenos Aires
        assertEquals(21, coordinateSystemTranslator.getZoneNumber(-34.66, -58.73));
        //around Madagascar
        assertEquals(38, coordinateSystemTranslator.getZoneNumber(-18.5, 46.5));
        //around Toliara city in Madagascar
        assertEquals(38, coordinateSystemTranslator.getZoneNumber(-22.5, 43.5));
        //around Toliara city center in Madagascar
        assertEquals(38, coordinateSystemTranslator.getZoneNumber(-23.355, 43.67));
        //around Central Japan
        assertEquals(54, coordinateSystemTranslator.getZoneNumber(37, 140.5));
        //around Tokyo city in Japan
        assertEquals(54, coordinateSystemTranslator.getZoneNumber(35.5, 139.5));
        //around Tokyo city center in Japan
        assertEquals(54, coordinateSystemTranslator.getZoneNumber(35.69, 139.77));
        //around the international date line
        assertEquals(60, coordinateSystemTranslator.getZoneNumber(28, 179));
        //to the immediate east
        assertEquals(1, coordinateSystemTranslator.getZoneNumber(28, -179));
        //with midpoint directly on it (-180)
        assertEquals(1, coordinateSystemTranslator.getZoneNumber(28, -180));
        //with midpoint directly on it (+180)
        assertEquals(1, coordinateSystemTranslator.getZoneNumber(28, 180));
        //around the equator
        assertEquals(54, coordinateSystemTranslator.getZoneNumber(1, 141));
        //to the immediate south
        assertEquals(54, coordinateSystemTranslator.getZoneNumber(-1, 141));
        //with midpoint directly on it
        assertEquals(54, coordinateSystemTranslator.getZoneNumber(0, 141));
        //around the international date line and equator
        assertEquals(60, coordinateSystemTranslator.getZoneNumber(1, 179));
        //to the immediate west and south
        assertEquals(60, coordinateSystemTranslator.getZoneNumber(-1, 179));
        //to the immediate east and north
        assertEquals(1, coordinateSystemTranslator.getZoneNumber(1, -179));
        //to the immediate east and south
        assertEquals(1, coordinateSystemTranslator.getZoneNumber(-1, -179));
        //with midpoint directly on it (0, -180)
        assertEquals(1, coordinateSystemTranslator.getZoneNumber(0, -180));
        //with midpoint directly on it (0, +180)
        assertEquals(1, coordinateSystemTranslator.getZoneNumber(0, 180));
    }

    @Test
    public void testGetZoneLetterFromLat() {
        //around Arizona in the United States
        assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(34));
        //around Prescott/Chino Valley in Arizona
        assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(34.5));
        //immediately around Prescott city in Arizona
        assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(34.545));
        //around Uruguay
        assertEquals("H", coordinateSystemTranslator.getUtmLetterDesignator(-32.5));
        //around Buenos Aires city in Argentina
        assertEquals("H", coordinateSystemTranslator.getUtmLetterDesignator(-34.5));
        //around Merlo town in Buenos Aires
        assertEquals("H", coordinateSystemTranslator.getUtmLetterDesignator(-34.66));
        //around Madagascar
        assertEquals("K", coordinateSystemTranslator.getUtmLetterDesignator(-18.5));
        //around Toliara city in Madagascar
        assertEquals("K", coordinateSystemTranslator.getUtmLetterDesignator(-22.5));
        //around Toliara city center in Madagascar
        assertEquals("K", coordinateSystemTranslator.getUtmLetterDesignator(-23.355));
        //around Central Japan
        assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(37));
        //around Tokyo city in Japan
        assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(35.5));
        //around Tokyo city center in Japan
        assertEquals("S", coordinateSystemTranslator.getUtmLetterDesignator(35.69));
        //around the equator
        assertEquals("N", coordinateSystemTranslator.getUtmLetterDesignator(1));
        //to the immediate south
        assertEquals("M", coordinateSystemTranslator.getUtmLetterDesignator(-1));
        //with midpoint directly on it
        assertEquals("N", coordinateSystemTranslator.getUtmLetterDesignator(0));
        //imediately south of north polar maximum
        assertEquals("X", coordinateSystemTranslator.getUtmLetterDesignator(83));
        //imediately north of north polar maximum
        assertEquals("Z", coordinateSystemTranslator.getUtmLetterDesignator(85));
        //directly on north polar maximum
        assertEquals("X", coordinateSystemTranslator.getUtmLetterDesignator(84));
        //imediately north of south polar minimum
        assertEquals("C", coordinateSystemTranslator.getUtmLetterDesignator(-79));
        //imediately south of south polar minimum
        assertEquals("Z", coordinateSystemTranslator.getUtmLetterDesignator(-81));
        //directly on south polar minimum
        assertEquals("C", coordinateSystemTranslator.getUtmLetterDesignator(-80));
    }

    @Test
    public void testParseUsng() throws ParseException {
        //should return zone=5; letter=Q
        UsngCoordinate parts = UsngCoordinate.parseUsngString("5Q");
        assertEquals(5, parts.getZoneNumber());
        assertEquals('Q', parts.getLatitudeBandLetter());

        //should return zone=12; letter=S
        parts = UsngCoordinate.parseUsngString("12S");
        assertEquals(12, parts.getZoneNumber());
        assertEquals('S', parts.getLatitudeBandLetter());

        //should return zone=5; letter=Q; square1=K; square2=B

        parts = UsngCoordinate.parseUsngString("5Q KB");
        assertEquals(5, parts.getZoneNumber());
        assertEquals('Q', parts.getLatitudeBandLetter());
        assertEquals('K',
                parts.getColumnLetter()
                        .charValue());
        assertEquals('B',
                parts.getRowLetter()
                        .charValue());

        //should return zone=12; letter=S; square1=V; square2=C

        parts = UsngCoordinate.parseUsngString("12S VC");
        assertEquals(12, parts.getZoneNumber());
        assertEquals('S', parts.getLatitudeBandLetter());
        assertEquals('V',
                parts.getColumnLetter()
                        .charValue());
        assertEquals('C',
                parts.getRowLetter()
                        .charValue());

        //should return zone=5; letter=Q; square1=K; square2=B; easting=42785; northing=31517

        parts = UsngCoordinate.parseUsngString("5Q KB 42785 31517");
        assertEquals(5, parts.getZoneNumber());
        assertEquals('Q', parts.getLatitudeBandLetter());
        assertEquals('K',
                parts.getColumnLetter()
                        .charValue());
        assertEquals('B',
                parts.getRowLetter()
                        .charValue());
        assertEquals(5,
                parts.getPrecision()
                        .getIntValue());
        assertEquals(42785.0, parts.getEasting(), 0);
        assertEquals(31517.0, parts.getNorthing(), 0);

        //should return zone=12; letter=S; square1=V; square2=C; easting=12900; northing=43292

        parts = UsngCoordinate.parseUsngString("12S VC 12900 43292");
        assertEquals(12, parts.getZoneNumber());
        assertEquals('S', parts.getLatitudeBandLetter());
        assertEquals('V',
                parts.getColumnLetter()
                        .charValue());
        assertEquals('C',
                parts.getRowLetter()
                        .charValue());
        assertEquals(5,
                parts.getPrecision()
                        .getIntValue());
        assertEquals(12900, parts.getEasting(), 0);
        assertEquals(43292, parts.getNorthing(), 0);
    }

    @Test(expected = ParseException.class)
    public void testParseUtm() throws ParseException {
        //should return zone=5; letter=Q; easting=-00001; northing=2199600
        String utmCoordinateString = "5Q -00001 2199600";
        UtmCoordinate utmCoordinate = UtmCoordinate.parseUtmString(utmCoordinateString);
        assertEquals(5, utmCoordinate.getZoneNumber());
        assertEquals('Q', utmCoordinate.getLattitudeBand().charValue());
        assertEquals(-1, utmCoordinate.getEasting(),0);
        assertEquals(2199600.0, utmCoordinate.getNorthing(), 0);
        assertEquals(CoordinatePrecision.ONE_METER, utmCoordinate.getPrecision());

        //should return zone=5; letter=null; easting=-00001; northing=2199600
        utmCoordinateString = "5 -00001 2199600";
        utmCoordinate = UtmCoordinate.parseUtmString(utmCoordinateString);
        assertEquals(5, utmCoordinate.getZoneNumber());
        assertNull(utmCoordinate.getLattitudeBand());
        assertEquals(-1, utmCoordinate.getEasting(),0);
        assertEquals(2199600.0, utmCoordinate.getNorthing(), 0);
        assertEquals(CoordinatePrecision.ONE_METER, utmCoordinate.getPrecision());

        //should throw ParseException
        utmCoordinateString = "5Q";
        UtmCoordinate.parseUtmString(utmCoordinateString);
    }

    @Test
    public void testConvertUsngtoUtm() {
        //with single digit zone
        //should return north=2131517; east=242785; zone=5; letter=Q
        int zone = 18;
        char letter = 'S';
        char sq1 = 'U';
        char sq2 = 'J';
        int easting = 23487;
        int northing = 6483;
        UtmCoordinate coords = coordinateSystemTranslator.toUtm(new UsngCoordinate(zone,
                letter,
                sq1,
                sq2,
                easting,
                northing));
        assertEquals(4306483, Math.floor(coords.getNorthing()), 0);
        assertEquals(323487, Math.floor(coords.getEasting()), 0);
        assertEquals(18, coords.getZoneNumber());
        assertEquals('S', coords.getLattitudeBand().charValue());

        //with two digit zone
        //should return north=43292; east=12900; zone=12; letter=S
        zone = 12;
        letter = 'S';
        sq1 = 'V';
        sq2 = 'C';
        easting = 12900;
        northing = 43292;
        coords = coordinateSystemTranslator.toUtm(new UsngCoordinate(zone, letter, sq1, sq2, easting, northing));
        assertEquals(3743292, Math.floor(coords.getNorthing()), 0);
        assertEquals(412900, Math.floor(coords.getEasting()), 0);
        assertEquals(12, coords.getZoneNumber());
        assertEquals('S', coords.getLattitudeBand().charValue());
    }

    @Test
    public void testConvertUtmToLatLon() {
        //with single digit zone and specifying accuracy
        int northing = 42785;
        int easting = 31517;
        int zone = 5;
        int accuracy = 100000;
        UtmCoordinate utmCoordinate = new UtmCoordinate(zone, easting, northing);
        BoundingBox boundingBox = coordinateSystemTranslator.toBoundingBox(utmCoordinate, accuracy);
        assertEquals(1, Math.floor(boundingBox.getNorth()), 0);
        assertEquals(-157, Math.floor(boundingBox.getEast()), 0);
        assertEquals(0, Math.floor(boundingBox.getSouth()), 0);
        assertEquals(-158, Math.floor(boundingBox.getWest()), 0);

        //should return lat=0; east=-158
        northing = 42785;
        easting = 31517;
        zone = 5;
        utmCoordinate = new UtmCoordinate(zone, easting, northing);
        LatLonCoordinate latLon = coordinateSystemTranslator.toLatLon(utmCoordinate);
        assertEquals(0, Math.floor(latLon.getLat()), 0);
        assertEquals(-158, Math.floor(latLon.getLon()), 0);

        //should return north=1; east=-115; south=0; west=-116
        northing = 12900;
        easting = 43292;
        zone = 12;
        accuracy = 100000;
        utmCoordinate = new UtmCoordinate(zone, easting, northing);
        boundingBox = coordinateSystemTranslator.toBoundingBox(utmCoordinate, accuracy);
        assertEquals(1, Math.floor(boundingBox.getNorth()), 0);
        assertEquals(-115, Math.floor(boundingBox.getEast()), 0);
        assertEquals(0, Math.floor(boundingBox.getSouth()), 0);
        assertEquals(-116, Math.floor(boundingBox.getWest()), 0);

        //should return lat=0; lon=-116
        northing = 12900;
        easting = 43292;
        zone = 12;
        utmCoordinate = new UtmCoordinate(zone, easting, northing);
        latLon = coordinateSystemTranslator.toLatLon(utmCoordinate);
        assertEquals(0, Math.floor(latLon.getLat()), 0);
        assertEquals(-116, Math.floor(latLon.getLon()), 0);
    }

    @Test
    public void testConvertUsngToLatLon() throws ParseException {
        //should return north=19; east=-155; south=19; west=-155
        UsngCoordinate usng = UsngCoordinate.parseUsngString("5Q KB 42785 31517");
        BoundingBox boundingBox = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(19, Math.floor(boundingBox.getNorth()), 0);
        assertEquals(-156, Math.floor(boundingBox.getEast()), 0);
        assertEquals(19, Math.floor(boundingBox.getSouth()), 0);
        assertEquals(-156, Math.floor(boundingBox.getWest()), 0);

        //should return north=33; east=-111; south=33; west=-111
        usng = UsngCoordinate.parseUsngString("12S VC 12900 43292");
        boundingBox = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(33, Math.floor(boundingBox.getNorth()), 0);
        assertEquals(-112, Math.floor(boundingBox.getEast()), 0);
        assertEquals(33, Math.floor(boundingBox.getSouth()), 0);
        assertEquals(-112, Math.floor(boundingBox.getWest()), 0);
    }

    @Test
    public void testConvertBoundingBoxToUsng() throws ParseException {
        //should return 12S
        String usngString = "12S";
        UsngCoordinate usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(37, 31, -108, -114)));

        //should return 12S UD
        usngString = "12S UD";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34.55,
                        34.45,
                        -112.4,
                        -112.3)));

        //should return 12S UD 7 1
        usngString = "12S UD 7 1";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34.50,
                        34.45,
                        -112.4,
                        -112.4)));

        //should return 12S UD 65 24
        usngString = "12S UD 65 24";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34.55,
                        34.55,
                        -112.465,
                        -112.47)));

        //should return 12S UD 649 241
        usngString = "12S UD 649 241";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34.55,
                        34.55,
                        -112.471,
                        -112.472)));

        //should return 12S UD 6494 2412
        usngString = "12S UD 6494 2412";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34.55,
                        34.55,
                        -112.47200,
                        -112.47190)));

        //should return 12S UD 649 241
        usngString = "12S UD 64941 24126";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34.55,
                        34.55,
                        -112.47200,
                        -112.47199)));

        //should return 21H
        usngString = "21H";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-30, -35, -53, -58)));

        //should return 21H UB
        usngString = "21H UB";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-34.5, -35, -58.5, -58.5)));

        //should return 21H UB 41 63
        usngString = "21H UB 41 63";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-34.665,
                        -34.66,
                        -58.73,
                        -58.73)));

        //should return 38K
        usngString = "38K";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-11, -26, 51, 42)));

        //should return 38K LA
        usngString = "38K LA";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-21.9, -22, 43.7, 43.6)));

        //should return 38K LA
        usngString = "38K LA 6 6";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-22, -22, 43.7, 43.65)));

        //should return 38K LV 64 17
        usngString = "38K LV 66 12";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-23.395,
                        -23.39,
                        43.70,
                        43.695)));

        //should return 54S
        usngString = "54S";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(41, 33, 143, 138)));

        //should return 54S UD
        usngString = "54S UD";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(35, 35, 140, 139)));

        //should return 54S UE 41 63
        usngString = "54S UE 86 51";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(35.7,
                        35.7,
                        139.75,
                        139.745)));
        //should return 60R
        usngString = "60R";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34, 23, 179, 172)));

        //should return 1R
        usngString = "1R";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34, 23, -179, -172)));

        //should return 1R BM
        usngString = "1R BM";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(28, 28, 179.9, -179.9)));

        //should return 58N
        usngString = "58N";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(8, 1, 166, 159)));

        //should return 58M
        usngString = "58M";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-1, -8, 166, 159)));

        //should return 58N
        usngString = "58N";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(8, -8, 166, 159)));

        //should return 60N
        usngString = "60N";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(8, 1, 179, 172)));

        //should return 60M
        usngString = "60M";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-1, -8, 179, 172)));

        //should return 1N
        usngString = "1N";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(8, 1, -179, -172)));

        //should return 1M
        usngString = "1M";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-1, -8, -179, -172)));

        //should return 1N AA
        usngString = "1N AA";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(0, 0, -179.9, 179.9)));

        //should return 30R
        usngString = "30R";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34, 23, -1, -8)));

        //should return 31R
        usngString = "31R";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34, 23, 1, 8)));

        //should return 31R
        usngString = "31R";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(34, 23, -1, 1)));

        //should return 30M
        usngString = "30M";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-1, -8, -1, -8)));

        //should return 31N
        usngString = "31N";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(8, 1, 8, 1)));

        //should return 31M
        usngString = "31M";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(-1, -8, 8, 1)));

        //should return 31N
        usngString = "31N";
        usngCoordinate = UsngCoordinate.parseUsngString(usngString);
        assertEquals(usngCoordinate,
                coordinateSystemTranslator.toUsng(new BoundingBox(8, -8, 1, -1)));
    }

    @Test
    public void testConvertLatLonToUsng() {
        //around Arizona in the United States
        //should return 12S WC 0 6
        assertEquals("12S WC 0 6",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(34, -111), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around Prescott/Chino Valley in Arizona
        //should return 12S UD 0 0
        assertEquals("12S UD 6 1",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(34.5, -112.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //immediately around Prescott city in Arizona
        //should return 12S UD 65 23
        assertEquals("12S UD 65 23",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(34.545, -112.465), CoordinatePrecision.ONE_KILOMETER)
                        .toString());

        //around Uruguay
        //should return 21H XE 4 0
        assertEquals("21H XE 4 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-32.5, -55.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around Buenos Aires city in Argentina
        //should return 21H UB 6 8
        assertEquals("21H UB 6 8",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-34.5, -58.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around Merlo town in Buenos Aires
        //should return 21H UB 41 63
        assertEquals("21H UB 41 63",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-34.66, -58.73), CoordinatePrecision.ONE_KILOMETER)
                        .toString());

        //around Madagascar
        //should return 38K PE 5 5
        assertEquals("38K PE 5 5",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-18.5, 46.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around Toliara city in Madagascar
        //should return 38K LA 4 1
        assertEquals("38K LA 4 1",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-22.5, 43.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around Toliara city center in Madagascar
        //should return 38K LA 64 17
        assertEquals("38K LA 45 11",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-22.5, 43.5), CoordinatePrecision.ONE_KILOMETER)
                        .toString());

        //around Central Japan
        //should return 54S VF 5 9
        assertEquals("54S VF 5 9",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(37, 140.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around Tokyo city in Japan
        //should return 54S UE 6 2
        assertEquals("54S UE 6 2",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(35.5, 139.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around Tokyo city center in Japan
        //should return 54S UE 41 63
        assertEquals("54S UE 88 50",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(35.69, 139.77), CoordinatePrecision.ONE_KILOMETER)
                        .toString());

        //around the international date line'
        //to the immediate west
        //should return 60R US 5 5
        assertEquals("60R US 5 5",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(28.5, 175.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate east
        //should return 1R FM 4 5
        assertEquals("1R FM 4 5",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(28.5, -175.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //with date line crossing the middle
        //should return 1R BM 0 5
        assertEquals("1R BM 0 5",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(28.5, 180), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around the equator
        //to the immediate north
        //should return 58N BK 2 9
        assertEquals("58N BK 2 9",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(4.5, 162.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate south
        //should return 58M BA 2 0
        assertEquals("58M BA 2 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-4.5, 162.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //with equator crossing the middle
        //should return 58N BF 2 0
        assertEquals("58N BF 2 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(0, 162.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around the international date line and equator
        //to the immediate west and north
        //should return 60N UK 3 9
        assertEquals("60N UK 3 9",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(4.5, 175.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate west and south
        //should return 60M UA 3 0
        assertEquals("60M UA 3 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-4.5, 175.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate east and north
        //should return 1N FE 6 9
        assertEquals("1N FE 6 9",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(4.5, -175.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate east and south
        //should return 1M FR 6 0
        assertEquals("1M FR 6 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-4.5, -175.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //with crossing of date line and equator at center point
        //should return 1N AA 6 0
        assertEquals("1N AA 6 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(0, 180), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around the prime meridian
        //to the immediate west
        //should return 30R US 5 5
        assertEquals("30R US 5 5",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(28.5, -4.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate east
        //should return 31R FM 4 5
        assertEquals("31R FM 4 5",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(28.5, 4.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //with date line crossing the middle
        //should return 31R BM 0 5
        assertEquals("31R BM 0 5",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(28.5, 0), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //around the prime meridian and equator
        //to the immediate west and north
        //should return 30N UK 3 9
        assertEquals("30N UK 3 9",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(4.5, -4.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate west and south
        //should return 30M UA 3 0
        assertEquals("30M UA 3 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-4.5, -4.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate east and north
        //should return 31N FE 6 9
        assertEquals("31N FE 6 9",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(4.5, 4.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //to the immediate east and south
        //should return 31M FR 6 0
        assertEquals("31M FR 6 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(-4.5, 4.5), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //with crossing of prime meridian and equator at center point
        //should return 31N AA 6 0
        assertEquals("31N AA 6 0",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(0, 0), CoordinatePrecision.TEN_KILOMETERS)
                        .toString());

        //with crossing of prime meridian and equator at center point
        //should return 31N
        assertEquals("31N",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(0, 0), CoordinatePrecision.SIX_BY_EIGHT_DEGREES)
                        .toString());

        //with crossing of prime meridian and equator at center point
        //should return 31N AA
        assertEquals("31N AA",
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(0, 0), CoordinatePrecision.ONE_HUNDRED_KILOMETERS)
                        .toString());
    }

    @Test
    public void testConvertLatLonToUtm() {
        //around Arizona in the United States
        //should return easting=500000; northing=3762155; zone=12
        UtmCoordinate utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(34, -111));
        assertEquals(500000, (int) utmCoordinate.getEasting());
        assertEquals(3762155, (int) utmCoordinate.getNorthing());
        assertEquals(12, utmCoordinate.getZoneNumber());

        //around Prescott/Chino Valley in Arizona
        //should return easting=362289; northing=3818618; zone=12
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(34.5, -112.5));
        assertEquals(362289, (int) utmCoordinate.getEasting());
        assertEquals(3818618, (int) utmCoordinate.getNorthing());
        assertEquals(12, utmCoordinate.getZoneNumber());

        //immediately around Prescott city in Arizona
        //should return easting=365575; northing=3823561; zone=12
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(34.545, -112.465));
        assertEquals(365575, (int) utmCoordinate.getEasting());
        assertEquals(3823561, (int) utmCoordinate.getNorthing());
        assertEquals(12, utmCoordinate.getZoneNumber());

        //around Uruguay
        //should return easting=640915; northing=-3596850; zone=21
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-32.5, -55.5));
        assertEquals(640915, (int) utmCoordinate.getEasting());
        assertEquals(-3596850, (int) utmCoordinate.getNorthing());
        assertEquals(21, utmCoordinate.getZoneNumber());

        //around Buenos Aires city in Argentina
        //should return easting=362289; northing=-3818618; zone=21
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-34.5, -58.5));
        assertEquals(362289, (int) utmCoordinate.getEasting());
        assertEquals(-3818618, (int) utmCoordinate.getNorthing());
        assertEquals(21, utmCoordinate.getZoneNumber());

        //around Merlo town in Buenos Aires
        //should return easting=341475; northing=-3836700; zone=21
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-34.66, -58.73));
        assertEquals(341475, (int) utmCoordinate.getEasting());
        assertEquals(-3836700, (int) utmCoordinate.getNorthing());
        assertEquals(21, utmCoordinate.getZoneNumber());

        //around Madagascar
        //should return easting=658354; northing=-2046162; zone=38
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-18.5, 46.5));
        assertEquals(658354, (int) utmCoordinate.getEasting());
        assertEquals(-2046162, (int) utmCoordinate.getNorthing());
        assertEquals(38, utmCoordinate.getZoneNumber());

        //around Toliara city in Madagascar
        //should return easting=345704; northing=-2488944; zone=38
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-22.5, 43.5));
        assertEquals(345704, (int) utmCoordinate.getEasting());
        assertEquals(-2488944, (int) utmCoordinate.getNorthing());
        assertEquals(38, utmCoordinate.getZoneNumber());

        //around Toliara city center in Madagascar
        //should return easting=364050; northing=-2583444; zone=38
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-23.355, 43.67));
        assertEquals(364050, (int) utmCoordinate.getEasting());
        assertEquals(-2583444, (int) utmCoordinate.getNorthing());
        assertEquals(38, utmCoordinate.getZoneNumber());

        //around Central Japan
        //should return easting=455511; northing=4094989; zone=54
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(37, 140.5));
        assertEquals(455511, (int) utmCoordinate.getEasting());
        assertEquals(4094989, (int) utmCoordinate.getNorthing());
        assertEquals(54, utmCoordinate.getZoneNumber());

        //around Tokyo city in Japan
        //should return easting=363955; northing=3929527; zone=54
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(35.5, 139.5));
        assertEquals(363955, (int) utmCoordinate.getEasting());
        assertEquals(3929527, (int) utmCoordinate.getNorthing());
        assertEquals(54, utmCoordinate.getZoneNumber());

        //around Tokyo city center in Japan
        //should return easting=388708; northing=3950262; zone=54
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(35.69, 139.77));
        assertEquals(388708, (int) utmCoordinate.getEasting());
        assertEquals(3950262, (int) utmCoordinate.getNorthing());
        assertEquals(54, utmCoordinate.getZoneNumber());

        //around the international date line
        //to the immediate west
        //should return easting=353193; northing=3153509; zone=60
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(28.5, 175.5));
        assertEquals(353193, (int) utmCoordinate.getEasting());
        assertEquals(3153509, (int) utmCoordinate.getNorthing());
        assertEquals(60, utmCoordinate.getZoneNumber());

        //to the immediate east
        //should return easting=646806; northing=3153509; zone=1
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(28.5, -175.5));
        assertEquals(646806, (int) utmCoordinate.getEasting());
        assertEquals(3153509, (int) utmCoordinate.getNorthing());
        assertEquals(1, utmCoordinate.getZoneNumber());

        //with date line crossing the middle
        //should return easting=206331; northing=3156262; zone=1
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(28.5, 180));
        assertEquals(206331, (int) utmCoordinate.getEasting());
        assertEquals(3156262, (int) utmCoordinate.getNorthing());
        assertEquals(1, utmCoordinate.getZoneNumber());

        //around the equator
        //to the immediate north
        //should return easting=222576; northing=497870; zone=58
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(4.5, 162.5));
        assertEquals(222576, (int) utmCoordinate.getEasting());
        assertEquals(497870, (int) utmCoordinate.getNorthing());
        assertEquals(58, utmCoordinate.getZoneNumber(), 0);

        //to the immediate south
        //should return easting=222576; northing=-497870; zone=58
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-4.5, 162.5));
        assertEquals(222576, (int) utmCoordinate.getEasting());
        assertEquals(-497870, (int) utmCoordinate.getNorthing());
        assertEquals(58, utmCoordinate.getZoneNumber(), 0);

        //with equator crossing the middle
        //should return easting=221723; northing=0; zone=58
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(0, 162.5));
        assertEquals(221723, (int) utmCoordinate.getEasting());
        assertEquals(0, (int) utmCoordinate.getNorthing());
        assertEquals(58, utmCoordinate.getZoneNumber(), 0);

        //around the international date line and equator
        //to the immediate west and north
        //should return easting=333579; northing=497566; zone=60
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(4.5, 175.5));
        assertEquals(333579, (int) utmCoordinate.getEasting());
        assertEquals(497566, (int) utmCoordinate.getNorthing());
        assertEquals(60, utmCoordinate.getZoneNumber(), 0);

        //to the immediate west and south
        //should return easting=333579; northing=-497566; zone=60
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-4.5, 175.5));
        assertEquals(333579, (int) utmCoordinate.getEasting());
        assertEquals(-497566, (int) utmCoordinate.getNorthing());
        assertEquals(60, utmCoordinate.getZoneNumber(), 0);

        //to the immediate east and north
        //should return easting=666420; northing=497566; zone=1
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(4.5, -175.5));
        assertEquals(666420, (int) utmCoordinate.getEasting());
        assertEquals(497566, (int) utmCoordinate.getNorthing());
        assertEquals(1, utmCoordinate.getZoneNumber(), 0);

        //to the immediate east and south
        //should return easting=666420; northing=666420; zone=1
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-4.5, -175.5));
        assertEquals(666420, (int) utmCoordinate.getEasting());
        assertEquals(666420, (int) utmCoordinate.getEasting());
        assertEquals(1, utmCoordinate.getZoneNumber(), 0);

        //with crossing of date line and equator at center point
        //should return easting=166021; northing=0; zone=1
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(0, 180));
        assertEquals(166021, (int) utmCoordinate.getEasting());
        assertEquals(0, (int) utmCoordinate.getNorthing());
        assertEquals(1, utmCoordinate.getZoneNumber(), 0);

        //around the prime meridian
        //to the immediate west
        //should return easting=353193; northing=3153509; zone=30
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(28.5, -4.5));
        assertEquals(353193, (int) utmCoordinate.getEasting());
        assertEquals(3153509, (int) utmCoordinate.getNorthing());
        assertEquals(30, utmCoordinate.getZoneNumber(), 0);

        //to the immediate east
        //should return easting=646806; northing=3153509; zone=31
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(28.5, 4.5));
        assertEquals(646806, (int) utmCoordinate.getEasting());
        assertEquals(3153509, (int) utmCoordinate.getNorthing());
        assertEquals(31, utmCoordinate.getZoneNumber(), 0);

        //with date line crossing the middle
        //should return easting=206331; northing=3156262; zone=31
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(28.5, 0));
        assertEquals(206331, (int) utmCoordinate.getEasting());
        assertEquals(3156262, (int) utmCoordinate.getNorthing());
        assertEquals(31, utmCoordinate.getZoneNumber(), 0);

        //around the prime meridian and equator
        //to the immediate west and north
        //should return easting=333579; northing=497566; zone=30
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(4.5, -4.5));
        assertEquals(333579, (int) utmCoordinate.getEasting());
        assertEquals(497566, (int) utmCoordinate.getNorthing());
        assertEquals(30, utmCoordinate.getZoneNumber(), 0);

        //to the immediate west and south
        //should return easting=333579; northing=-497566; zone=30
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-4.5, -4.5));
        assertEquals(333579, (int) utmCoordinate.getEasting());
        assertEquals(-497566, (int) utmCoordinate.getNorthing());
        assertEquals(30, utmCoordinate.getZoneNumber(), 0);

        //to the immediate east and north
        //should return easting=666420; northing=497566; zone=31
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(4.5, 4.5));
        assertEquals(666420, (int) utmCoordinate.getEasting());
        assertEquals(497566, (int) utmCoordinate.getNorthing());
        assertEquals(31, utmCoordinate.getZoneNumber(), 0);

        //to the immediate east and south
        //should return easting=666420; northing=-497566; zone=31
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(-4.5, 4.5));
        assertEquals(666420, (int) utmCoordinate.getEasting());
        assertEquals(-497566, (int) utmCoordinate.getNorthing());
        assertEquals(31, utmCoordinate.getZoneNumber(), 0);

        //with crossing of prime meridian and equator at center point
        //should return easting=166021; northing=0; zone=31
        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(0, 0));
        assertEquals(166021, (int) utmCoordinate.getEasting());
        assertEquals(0, (int) utmCoordinate.getNorthing());
        assertEquals(31, utmCoordinate.getZoneNumber(), 0);
    }

    @Test
    public void testGithubData() throws ParseException {
        //Using test data from github issue
        //washington monument
        //should return lat 38.8895 long -77.0352
        double lat = 38.8895;
        double lon = -77.0352;
        int utmNorthing = 4306483;
        int utmEasting = 323486;
        int zoneNum = 18;
        UsngCoordinate usng = UsngCoordinate.parseUsngString("18S UJ 23487 06483");

        UtmCoordinate utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        LatLonCoordinate utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        LatLonCoordinate usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //white house
        //should return lat 38.8977 lon -77.0366
        lat = 38.8977;
        lon = -77.0366;
        utmNorthing = 4307395;
        utmEasting = 323385;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18S UJ 23386 07396");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //mount everest'
        //should return lat 27.9881 lon 86.9253
        lat = 27.9881;
        lon = 86.9253;
        utmNorthing = 3095886;
        utmEasting = 492654;
        zoneNum = 45;
        usng = UsngCoordinate.parseUsngString("45R VL 92654 95886");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //hollywood sign'
        //should return lat 34.1341 lon -118.3217
        lat = 34.1341;
        lon = -118.3217;
        utmNorthing = 3777813;
        utmEasting = 378131;
        zoneNum = 11;
        usng = UsngCoordinate.parseUsngString("11S LT 78132 77814");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //empire state building'
        //should return lat 40.7484 lon -73.9857'
        lat = 40.7484;
        lon = -73.9857;
        utmNorthing = 4511322;
        utmEasting = 585628;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18T WL 85628 11322");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //arlington cemetery'
        //should return lat 38.88 lon -77.07'
        lat = 38.88;
        lon = -77.07;
        utmNorthing = 4305496;
        utmEasting = 320444;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18S UJ 20444 05497");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 100), Math.round(utmToLL.getLat() * 100));
        assertEquals(Math.round(lon * 100), Math.round(utmToLL.getLon() * 100));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 100), Math.round(usngToLL.getLat() * 100));
        assertEquals(Math.round(lon * 100), Math.round(usngToLL.getLon() * 100));

        //raven\'s stadium'
        //should return lat 39.277881 lon -76.622639'
        lat = 39.277881;
        lon = -76.622639;
        utmNorthing = 4348868;
        utmEasting = 360040;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18S UJ 60040 48869");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //independence hall'
        //should return lat 39.9489 lon -75.15'
        lat = 39.9489;
        lon = -75.15;
        utmNorthing = 4422096;
        utmEasting = 487186;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18S VK 87187 22096");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 100), Math.round(utmToLL.getLon() * 100));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 100), Math.round(usngToLL.getLon() * 100));

        //naval air station oceana'
        //should return lat 36.8206 lon -76.0333'
        lat = 36.8206;
        lon = -76.0333;
        utmNorthing = 4075469;
        utmEasting = 407844;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18S VF 07844 75469");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //uss north carolina'
        //should return lat 34.2364 lon -77.9542'
        lat = 34.2364;
        lon = -77.9542;
        utmNorthing = 3792316;
        utmEasting = 227899;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18S TC 27900 92317");

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //m-80-n and n-606 junction'
        //should return lat -36.0872 lon -72.8078'
        lat = -36.0872;
        lon = -72.8078;
        utmNorthing = 6004156;
        utmEasting = 697374;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18H XF 97375 04155");

        if (lat < 0) {
            utmNorthing -= 10000000.0;
        }

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //cobquecura'
        //should return lat -36.1333 lon -72.7833'
        lat = -36.1333;
        lon = -72.7833;
        utmNorthing = 5998991;
        utmEasting = 699464;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18H XE 99464 98991");

        if (lat < 0) {
            utmNorthing -= 10000000.0;
        }

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));

        //aerodromo los morros (scqr)'
        //should return lat -36.1222 lon -72.8044'
        lat = -36.1222;
        lon = -72.8044;
        utmNorthing = 6000266;
        utmEasting = 697593;
        zoneNum = 18;
        usng = UsngCoordinate.parseUsngString("18H XF 97593 00265");

        if (lat < 0) {
            utmNorthing -= 10000000.0;
        }

        utmCoordinate = coordinateSystemTranslator.toUtm(new LatLonCoordinate(lat, lon));

        assertEquals(utmEasting, (int) utmCoordinate.getEasting());
        assertEquals(utmNorthing, (int) utmCoordinate.getNorthing());
        assertEquals(zoneNum, utmCoordinate.getZoneNumber());

        utmCoordinate = new UtmCoordinate(zoneNum, utmEasting, utmNorthing);
        utmToLL = coordinateSystemTranslator.toLatLon(utmCoordinate);

        assertEquals(Math.round(lat * 10000), Math.round(utmToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(utmToLL.getLon() * 10000));

        assertEquals(usng,
                coordinateSystemTranslator.toUsng(new LatLonCoordinate(lat, lon),
                        CoordinatePrecision.ONE_METER));

        usngToLL = coordinateSystemTranslator.toLatLon(usng);

        assertEquals(Math.round(lat * 10000), Math.round(usngToLL.getLat() * 10000));
        assertEquals(Math.round(lon * 10000), Math.round(usngToLL.getLon() * 10000));
    }

    @Test
    public void testLLBoxToUSNG() throws ParseException {
        //toUsng
        //should return 18S UJ 23487 06483
        String usngString = "18S UJ 23487 06483";
        UsngCoordinate expected = UsngCoordinate.parseUsngString(usngString);
        double lat = 38.8895;
        double lon = -77.0352;
        LatLonCoordinate latLonCoordinate = new LatLonCoordinate(lat, lon);
        UsngCoordinate actual = coordinateSystemTranslator.toUsng(latLonCoordinate);
        assertEquals(expected, actual);

        //should return 18S UJ 2348 0648
        usngString = "18S UJ 2349 0648";
        expected = UsngCoordinate.parseUsngString(usngString);
        lat = 38.8895;
        lon = -77.0352;
        double lon2 = -77.0351;
        BoundingBox boundingBox = new BoundingBox(lat, lat, lon, lon2);
        actual = coordinateSystemTranslator.toUsng(boundingBox);
        assertEquals(expected, actual);

        //should return 18S UJ 234 064
        usngString = "18S UJ 234 064";
        expected = UsngCoordinate.parseUsngString(usngString);
        lat = 38.8895;
        lon = -77.0352;
        lon2 = -77.035;
        boundingBox = new BoundingBox(lat, lat, lon, lon2);
        actual = coordinateSystemTranslator.toUsng(boundingBox);
        assertEquals(expected, actual);

        //should return 18S UJ 23 06
        usngString = "18S UJ 23 06";
        expected = UsngCoordinate.parseUsngString(usngString);
        lat = 38.8895;
        lon = -77.0352;
        lon2 = -77.033;
        latLonCoordinate = new LatLonCoordinate(lat, lon2);
        actual = coordinateSystemTranslator.toUsng(latLonCoordinate, CoordinatePrecision.ONE_KILOMETER);
        assertEquals(expected, actual);

        //should return 18S UJ 2 0
        usngString = "18S UJ 2 0";
        expected = UsngCoordinate.parseUsngString(usngString);
        lat = 38.8895;
        lon = -77.0352;
        lon2 = -77.06;
        latLonCoordinate = new LatLonCoordinate(lat, lon2);
        actual = coordinateSystemTranslator.toUsng(latLonCoordinate, CoordinatePrecision.TEN_KILOMETERS);
        assertEquals(expected, actual);

        //should return 18S UJusng
        usngString = "18S UJ";
        expected = UsngCoordinate.parseUsngString(usngString);
        lat = 38.8895;
        lon = -77.0352;
        lon2 = -77.2;
        latLonCoordinate = new LatLonCoordinate(lat, lon2);
        actual = coordinateSystemTranslator.toUsng(latLonCoordinate, CoordinatePrecision.ONE_HUNDRED_KILOMETERS);
        assertEquals(expected, actual);

        //should return 17S
        usngString = "17S";
        expected = UsngCoordinate.parseUsngString(usngString);
        lat = 38.8895;
        lon = -77.0352;
        lon2 = -80;
        latLonCoordinate = new LatLonCoordinate(lat, lon2);
        actual = coordinateSystemTranslator.toUsng(latLonCoordinate, CoordinatePrecision.SIX_BY_EIGHT_DEGREES);
        assertEquals(expected, actual);
    }

    @Test
    public void testUsngToLatLon() throws ParseException {
        //should return 38.8895 -77.0352
        UsngCoordinate usng = UsngCoordinate.parseUsngString("18S UJ 23487 06483");
        double lat = 38.8895;
        double lon = -77.0352;
        LatLonCoordinate llResult = coordinateSystemTranslator.toLatLon(usng);
        assertEquals(lat, llResult.getLat(), 0.0001);
        assertEquals(lon, llResult.getLon(), 0.0001);

        //should return 38.8895 -77.0352 -77.0351
        usng = UsngCoordinate.parseUsngString("18S UJ 2349 0648");
        double north = 38.8895;
        double south = 38.8895;
        double west = -77.0352;
        double east = -77.0351;
        BoundingBox result = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(north, result.getNorth(), 0.0001);
        assertEquals(south, result.getSouth(), 0.0001);
        assertEquals(east, result.getEast(), 0.0001);
        assertEquals(west, result.getWest(), 0.0001);

        //should return 38.8895 -77.0350 -77.0361'
        usng = UsngCoordinate.parseUsngString("18S UJ 234 064");
        north = 38.8896;
        west = -77.0361;
        east = -77.0350;
        south = 38.8887;
        result = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(north, result.getNorth(), 0.0001);
        assertEquals(south, result.getSouth(), 0.0001);
        assertEquals(east, result.getEast(), 0.0001);
        assertEquals(west, result.getWest(), 0.0001);

        //should return 38.8942 -77.0406 38.8850 -77.0294'
        usng = UsngCoordinate.parseUsngString("18S UJ 23 06");
        north = 38.8942;
        west = -77.0406;
        east = -77.0294;
        south = 38.8850;
        result = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(north, result.getNorth(), 0.0001);
        assertEquals(south, result.getSouth(), 0.0001);
        assertEquals(east, result.getEast(), 0.0001);
        assertEquals(west, result.getWest(), 0.0001);

        //should return 38.9224 -77.0736 38.8304 -76.9610'
        usng = UsngCoordinate.parseUsngString("18S UJ 2 0");
        north = 38.9224;
        west = -77.0736;
        east = -76.9610;
        south = 38.8304;
        result = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(north, result.getNorth(), 0.0001);
        assertEquals(south, result.getSouth(), 0.0001);
        assertEquals(east, result.getEast(), 0.0001);
        assertEquals(west, result.getWest(), 0.0001);

        //should return 39.7440 -77.3039 38.8260 -76.1671'
        usng = UsngCoordinate.parseUsngString("18S UJ");
        north = 39.7440;
        west = -77.3039;
        east = -76.1671;
        south = 38.8260;
        result = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(north, result.getNorth(), 0.0001);
        assertEquals(south, result.getSouth(), 0.0001);
        assertEquals(east, result.getEast(), 0.0001);
        assertEquals(west, result.getWest(), 0.0001);

        //should return 40 -84 32 -78'
        usng = UsngCoordinate.parseUsngString("17S");
        north = 40;
        west = -84;
        east = -78;
        south = 32;
        result = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(north, result.getNorth(), 0.0001);
        assertEquals(south, result.getSouth(), 0.0001);
        assertEquals(east, result.getEast(), 0.0001);
        assertEquals(west, result.getWest(), 0.0001);

        //should return 32 -102 24 -96'
        usng = UsngCoordinate.parseUsngString("14R");
        north = 32;
        west = -102;
        east = -96;
        south = 24;
        result = coordinateSystemTranslator.toBoundingBox(usng);
        assertEquals(west, result.getWest(), 0);
        assertEquals(north, result.getNorth(), 0.0001);
        assertEquals(south, result.getSouth(), 0.0001);
        assertEquals(east, result.getEast(), 0.0001);
    }
}
