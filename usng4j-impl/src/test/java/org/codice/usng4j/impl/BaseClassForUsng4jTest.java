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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.junit.BeforeClass;

public abstract class BaseClassForUsng4jTest {
  static final List<UtmUpsTestData> validUpsCoordinatesTests = new ArrayList<>();
  static final List<UtmUpsTestData> validUtmCoordinatesTests = new ArrayList<>();
  static UtmUpsTestData expectedTestDataSingleCoordinate;

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
    expectedTestDataSingleCoordinate = validUpsCoordinatesTests.get(0);
    // TODO https://github.com/codice/usng4j/issues/9: add a set of valid and invalid UTM
    // coordinates test data
  }

  void runTestWithWithData(
      final Consumer<UtmUpsTestData> testDataConsumer, final List<UtmUpsTestData> testDataList) {
    final List<String> errorMessages = runTestsCollectErrors(testDataConsumer, testDataList);
    if (!errorMessages.isEmpty()) {
      fail(
          "Tests failures:\n"
              + errorMessages.stream().map(String::toString).collect(Collectors.joining("\n")));
    }
  }

  private List<String> runTestsCollectErrors(
      final Consumer<UtmUpsTestData> testDataConsumer, final List<UtmUpsTestData> testDataList) {
    return testDataList
        .stream()
        .map(
            testData -> {
              try {
                testDataConsumer.accept(testData);
                return Optional.<String>empty();
              } catch (AssertionError failure) {
                return Optional.of(
                    failure.getMessage() + String.format("\n\t\t[Input Data:  %s]", testData));
              }
            })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }
}
