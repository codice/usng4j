package org.codice.usng4j.impl;

import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.Test;

public class UtmUpsCoordinateImplTest {

  private Gson gson = new GsonBuilder().create();

  private InputStreamReader reader(String file) throws IOException {
    return new InputStreamReader(UtmUpsCoordinateImplTest.class.getResourceAsStream(file), "UTF-8");
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
    UtmUpsCoordinateImpl[] upsObjects =
        gson.fromJson(reader("/ValidUpsCoordinates.json"), UtmUpsCoordinateImpl[].class);
    for (UtmUpsCoordinateImpl upsCoordinate : upsObjects) {
      System.out.println(upsCoordinate.toString());
    }
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
