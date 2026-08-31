package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReplaceWarehouseUseCaseTest {

  private WarehouseStore warehouseStore;
  private LocationResolver locationResolver;
  private ReplaceWarehouseUseCase replaceUseCase;

  @BeforeEach
  void setUp() {
    warehouseStore = mock(WarehouseStore.class);
    locationResolver = mock(LocationResolver.class);
    CreateWarehouseUseCase createUseCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
    replaceUseCase = new ReplaceWarehouseUseCase(warehouseStore, createUseCase);
  }

  @Test
  void shouldReplaceWarehouseWhenAllRulesAreSatisfied() {
    Warehouse current = newWarehouse("MWH.001", "AMSTERDAM-001", 50, 10);
    Warehouse replacement = newWarehouse("MWH.001", "AMSTERDAM-001", 60, 10);

    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(current);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
        .thenReturn(new Location("AMSTERDAM-001", 5, 100));
    when(warehouseStore.findActiveByLocation("AMSTERDAM-001"))
        .thenReturn(List.of(current));

    replaceUseCase.replace(replacement);

    assertNotNull(current.archivedAt);
    verify(warehouseStore).update(current);
    verify(warehouseStore).create(replacement);
  }

  @Test
  void shouldThrowWhenCurrentWarehouseDoesNotExist() {
    Warehouse replacement = newWarehouse("MWH.999", "AMSTERDAM-001", 60, 10);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);

    WarehouseNotFoundException exception =
        assertThrows(WarehouseNotFoundException.class, () -> replaceUseCase.replace(replacement));
    assertEquals("Active warehouse not found: MWH.999", exception.getMessage());
  }

  @Test
  void shouldThrowWhenStockDoesNotMatch() {
    Warehouse current = newWarehouse("MWH.001", "AMSTERDAM-001", 50, 10);
    Warehouse replacement = newWarehouse("MWH.001", "AMSTERDAM-001", 60, 20);
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(current);

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> replaceUseCase.replace(replacement));
    assertEquals(
        "Stock of the new warehouse must match the stock of the warehouse being replaced",
        exception.getMessage());
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  void shouldThrowWhenNewCapacityCannotAccommodateCurrentStock() {
    Warehouse current = newWarehouse("MWH.001", "AMSTERDAM-001", 50, 40);
    Warehouse replacement = newWarehouse("MWH.001", "AMSTERDAM-001", 30, 40);
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(current);

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> replaceUseCase.replace(replacement));
    assertEquals(
        "New warehouse capacity must accommodate the stock of the warehouse being replaced",
        exception.getMessage());
  }

  private Warehouse newWarehouse(
      String businessUnitCode, String location, int capacity, int stock) {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }
}
