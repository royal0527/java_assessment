package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArchiveWarehouseUseCaseTest {

  private WarehouseStore warehouseStore;
  private ArchiveWarehouseUseCase useCase;

  @BeforeEach
  void setUp() {
    warehouseStore = mock(WarehouseStore.class);
    useCase = new ArchiveWarehouseUseCase(warehouseStore);
  }

  @Test
  void shouldArchiveExistingWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.001";
    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(warehouse);

    useCase.archive(warehouse);

    assertNotNull(warehouse.archivedAt);
    verify(warehouseStore).update(warehouse);
  }

  @Test
  void shouldThrowWhenWarehouseDoesNotExist() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.999";
    when(warehouseStore.findByBusinessUnitCode("MWH.999")).thenReturn(null);

    WarehouseNotFoundException exception =
        assertThrows(WarehouseNotFoundException.class, () -> useCase.archive(warehouse));
    assertEquals("Warehouse not found: MWH.999", exception.getMessage());
  }
}
