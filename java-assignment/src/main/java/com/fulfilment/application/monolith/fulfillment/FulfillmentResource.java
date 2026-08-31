package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentResource {

  @Inject FulfillmentRepository fulfillmentRepository;
  @Inject ProductRepository productRepository;
  @Inject WarehouseRepository warehouseRepository;

  @GET
  public List<Fulfillment> list() {
    return fulfillmentRepository.listAll();
  }

  @POST
  @Transactional
  public Response create(Fulfillment fulfillment) {
    validate(fulfillment);
    fulfillmentRepository.persist(fulfillment);
    return Response.status(201).entity(fulfillment).build();
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(@PathParam("id") Long id) {
    Fulfillment entity = fulfillmentRepository.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Fulfillment with id of " + id + " does not exist.", 404);
    }
    fulfillmentRepository.delete(entity);
    return Response.status(204).build();
  }

  private void validate(Fulfillment fulfillment) {
    if (fulfillment.storeId == null) {
      throw new WebApplicationException("Store id is required", 422);
    }
    if (fulfillment.productId == null) {
      throw new WebApplicationException("Product id is required", 422);
    }
    if (fulfillment.warehouseId == null) {
      throw new WebApplicationException("Warehouse id is required", 422);
    }

    if (Store.findById(fulfillment.storeId) == null) {
      throw new WebApplicationException("Store not found: " + fulfillment.storeId, 404);
    }
    if (productRepository.findById(fulfillment.productId) == null) {
      throw new WebApplicationException("Product not found: " + fulfillment.productId, 404);
    }
    if (warehouseRepository.findById(fulfillment.warehouseId) == null) {
      throw new WebApplicationException("Warehouse not found: " + fulfillment.warehouseId, 404);
    }

    if (fulfillmentRepository.countByStoreAndProduct(fulfillment.storeId, fulfillment.productId)
        >= 2) {
      throw new WebApplicationException(
          "A product can be fulfilled by a maximum of 2 warehouses per store", 422);
    }
    if (fulfillmentRepository.countDistinctWarehousesByStore(fulfillment.storeId) >= 3) {
      throw new WebApplicationException(
          "A store can be fulfilled by a maximum of 3 warehouses", 422);
    }
    if (fulfillmentRepository.countDistinctProductsByWarehouse(fulfillment.warehouseId) >= 5) {
      throw new WebApplicationException(
          "A warehouse can store a maximum of 5 different products", 422);
    }
  }
}
