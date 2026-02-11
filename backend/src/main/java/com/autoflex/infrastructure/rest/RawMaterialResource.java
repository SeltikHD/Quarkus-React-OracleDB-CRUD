package com.autoflex.infrastructure.rest;

import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import com.autoflex.domain.port.in.RawMaterialUseCase;
import com.autoflex.infrastructure.rest.dto.RawMaterialRequest;
import com.autoflex.infrastructure.rest.dto.RawMaterialResponse;
import com.autoflex.infrastructure.rest.dto.StockAdjustmentRequest;
import com.autoflex.infrastructure.rest.mapper.RawMaterialRestMapper;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/** REST resource for raw material management operations. */
@Path("/api/v1/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Raw Materials", description = "Raw material management operations")
public class RawMaterialResource {

  private final RawMaterialUseCase rawMaterialUseCase;
  private final RawMaterialRestMapper mapper;

  @Inject
  public RawMaterialResource(RawMaterialUseCase rawMaterialUseCase, RawMaterialRestMapper mapper) {
    this.rawMaterialUseCase = rawMaterialUseCase;
    this.mapper = mapper;
  }

  @GET
  @Operation(summary = "List all raw materials")
  public List<RawMaterialResponse> listRawMaterials(
      @QueryParam("includeInactive") @DefaultValue("false") boolean includeInactive,
      @QueryParam("search") String search) {
    List<RawMaterial> rawMaterials;
    if (search != null && !search.isBlank()) {
      rawMaterials = rawMaterialUseCase.searchRawMaterials(search);
    } else if (includeInactive) {
      rawMaterials = rawMaterialUseCase.listAllRawMaterials();
    } else {
      rawMaterials = rawMaterialUseCase.listActiveRawMaterials();
    }
    return rawMaterials.stream().map(mapper::toResponse).toList();
  }

  @GET
  @Path("/{id}")
  @Operation(summary = "Get raw material by ID")
  public RawMaterialResponse getRawMaterial(@PathParam("id") Long id) {
    RawMaterial rawMaterial = rawMaterialUseCase.getRawMaterialById(RawMaterialId.of(id));
    return mapper.toResponse(rawMaterial);
  }

  @POST
  @Operation(summary = "Create raw material")
  public Response createRawMaterial(@Valid RawMaterialRequest request) {
    RawMaterialUseCase.CreateRawMaterialCommand command = mapper.toCreateCommand(request);
    RawMaterial created = rawMaterialUseCase.createRawMaterial(command);
    RawMaterialResponse response = mapper.toResponse(created);
    return Response.created(URI.create("/api/v1/raw-materials/" + created.getId().value()))
        .entity(response)
        .build();
  }

  @PUT
  @Path("/{id}")
  @Operation(summary = "Update raw material")
  public RawMaterialResponse updateRawMaterial(
      @PathParam("id") Long id, @Valid RawMaterialRequest request) {
    RawMaterialUseCase.UpdateRawMaterialCommand command = mapper.toUpdateCommand(request);
    RawMaterial updated = rawMaterialUseCase.updateRawMaterial(RawMaterialId.of(id), command);
    return mapper.toResponse(updated);
  }

  @PATCH
  @Path("/{id}/stock")
  @Operation(summary = "Adjust raw material stock")
  public RawMaterialResponse adjustStock(
      @PathParam("id") Long id, @Valid StockAdjustmentRequest request) {
    RawMaterial updated =
        rawMaterialUseCase.adjustStock(RawMaterialId.of(id), request.getQuantity());
    return mapper.toResponse(updated);
  }

  @DELETE
  @Path("/{id}")
  @Operation(summary = "Delete or deactivate raw material")
  public Response deleteRawMaterial(
      @PathParam("id") Long id, @QueryParam("permanent") @DefaultValue("false") boolean permanent) {
    RawMaterialId rawMaterialId = RawMaterialId.of(id);
    if (permanent) {
      rawMaterialUseCase.deleteRawMaterial(rawMaterialId);
    } else {
      rawMaterialUseCase.deactivateRawMaterial(rawMaterialId);
    }
    return Response.noContent().build();
  }
}
