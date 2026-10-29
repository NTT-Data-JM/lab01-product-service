package com.nttdata.product.infrastructure.adapter.out.persistence;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nttdata.product.domain.model.Product;
import com.nttdata.product.domain.port.out.ProductRepositoryPort;
import com.nttdata.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.nttdata.product.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador de salida en memoria. Conserva el orden de inserción y es seguro para hilos.
 */
@Repository
@RequiredArgsConstructor
public class InMemoryProductRepositoryAdapter implements ProductRepositoryPort {

	private final Map<String, ProductEntity> products = Collections.synchronizedMap(new LinkedHashMap<>());
	private final ProductPersistenceMapper productPersistenceMapper;

	@Override
	public Product save(Product product) {
		ProductEntity entity = productPersistenceMapper.toEntity(product);
		products.put(entity.getId(), entity);
		return productPersistenceMapper.toDomain(entity);
	}

	@Override
	public List<Product> findAll() {
		synchronized (products) {
			return products.values().stream().map(productPersistenceMapper::toDomain).toList();
		}
	}

	@Override
	public Optional<Product> findById(String id) {
		return Optional.ofNullable(products.get(id)).map(productPersistenceMapper::toDomain);
	}

}
