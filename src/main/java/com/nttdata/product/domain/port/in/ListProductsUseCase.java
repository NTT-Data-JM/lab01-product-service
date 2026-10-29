package com.nttdata.product.domain.port.in;

import java.util.List;

import com.nttdata.product.domain.model.Product;

public interface ListProductsUseCase {

	List<Product> list();

}
