package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.list("archivedAt is null").stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse entity = new DbWarehouse();
    entity.businessUnitCode = warehouse.businessUnitCode;
    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = warehouse.createdAt != null ? warehouse.createdAt : LocalDateTime.now();
    entity.archivedAt = warehouse.archivedAt;
    persist(entity);
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse entity = findByBusinessUnitCodeEntity(warehouse.businessUnitCode);
    if (entity != null) {
      entity.location = warehouse.location;
      entity.capacity = warehouse.capacity;
      entity.stock = warehouse.stock;
      entity.createdAt = warehouse.createdAt;
      entity.archivedAt = warehouse.archivedAt;
      persist(entity);
    }
  }

  @Override
  public void remove(Warehouse warehouse) {
    delete("businessUnitCode", warehouse.businessUnitCode);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse entity = findByBusinessUnitCodeEntity(buCode);
    return entity != null ? entity.toWarehouse() : null;
  }

  @Override
  public Warehouse findByDatabaseId(Long id) {
    DbWarehouse entity = findById(id);
    return entity != null ? entity.toWarehouse() : null;
  }

  @Override
  public List<Warehouse> findActiveByLocation(String location) {
    return list("location = ?1 and archivedAt is null", location).stream()
        .map(DbWarehouse::toWarehouse)
        .toList();
  }

  @Override
  public long countActiveByLocation(String location) {
    return count("location = ?1 and archivedAt is null", location);
  }

  @Override
  public int sumCapacityByLocation(String location) {
    return list("location = ?1 and archivedAt is null", location).stream()
        .mapToInt(w -> w.capacity != null ? w.capacity : 0)
        .sum();
  }

  private DbWarehouse findByBusinessUnitCodeEntity(String buCode) {
    return find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
  }
}
