package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {
    validateNewWarehouse(warehouse, null);

    warehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(warehouse);
  }

  void validateNewWarehouse(Warehouse warehouse, String excludedBusinessUnitCode) {
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new WarehouseValidationException("Business unit code is required");
    }

    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null
        && (excludedBusinessUnitCode == null
            || !excludedBusinessUnitCode.equals(warehouse.businessUnitCode))) {
      throw new WarehouseValidationException(
          "Business unit code already exists: " + warehouse.businessUnitCode);
    }

    if (warehouse.location == null || warehouse.location.isBlank()) {
      throw new WarehouseValidationException("Location is required");
    }

    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new WarehouseValidationException("Invalid location: " + warehouse.location);
    }

    long activeAtLocation =
        warehouseStore.findActiveByLocation(warehouse.location).stream()
            .filter(
                existing ->
                    excludedBusinessUnitCode == null
                        || !excludedBusinessUnitCode.equals(existing.businessUnitCode))
            .count();
    if (activeAtLocation >= location.maxNumberOfWarehouses) {
      throw new WarehouseValidationException(
          "Maximum number of warehouses reached for location: " + warehouse.location);
    }

    if (warehouse.capacity == null || warehouse.capacity <= 0) {
      throw new WarehouseValidationException("Capacity must be greater than zero");
    }

    int usedCapacityAtLocation =
        warehouseStore.findActiveByLocation(warehouse.location).stream()
            .filter(
                existing ->
                    excludedBusinessUnitCode == null
                        || !excludedBusinessUnitCode.equals(existing.businessUnitCode))
            .mapToInt(existing -> existing.capacity != null ? existing.capacity : 0)
            .sum();
    if (warehouse.capacity > location.maxCapacity
        || usedCapacityAtLocation + warehouse.capacity > location.maxCapacity) {
      throw new WarehouseValidationException(
          "Warehouse capacity exceeds the maximum capacity for location: " + warehouse.location);
    }

    if (warehouse.stock == null || warehouse.stock < 0) {
      throw new WarehouseValidationException("Stock cannot be negative");
    }
    if (warehouse.stock > warehouse.capacity) {
      throw new WarehouseValidationException("Stock exceeds warehouse capacity");
    }
  }
}
