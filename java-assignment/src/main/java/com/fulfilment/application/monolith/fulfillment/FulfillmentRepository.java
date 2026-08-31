package com.fulfilment.application.monolith.fulfillment;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FulfillmentRepository implements PanacheRepository<Fulfillment> {

  public long countByStoreAndProduct(Long storeId, Long productId) {
    return count("storeId = ?1 and productId = ?2", storeId, productId);
  }

  public long countDistinctWarehousesByStore(Long storeId) {
    return count("select count(distinct warehouseId) from Fulfillment where storeId = ?1", storeId);
  }

  public long countDistinctProductsByWarehouse(Long warehouseId) {
    return count("select count(distinct productId) from Fulfillment where warehouseId = ?1", warehouseId);
  }
}
