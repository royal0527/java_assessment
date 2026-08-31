package com.fulfilment.application.monolith.stores;

public record StoreEvent(Store store, StoreEventType type) {

  public enum StoreEventType {
    CREATE,
    UPDATE
  }
}
