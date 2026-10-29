package com.nttdata.product.infrastructure.adapter.in.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.nttdata.product.domain.port.in.FindProductUseCase;
import com.nttdata.product.domain.port.in.ListProductsUseCase;
import com.nttdata.product.domain.port.in.RegisterProductUseCase;
import com.nttdata.product.infrastructure.adapter.in.rest.api.ApiApiDelegate;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ProductoRequest;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ProductoResponse;
import com.nttdata.product.infrastructure.adapter.in.rest.mapper.ProductRestMapper;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador de entrada REST: implementa el delegate generado desde {@code api.yml} y traduce
 * entre el contrato HTTP y los casos de uso del dominio.
 */
@Component
@RequiredArgsConstructor
public class ProductRestAdapter implements ApiApiDelegate {

	private final RegisterProductUseCase registerProductUseCase;
	private final ListProductsUseCase listProductsUseCase;
	private final FindProductUseCase findProductUseCase;
	private final ProductRestMapper productRestMapper;

	@Override
	public ResponseEntity<ProductoResponse> registrarProducto(ProductoRequest productoRequest) {
		return ResponseEntity.ok(productRestMapper.toResponse(
				registerProductUseCase.register(productRestMapper.toCommand(productoRequest))));
	}

	@Override
	public ResponseEntity<List<ProductoResponse>> listarProductos() {
		List<ProductoResponse> products = listProductsUseCase.list().stream().map(productRestMapper::toResponse).toList();
		if (products.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(products);
	}

	@Override
	public ResponseEntity<ProductoResponse> obtenerProductoPorId(String id) {
		return findProductUseCase.findBySku(id)
				.map(productRestMapper::toResponse)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.noContent().build());
	}

}
