package com.fulfilment.application.monolith.stores;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StoreEventListenerTest {

  private LegacyStoreManagerGateway legacyStoreManagerGateway;
  private StoreEventListener listener;

  @BeforeEach
  void setUp() {
    legacyStoreManagerGateway = mock(LegacyStoreManagerGateway.class);
    listener = new StoreEventListener();
    listener.legacyStoreManagerGateway = legacyStoreManagerGateway;
  }

  @Test
  void shouldCallCreateOnLegacySystemForCreateEvent() {
    Store store = new Store("Test Store");
    store.quantityProductsInStock = 42;

    listener.handleStoreEvent(new StoreEvent(store, StoreEvent.StoreEventType.CREATE));

    verify(legacyStoreManagerGateway).createStoreOnLegacySystem(store);
  }

  @Test
  void shouldCallUpdateOnLegacySystemForUpdateEvent() {
    Store store = new Store("Test Store");
    store.quantityProductsInStock = 10;

    listener.handleStoreEvent(new StoreEvent(store, StoreEvent.StoreEventType.UPDATE));

    verify(legacyStoreManagerGateway).updateStoreOnLegacySystem(store);
  }
}
