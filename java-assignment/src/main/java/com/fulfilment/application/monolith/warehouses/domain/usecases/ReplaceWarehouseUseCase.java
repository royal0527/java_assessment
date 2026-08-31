package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final CreateWarehouseUseCase createWarehouseUseCase;

  @Inject
  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore, CreateWarehouseUseCase createWarehouseUseCase) {
    this.warehouseStore = warehouseStore;
    this.createWarehouseUseCase = createWarehouseUseCase;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {
    Warehouse current = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (current == null) {
      throw new WarehouseNotFoundException(
          "Active warehouse not found: " + newWarehouse.businessUnitCode);
    }

    if (!current.stock.equals(newWarehouse.stock)) {
      throw new WarehouseValidationException(
          "Stock of the new warehouse must match the stock of the warehouse being replaced");
    }

    if (newWarehouse.capacity < current.stock) {
      throw new WarehouseValidationException(
          "New warehouse capacity must accommodate the stock of the warehouse being replaced");
    }

    createWarehouseUseCase.validateNewWarehouse(newWarehouse, current.businessUnitCode);

    current.archivedAt = LocalDateTime.now();
    warehouseStore.update(current);

    newWarehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(newWarehouse);
  }
}
