/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.0.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.sciencesakura.sample.api;

import com.sciencesakura.sample.api.model.ErrorResponse;
import com.sciencesakura.sample.api.model.ItemObject;
import com.sciencesakura.sample.api.model.UpdateItemStockRequest;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "item", description = "Item API")
public interface ItemsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /items : Create a new item
     *
     * @param itemObject New item to create (required)
     * @return Created (status code 201)
     */
    @Operation(
        operationId = "createItem",
        summary = "Create a new item",
        tags = { "item" },
        responses = {
            @ApiResponse(responseCode = "201", description = "Created")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/items",
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> createItem(
        @Parameter(name = "ItemObject", description = "New item to create", required = true) @Valid @RequestBody ItemObject itemObject
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /items/{code} : Delete an item
     *
     * @param code Item code (required)
     * @return No Content (status code 204)
     */
    @Operation(
        operationId = "deleteItem",
        summary = "Delete an item",
        tags = { "item" },
        responses = {
            @ApiResponse(responseCode = "204", description = "No Content")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/items/{code}"
    )
    default ResponseEntity<Void> deleteItem(
        @Pattern(regexp = "[0-9A-Z]{8}") @Parameter(name = "code", description = "Item code", required = true, in = ParameterIn.PATH) @PathVariable("code") String code
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /items/{code} : Get an item
     *
     * @param code Item code (required)
     * @return OK (status code 200)
     *         or Not Found (status code 404)
     */
    @Operation(
        operationId = "getItem",
        summary = "Get an item",
        tags = { "item" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ItemObject.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/items/{code}",
        produces = { "application/json" }
    )
    default ResponseEntity<ItemObject> getItem(
        @Pattern(regexp = "[0-9A-Z]{8}") @Parameter(name = "code", description = "Item code", required = true, in = ParameterIn.PATH) @PathVariable("code") String code
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"code\" : \"code\", \"price\" : 0.08008281904610115, \"name\" : \"name\", \"description\" : \"description\", \"stock\" : 0.6027456183070403 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /items : Get item list
     *
     * @param q Text to search (optional)
     * @param p 0-based page number (optional, default to 0)
     * @param s Size of a page (optional, default to 20)
     * @return OK (status code 200)
     */
    @Operation(
        operationId = "getItemList",
        summary = "Get item list",
        tags = { "item" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ItemObject.class)))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/items",
        produces = { "application/json" }
    )
    default ResponseEntity<List<ItemObject>> getItemList(
        @Parameter(name = "q", description = "Text to search", in = ParameterIn.QUERY) @Valid @RequestParam(value = "q", required = false) String q,
        @Min(0) @Parameter(name = "p", description = "0-based page number", in = ParameterIn.QUERY) @Valid @RequestParam(value = "p", required = false, defaultValue = "0") Integer p,
        @Min(1) @Parameter(name = "s", description = "Size of a page", in = ParameterIn.QUERY) @Valid @RequestParam(value = "s", required = false, defaultValue = "20") Integer s
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"code\" : \"code\", \"price\" : 0.08008281904610115, \"name\" : \"name\", \"description\" : \"description\", \"stock\" : 0.6027456183070403 }, { \"code\" : \"code\", \"price\" : 0.08008281904610115, \"name\" : \"name\", \"description\" : \"description\", \"stock\" : 0.6027456183070403 } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /items/{code} : Update an item
     *
     * @param code Item code (required)
     * @param itemObject Item to update (required)
     * @return No Content (status code 204)
     */
    @Operation(
        operationId = "updateItem",
        summary = "Update an item",
        tags = { "item" },
        responses = {
            @ApiResponse(responseCode = "204", description = "No Content")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/items/{code}",
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> updateItem(
        @Pattern(regexp = "[0-9A-Z]{8}") @Parameter(name = "code", description = "Item code", required = true, in = ParameterIn.PATH) @PathVariable("code") String code,
        @Parameter(name = "ItemObject", description = "Item to update", required = true) @Valid @RequestBody ItemObject itemObject
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /items/{code}/stock : Update the quantity of an item
     *
     * @param code Item code (required)
     * @param updateItemStockRequest Quantity of stock to update (required)
     * @return No Content (status code 204)
     *         or Not Found (status code 404)
     */
    @Operation(
        operationId = "updateItemStockQuantity",
        summary = "Update the quantity of an item",
        tags = { "item" },
        responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/items/{code}/stock",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> updateItemStockQuantity(
        @Pattern(regexp = "[0-9A-Z]{8}") @Parameter(name = "code", description = "Item code", required = true, in = ParameterIn.PATH) @PathVariable("code") String code,
        @Parameter(name = "UpdateItemStockRequest", description = "Quantity of stock to update", required = true) @Valid @RequestBody UpdateItemStockRequest updateItemStockRequest
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
