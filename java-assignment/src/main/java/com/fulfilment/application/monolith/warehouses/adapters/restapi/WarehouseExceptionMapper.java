package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WarehouseExceptionMapper
    implements ExceptionMapper<RuntimeException> {

  @Override
  public Response toResponse(RuntimeException exception) {
    int status;
    if (exception instanceof WarehouseValidationException) {
      status = Response.Status.BAD_REQUEST.getStatusCode();
    } else if (exception instanceof WarehouseNotFoundException) {
      status = Response.Status.NOT_FOUND.getStatusCode();
    } else if (exception instanceof WebApplicationException webApplicationException) {
      status = webApplicationException.getResponse().getStatus();
    } else {
      status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }
    return Response.status(status).entity(exception.getMessage()).build();
  }
}
