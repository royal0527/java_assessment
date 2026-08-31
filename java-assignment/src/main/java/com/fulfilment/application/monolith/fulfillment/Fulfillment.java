package com.fulfilment.application.monolith.fulfillment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Fulfillment {

  @Id @GeneratedValue public Long id;

  public Long storeId;

  public Long productId;

  public Long warehouseId;

  public Fulfillment() {}

  public Fulfillment(Long storeId, Long productId, Long warehouseId) {
    this.storeId = storeId;
    this.productId = productId;
    this.warehouseId = warehouseId;
  }
}
