package com.nttdata.product.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.nttdata.product.domain.model.Product;

public interface ProductRepositoryPort {

	Product save(Product product);

	List<Product> findAll();

	Optional<Product> findById(String id);

}
