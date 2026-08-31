package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  private WarehouseStore warehouseStore;
  private LocationResolver locationResolver;
  private CreateWarehouseUseCase useCase;

  @BeforeEach
  void setUp() {
    warehouseStore = mock(WarehouseStore.class);
    locationResolver = mock(LocationResolver.class);
    useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
  }

  @Test
  void shouldCreateWarehouseWhenAllRulesAreSatisfied() {
    Warehouse warehouse = newWarehouse("MWH.999", "AMSTERDAM-002", 50, 10);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-002"))
        .thenReturn(new Location("AMSTERDAM-002", 3, 75));
    when(warehouseStore.findActiveByLocation("AMSTERDAM-002")).thenReturn(List.of());
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);

    useCase.create(warehouse);

    verify(warehouseStore).create(warehouse);
  }

  @Test
  void shouldRejectDuplicateBusinessUnitCode() {
    Warehouse warehouse = newWarehouse("MWH.001", "AMSTERDAM-002", 50, 10);
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(new Warehouse());

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals("Business unit code already exists: MWH.001", exception.getMessage());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  void shouldRejectInvalidLocation() {
    Warehouse warehouse = newWarehouse("MWH.999", "UNKNOWN", 50, 10);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("UNKNOWN")).thenReturn(null);

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals("Invalid location: UNKNOWN", exception.getMessage());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  void shouldRejectLocationWithTooManyWarehouses() {
    Warehouse warehouse = newWarehouse("MWH.999", "ZWOLLE-001", 10, 5);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("ZWOLLE-001"))
        .thenReturn(new Location("ZWOLLE-001", 1, 40));
    Warehouse existing = newWarehouse("MWH.001", "ZWOLLE-001", 20, 5);
    when(warehouseStore.findActiveByLocation("ZWOLLE-001")).thenReturn(List.of(existing));

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals(
        "Maximum number of warehouses reached for location: ZWOLLE-001", exception.getMessage());
  }

  @Test
  void shouldRejectCapacityExceedingLocationLimit() {
    Warehouse warehouse = newWarehouse("MWH.999", "ZWOLLE-002", 60, 5);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("ZWOLLE-002"))
        .thenReturn(new Location("ZWOLLE-002", 2, 50));
    when(warehouseStore.findActiveByLocation("ZWOLLE-002")).thenReturn(List.of());

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals(
        "Warehouse capacity exceeds the maximum capacity for location: ZWOLLE-002",
        exception.getMessage());
  }

  @Test
  void shouldRejectStockGreaterThanCapacity() {
    Warehouse warehouse = newWarehouse("MWH.999", "AMSTERDAM-002", 10, 20);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-002"))
        .thenReturn(new Location("AMSTERDAM-002", 3, 75));
    when(warehouseStore.findActiveByLocation("AMSTERDAM-002")).thenReturn(List.of());

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals("Stock exceeds warehouse capacity", exception.getMessage());
  }

  @Test
  void shouldRejectBlankBusinessUnitCode() {
    Warehouse warehouse = newWarehouse("  ", "AMSTERDAM-002", 10, 5);

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals("Business unit code is required", exception.getMessage());
  }

  @Test
  void shouldRejectBlankLocation() {
    Warehouse warehouse = newWarehouse("MWH.999", "", 10, 5);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals("Location is required", exception.getMessage());
  }

  @Test
  void shouldRejectNonPositiveCapacity() {
    Warehouse warehouse = newWarehouse("MWH.999", "AMSTERDAM-002", 0, 0);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-002"))
        .thenReturn(new Location("AMSTERDAM-002", 3, 75));
    when(warehouseStore.findActiveByLocation("AMSTERDAM-002")).thenReturn(List.of());

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals("Capacity must be greater than zero", exception.getMessage());
  }

  @Test
  void shouldRejectNegativeStock() {
    Warehouse warehouse = newWarehouse("MWH.999", "AMSTERDAM-002", 10, -1);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-002"))
        .thenReturn(new Location("AMSTERDAM-002", 3, 75));
    when(warehouseStore.findActiveByLocation("AMSTERDAM-002")).thenReturn(List.of());

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals("Stock cannot be negative", exception.getMessage());
  }

  @Test
  void shouldRejectWhenCumulativeCapacityExceedsLocationLimit() {
    Warehouse existing = newWarehouse("MWH.010", "AMSTERDAM-002", 60, 5);
    Warehouse warehouse = newWarehouse("MWH.999", "AMSTERDAM-002", 20, 5);
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-002"))
        .thenReturn(new Location("AMSTERDAM-002", 3, 75));
    when(warehouseStore.findActiveByLocation("AMSTERDAM-002"))
        .thenReturn(List.of(existing));

    WarehouseValidationException exception =
        assertThrows(WarehouseValidationException.class, () -> useCase.create(warehouse));
    assertEquals(
        "Warehouse capacity exceeds the maximum capacity for location: AMSTERDAM-002",
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
