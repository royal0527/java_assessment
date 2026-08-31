package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class WarehouseValidationException extends IllegalArgumentException {

  public WarehouseValidationException(String message) {
    super(message);
  }
}
