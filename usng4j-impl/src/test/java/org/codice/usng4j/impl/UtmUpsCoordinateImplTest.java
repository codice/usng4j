package org.codice.usng4j.impl;

import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtmUpsCoordinateImplTest {

  class UtmUpsTestData {
    final double latitude;
    final double longitude;
    final double easting;
    final double northing;
    final boolean northPole;

    UtmUpsTestData(
        final double latitude,
        final double longitude,
        final double easting,
        final double northing,
        final boolean northPole) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.easting = easting;
      this.northing = northing;
      this.northPole = northPole;
    }

    @Override
    public String toString() {
      return String.format(
          "Lat: %f,  Lon: %f,  E: %f,  N: %f,  NP: %b",
          latitude, longitude, easting, northing, northPole);
    }
  }

  private static final List<UtmUpsTestData> validUpsCoordinatesTests = new ArrayList<>();
  private static final List<UtmUpsTestData> validUtmCoordinatesTests = new ArrayList<>();

  private static InputStreamReader reader(final String fileName) throws IOException {
    return new InputStreamReader(
        UtmUpsCoordinateImplTest.class.getResourceAsStream(fileName), "UTF-8");
  }

  @BeforeClass
  public static void allTestsSetup() throws IOException {
    final Gson gson = new GsonBuilder().create();
    final Type testDataListType = new TypeToken<List<UtmUpsTestData>>() {}.getType();
    validUpsCoordinatesTests.addAll(
        gson.fromJson(reader("/ValidUpsCoordinates.json"), testDataListType));
    // TODO:  add a set of valid UTM coordinates test data
    // validUtmCoordinatesTests.addAll(gson.fromJson(
    //  reader("/ValidUtmCoordinates.json"), testDataListType));
    // TODO:  add a set of invalid UPS and UTM coordinates test data
    // TODO:  add reading invalid tests
  }

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
  public void testParsingUtmOverlappingUpsAsUtmUps() {
    fail();
  }

  @Test
  public void testParsingUpsOverlappingUtmAsUtmUps() {
    fail();
  }
}
