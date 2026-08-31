package com.fulfilment.application.monolith.fulfillment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FulfillmentResourceTest {

  @Test
  public void testListFulfillmentsInitiallyEmpty() {
    given().when().get("fulfillment").then().statusCode(200).body(containsString("[]"));
  }

  @Test
  public void testCreateFulfillment() {
    String payload = "{\"storeId\":1,\"productId\":1,\"warehouseId\":1}";

    given()
        .contentType("application/json")
        .body(payload)
        .when()
        .post("fulfillment")
        .then()
        .statusCode(201)
        .body(containsString("\"storeId\":1"));
  }

  @Test
  public void testCreateFulfillmentWithMissingStore() {
    String payload = "{\"productId\":1,\"warehouseId\":1}";

    given().contentType("application/json").body(payload).when().post("fulfillment").then().statusCode(422);
  }

  @Test
  public void testCreateFulfillmentWithUnknownEntities() {
    String payload = "{\"storeId\":99999,\"productId\":1,\"warehouseId\":1}";

    given().contentType("application/json").body(payload).when().post("fulfillment").then().statusCode(404);
  }

  @Test
  public void testProductMaxTwoWarehousesPerStore() {
    given()
        .contentType("application/json")
        .body("{\"storeId\":2,\"productId\":2,\"warehouseId\":1}")
        .when()
        .post("fulfillment")
        .then()
        .statusCode(201);

    given()
        .contentType("application/json")
        .body("{\"storeId\":2,\"productId\":2,\"warehouseId\":2}")
        .when()
        .post("fulfillment")
        .then()
        .statusCode(201);

    given()
        .contentType("application/json")
        .body("{\"storeId\":2,\"productId\":2,\"warehouseId\":3}")
        .when()
        .post("fulfillment")
        .then()
        .statusCode(422);
  }

  @Test
  public void testStoreMaxThreeWarehouses() {
    given()
        .contentType("application/json")
        .body("{\"storeId\":3,\"productId\":1,\"warehouseId\":1}")
        .when()
        .post("fulfillment")
        .then()
        .statusCode(201);

    given()
        .contentType("application/json")
        .body("{\"storeId\":3,\"productId\":2,\"warehouseId\":2}")
        .when()
        .post("fulfillment")
        .then()
        .statusCode(201);

    given()
        .contentType("application/json")
        .body("{\"storeId\":3,\"productId\":3,\"warehouseId\":3}")
        .when()
        .post("fulfillment")
        .then()
        .statusCode(201);

    given()
        .contentType("application/json")
        .body("{\"storeId\":3,\"productId\":1,\"warehouseId\":2}")
        .when()
        .post("fulfillment")
        .then()
        .statusCode(422);
  }

  @Test
  public void testWarehouseMaxFiveProducts() {
    String warehousePayload =
        "{\"businessUnitCode\":\"MWH.900\",\"location\":\"AMSTERDAM-002\",\"capacity\":30,\"stock\":0}";
    given()
        .contentType("application/json")
        .body(warehousePayload)
        .when()
        .post("warehouse")
        .then()
        .statusCode(201);
    int warehouseId =
        given()
            .when()
            .get("warehouse")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getInt("find { it.businessUnitCode == 'MWH.900' }.id");

    int product4 = createProduct("P4");
    int product5 = createProduct("P5");
    int product6 = createProduct("P6");

    createFulfillment(1, 1, warehouseId);
    createFulfillment(1, 2, warehouseId);
    createFulfillment(1, 3, warehouseId);
    createFulfillment(1, product4, warehouseId);
    createFulfillment(1, product5, warehouseId);

    given()
        .contentType("application/json")
        .body(
            String.format(
                "{\"storeId\":1,\"productId\":%d,\"warehouseId\":%d}", product6, warehouseId))
        .when()
        .post("fulfillment")
        .then()
        .statusCode(422);
  }

  private int createProduct(String name) {
    given()
        .contentType("application/json")
        .body("{\"name\":\"" + name + "\",\"stock\":1}")
        .when()
        .post("product")
        .then()
        .statusCode(201);
    return given()
        .when()
        .get("product")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath()
        .getInt("find { it.name == '" + name + "' }.id");
  }

  private void createFulfillment(int storeId, int productId, int warehouseId) {
    given()
        .contentType("application/json")
        .body(
            String.format(
                "{\"storeId\":%d,\"productId\":%d,\"warehouseId\":%d}",
                storeId, productId, warehouseId))
        .when()
        .post("fulfillment")
        .then()
        .statusCode(201);
  }
}
