package com.nttdata.product.infrastructure.adapter.in.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.nttdata.product.domain.model.Product;
import com.nttdata.product.domain.port.in.RegisterProductCommand;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ProductoRequest;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ProductoResponse;

class ProductRestMapperTest {

	private final ProductRestMapper mapper = new ProductRestMapper();

	@Test
	void convierteRequestEnComando() {
		ProductoRequest request = new ProductoRequest().nombre("Laptop").descripcion("16GB").precio(1200.5)
				.categoria("Tecnología").stock(10);

		RegisterProductCommand command = mapper.toCommand(request);

		assertThat(command).isEqualTo(new RegisterProductCommand("Laptop", "16GB", 1200.5, "Tecnología", 10));
	}

	@Test
	void requestNuloSeConvierteEnComandoNulo() {
		assertThat(mapper.toCommand(null)).isNull();
	}

	@Test
	void convierteProductoEnResponse() {
		OffsetDateTime fecha = OffsetDateTime.parse("2026-02-18T10:30:00Z");
		Product product = Product.builder().id("sku-1").nombre("Laptop").descripcion("16GB").precio(1200.5)
				.categoria("Tecnología").stock(10).fechaCreacion(fecha).build();

		ProductoResponse response = mapper.toResponse(product);

		assertThat(response).isEqualTo(new ProductoResponse().id("sku-1").nombre("Laptop").descripcion("16GB")
				.precio(1200.5).categoria("Tecnología").stock(10).fechaCreacion(fecha));
	}

}
