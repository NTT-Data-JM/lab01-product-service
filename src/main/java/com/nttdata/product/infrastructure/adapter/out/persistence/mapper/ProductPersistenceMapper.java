package com.nttdata.product.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.nttdata.product.domain.model.Product;
import com.nttdata.product.infrastructure.adapter.out.persistence.entity.ProductEntity;

@Component
public class ProductPersistenceMapper {

	public ProductEntity toEntity(Product product) {
		return ProductEntity.builder()
				.id(product.getId())
				.nombre(product.getNombre())
				.descripcion(product.getDescripcion())
				.precio(product.getPrecio())
				.categoria(product.getCategoria())
				.stock(product.getStock())
				.fechaCreacion(product.getFechaCreacion())
				.build();
	}

	public Product toDomain(ProductEntity entity) {
		return Product.builder()
				.id(entity.getId())
				.nombre(entity.getNombre())
				.descripcion(entity.getDescripcion())
				.precio(entity.getPrecio())
				.categoria(entity.getCategoria())
				.stock(entity.getStock())
				.fechaCreacion(entity.getFechaCreacion())
				.build();
	}

}
