package org.codice.usng4j.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;

public abstract class BaseClassForUsng4jTest {
  static final List<UtmUpsTestData> validUpsCoordinatesTests = new ArrayList<>();
  static final List<UtmUpsTestData> validUtmCoordinatesTests = new ArrayList<>();

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
}
