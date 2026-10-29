package com.nttdata.product.domain.port.in;

import com.nttdata.product.domain.model.Product;

public interface RegisterProductUseCase {

	Product register(RegisterProductCommand command);

}
