package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreResourceTest {

  @Test
  public void testCreateAndUpdateStorePropagatesToLegacySystem() {
    String path = "store";

    given()
        .contentType("application/json")
        .body("{\"name\":\"NORRÅKER\",\"quantityProductsInStock\":12}")
        .when()
        .post(path)
        .then()
        .statusCode(201)
        .body(containsString("NORRÅKER"));

    int id =
        given()
            .when()
            .get(path)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getInt("find { it.name == 'NORRÅKER' }.id");

    given()
        .contentType("application/json")
        .body("{\"name\":\"NORRÅKER-UPDATED\",\"quantityProductsInStock\":15}")
        .when()
        .put(path + "/" + id)
        .then()
        .statusCode(200)
        .body(containsString("NORRÅKER-UPDATED"));
  }

  @Test
  public void testGetUnknownStoreReturnsNotFound() {
    given().when().get("store/99999").then().statusCode(404);
  }

  @Test
  public void testCreateStoreWithInvalidIdFails() {
    given()
        .contentType("application/json")
        .body("{\"id\":99,\"name\":\"Invalid\",\"quantityProductsInStock\":12}")
        .when()
        .post("store")
        .then()
        .statusCode(422);
  }
}
