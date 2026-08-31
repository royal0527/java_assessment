package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class StoreEventListener {

  @Inject LegacyStoreManagerGateway legacyStoreManagerGateway;

  public void handleStoreEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreEvent event) {
    switch (event.type()) {
      case CREATE -> legacyStoreManagerGateway.createStoreOnLegacySystem(event.store());
      case UPDATE -> legacyStoreManagerGateway.updateStoreOnLegacySystem(event.store());
    }
  }
}
