package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseEndpointTest {

  @Test
  public void testListWarehouses() {
    given()
        .when()
        .get("warehouse")
        .then()
        .statusCode(200)
        .body(containsString("MWH.012"), containsString("AMSTERDAM-001"));
  }

  @Test
  public void testGetWarehouseById() {
    given()
        .when()
        .get("warehouse/2")
        .then()
        .statusCode(200)
        .body(containsString("MWH.012"), containsString("AMSTERDAM-001"));
  }

  @Test
  public void testGetWarehouseByUnknownId() {
    given().when().get("warehouse/99999").then().statusCode(404);
  }

  @Test
  public void testCreateWarehouse() {
    String payload =
        "{\"businessUnitCode\":\"MWH.100\",\"location\":\"AMSTERDAM-002\",\"capacity\":20,\"stock\":10}";

    given()
        .contentType("application/json")
        .body(payload)
        .when()
        .post("warehouse")
        .then()
        .statusCode(201)
        .body(containsString("MWH.100"), containsString("AMSTERDAM-002"));
  }

  @Test
  public void testCreateWarehouseWithInvalidLocation() {
    String payload =
        "{\"businessUnitCode\":\"MWH.101\",\"location\":\"NOWHERE\",\"capacity\":20,\"stock\":10}";

    given().contentType("application/json").body(payload).when().post("warehouse").then().statusCode(400);
  }

  @Test
  public void testCreateWarehouseWithDuplicateBusinessUnitCode() {
    String payload =
        "{\"businessUnitCode\":\"MWH.001\",\"location\":\"AMSTERDAM-002\",\"capacity\":20,\"stock\":10}";

    given().contentType("application/json").body(payload).when().post("warehouse").then().statusCode(400);
  }

  @Test
  public void testArchiveWarehouse() {
    given().when().delete("warehouse/1").then().statusCode(204);

    given()
        .when()
        .get("warehouse")
        .then()
        .statusCode(200)
        .body(
            not(containsString("MWH.001")),
            containsString("MWH.012"),
            containsString("MWH.023"));
  }

  @Test
  public void testReplaceWarehouse() {
    String payload =
        "{\"location\":\"AMSTERDAM-001\",\"capacity\":60,\"stock\":5}";

    given()
        .contentType("application/json")
        .body(payload)
        .when()
        .post("warehouse/MWH.012/replacement")
        .then()
        .statusCode(200)
        .body(containsString("MWH.012"), containsString("AMSTERDAM-001"));
  }
}
