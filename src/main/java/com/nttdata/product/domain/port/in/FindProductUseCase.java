package com.nttdata.product.domain.port.in;

import java.util.Optional;

import com.nttdata.product.domain.model.Product;

public interface FindProductUseCase {

	Optional<Product> findBySku(String sku);

}
