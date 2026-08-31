package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

  @Test
  public void testGetUnknownProductReturnsNotFound() {
    given().when().get("product/99999").then().statusCode(404);
  }

  @Test
  public void testCrudProduct() {
    final String path = "product";

    // Create a dedicated product for this test
    given()
        .contentType("application/json")
        .body("{\"name\":\"EKTORP\",\"stock\":7}")
        .when()
        .post(path)
        .then()
        .statusCode(201);

    // List all should contain the new product and seed products
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(
            containsString("EKTORP"),
            containsString("TONSTAD"),
            containsString("KALLAX"),
            containsString("BESTÅ"));

    // Delete the dedicated product by name lookup
    int id =
        given()
            .when()
            .get(path)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getInt("find { it.name == 'EKTORP' }.id");

    given().when().delete(path + "/" + id).then().statusCode(204);

    // List all, EKTORP should be missing now, seed products remain
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(
            not(containsString("EKTORP")),
            containsString("TONSTAD"),
            containsString("KALLAX"),
            containsString("BESTÅ"));
  }
}
