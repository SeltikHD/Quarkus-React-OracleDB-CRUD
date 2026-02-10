package com.autoflex.infrastructure.rest.exception;

import com.autoflex.domain.port.in.ProductUseCase;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * GlobalExceptionHandler - Centralized exception handling for REST API.
 *
 * <p>Maps domain exceptions to appropriate HTTP responses with consistent error format.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        // Domain-specific exceptions
        if (exception instanceof ProductUseCase.ProductNotFoundException) {
            return buildErrorResponse(Response.Status.NOT_FOUND, exception.getMessage());
        }
        
        if (exception instanceof ProductUseCase.ProductSkuAlreadyExistsException) {
            return buildErrorResponse(Response.Status.CONFLICT, exception.getMessage());
        }
        
        if (exception instanceof ProductUseCase.InsufficientStockException) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }
        
        if (exception instanceof IllegalArgumentException) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }
        
        // Log unexpected exceptions
        exception.printStackTrace();
        
        // Generic error for unhandled exceptions
        return buildErrorResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        Map<String, Object> error = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.getStatusCode(),
                "error", status.getReasonPhrase(),
                "message", message
        );
        
        return Response.status(status)
                .entity(error)
                .build();
    }
}
