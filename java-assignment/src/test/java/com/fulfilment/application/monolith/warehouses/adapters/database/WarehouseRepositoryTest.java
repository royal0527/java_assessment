package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Transactional
public class WarehouseRepositoryTest {

  @Inject WarehouseRepository warehouseRepository;

  @Test
  public void testFindByBusinessUnitCode() {
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode("MWH.001");
    assertNotNull(warehouse);
    assertEquals("ZWOLLE-001", warehouse.location);
  }

  @Test
  public void testFindByDatabaseId() {
    Warehouse warehouse = warehouseRepository.findByDatabaseId(1L);
    assertNotNull(warehouse);
    assertEquals("MWH.001", warehouse.businessUnitCode);
  }

  @Test
  public void testFindActiveByLocation() {
    List<Warehouse> warehouses = warehouseRepository.findActiveByLocation("AMSTERDAM-001");
    assertEquals(1, warehouses.size());
    assertEquals("MWH.012", warehouses.get(0).businessUnitCode);
  }

  @Test
  public void testCountActiveByLocation() {
    long count = warehouseRepository.countActiveByLocation("TILBURG-001");
    assertEquals(1, count);
  }

  @Test
  public void testSumCapacityByLocation() {
    int capacity = warehouseRepository.sumCapacityByLocation("ZWOLLE-001");
    assertEquals(100, capacity);
  }

  @Test
  public void testUpdateAndArchive() {
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode("MWH.023");
    warehouse.stock = 25;
    warehouseRepository.update(warehouse);

    Warehouse updated = warehouseRepository.findByBusinessUnitCode("MWH.023");
    assertEquals(25, updated.stock);
  }

  @Test
  public void testRemove() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "TEMP.001";
    warehouse.location = "AMSTERDAM-001";
    warehouse.capacity = 5;
    warehouse.stock = 0;
    warehouseRepository.create(warehouse);

    Warehouse created = warehouseRepository.findByBusinessUnitCode("TEMP.001");
    assertNotNull(created);

    warehouseRepository.remove(created);
    assertNull(warehouseRepository.findByBusinessUnitCode("TEMP.001"));
  }

  @Test
  public void testGetAll() {
    List<Warehouse> warehouses = warehouseRepository.getAll();
    assertTrue(warehouses.size() >= 3);
  }
}
