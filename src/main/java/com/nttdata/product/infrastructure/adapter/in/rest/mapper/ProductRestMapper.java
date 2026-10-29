package com.nttdata.product.infrastructure.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import com.nttdata.product.domain.model.Product;
import com.nttdata.product.domain.port.in.RegisterProductCommand;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ProductoRequest;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ProductoResponse;

@Component
public class ProductRestMapper {

	public RegisterProductCommand toCommand(ProductoRequest request) {
		if (request == null) {
			return null;
		}
		return RegisterProductCommand.builder()
				.nombre(request.getNombre())
				.descripcion(request.getDescripcion())
				.precio(request.getPrecio())
				.categoria(request.getCategoria())
				.stock(request.getStock())
				.build();
	}

	public ProductoResponse toResponse(Product product) {
		return new ProductoResponse()
				.id(product.getId())
				.nombre(product.getNombre())
				.descripcion(product.getDescripcion())
				.precio(product.getPrecio())
				.categoria(product.getCategoria())
				.stock(product.getStock())
				.fechaCreacion(product.getFechaCreacion());
	}

}
