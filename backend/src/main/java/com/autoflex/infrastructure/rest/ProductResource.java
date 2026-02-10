package com.autoflex.infrastructure.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.product.ProductId;
import com.autoflex.domain.port.in.ProductUseCase;
import com.autoflex.infrastructure.rest.dto.ProductRequest;
import com.autoflex.infrastructure.rest.dto.ProductResponse;
import com.autoflex.infrastructure.rest.mapper.ProductRestMapper;

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

/**
 * ProductResource - REST API adapter for Product operations.
 *
 * <p><b>HEXAGONAL ARCHITECTURE:</b>
 * This is an INPUT ADAPTER (also called "driving adapter" or "primary adapter").
 * It receives HTTP requests and translates them to domain use case calls.
 *
 * <p><b>RESPONSIBILITIES:</b>
 * <ul>
 *   <li>HTTP request/response handling</li>
 *   <li>DTO to domain command mapping</li>
 *   <li>Domain entity to DTO response mapping</li>
 *   <li>OpenAPI documentation</li>
 * </ul>
 */
@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "Product management operations")
public class ProductResource {

    private final ProductUseCase productUseCase;
    private final ProductRestMapper mapper;

    @Inject
    public ProductResource(ProductUseCase productUseCase, ProductRestMapper mapper) {
        this.productUseCase = productUseCase;
        this.mapper = mapper;
    }

    @GET
    @Operation(summary = "List all products", description = "Retrieves a list of all active products")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Products retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(type = SchemaType.ARRAY, implementation = ProductResponse.class)
            )
        )
    })
    public List<ProductResponse> listProducts(
            @QueryParam("includeInactive") @DefaultValue("false") boolean includeInactive,
            @QueryParam("search") String search) {
        
        List<Product> products;
        
        if (search != null && !search.isBlank()) {
            products = productUseCase.searchProducts(search);
        } else if (includeInactive) {
            products = productUseCase.listAllProducts();
        } else {
            products = productUseCase.listActiveProducts();
        }
        
        return products.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its ID")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Product found",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @APIResponse(responseCode = "404", description = "Product not found")
    })
    public ProductResponse getProduct(
            @Parameter(description = "Product ID", required = true)
            @PathParam("id") Long id) {
        Product product = productUseCase.getProductById(ProductId.of(id));
        return mapper.toResponse(product);
    }

    @POST
    @Operation(summary = "Create product", description = "Creates a new product")
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid request"),
        @APIResponse(responseCode = "409", description = "Product with SKU already exists")
    })
    public Response createProduct(@Valid ProductRequest request) {
        ProductUseCase.CreateProductCommand command = mapper.toCreateCommand(request);
        Product created = productUseCase.createProduct(command);
        ProductResponse response = mapper.toResponse(created);
        
        return Response.created(URI.create("/api/v1/products/" + created.getId().value()))
                .entity(response)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update product", description = "Updates an existing product")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid request"),
        @APIResponse(responseCode = "404", description = "Product not found"),
        @APIResponse(responseCode = "409", description = "Product with SKU already exists")
    })
    public ProductResponse updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathParam("id") Long id,
            @Valid ProductRequest request) {
        
        ProductUseCase.UpdateProductCommand command = mapper.toUpdateCommand(request);
        Product updated = productUseCase.updateProduct(ProductId.of(id), command);
        return mapper.toResponse(updated);
    }

    @PATCH
    @Path("/{id}/stock")
    @Operation(summary = "Adjust stock", description = "Adjusts the stock quantity of a product")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Stock adjusted successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid quantity or insufficient stock"),
        @APIResponse(responseCode = "404", description = "Product not found")
    })
    public ProductResponse adjustStock(
            @Parameter(description = "Product ID", required = true)
            @PathParam("id") Long id,
            @Parameter(description = "Quantity to add (positive) or remove (negative)", required = true)
            @QueryParam("delta") int delta) {
        
        Product updated = productUseCase.adjustStock(ProductId.of(id), delta);
        return mapper.toResponse(updated);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete product", description = "Soft-deletes a product by deactivating it")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Product deactivated successfully"),
        @APIResponse(responseCode = "404", description = "Product not found")
    })
    public Response deactivateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathParam("id") Long id,
            @Parameter(description = "Permanently delete instead of soft-delete")
            @QueryParam("permanent") @DefaultValue("false") boolean permanent) {
        
        ProductId productId = ProductId.of(id);
        
        if (permanent) {
            productUseCase.deleteProduct(productId);
        } else {
            productUseCase.deactivateProduct(productId);
        }
        
        return Response.noContent().build();
    }
}
