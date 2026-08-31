package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreResourceErrorMapperTest {

  @Inject StoreResource.ErrorMapper mapper;

  @Test
  public void testMapsWebApplicationExceptionToItsStatus() {
    Response response = mapper.toResponse(new WebApplicationException("Not found", 404));

    assertEquals(404, response.getStatus());
  }

  @Test
  public void testMapsGenericExceptionToInternalServerError() {
    Response response = mapper.toResponse(new RuntimeException("Boom"));

    assertEquals(500, response.getStatus());
  }
}
