package com.nttdata.product.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

class ProductTest {

	@Test
	void dosProductosConElMismoSkuSonIguales() {
		Product original = Product.builder().id("sku-1").nombre("Laptop").precio(10.0).build();
		Product modificado = original.toBuilder().nombre("Otro nombre").precio(99.0).build();

		assertThat(modificado).isEqualTo(original).hasSameHashCodeAs(original);
	}

	@Test
	void productosConDistintoSkuNoSonIguales() {
		Product uno = Product.builder().id("sku-1").nombre("Laptop").build();
		Product otro = uno.toBuilder().id("sku-2").build();

		assertThat(uno).isNotEqualTo(otro);
	}

	@Test
	void elBuilderAsignaTodosLosAtributos() {
		OffsetDateTime fecha = OffsetDateTime.parse("2026-02-18T10:30:00Z");

		Product product = Product.builder().id("sku-1").nombre("Laptop").descripcion("16GB").precio(1200.5)
				.categoria("Tecnología").stock(10).fechaCreacion(fecha).build();

		assertThat(product.getId()).isEqualTo("sku-1");
		assertThat(product.getNombre()).isEqualTo("Laptop");
		assertThat(product.getDescripcion()).isEqualTo("16GB");
		assertThat(product.getPrecio()).isEqualTo(1200.5);
		assertThat(product.getCategoria()).isEqualTo("Tecnología");
		assertThat(product.getStock()).isEqualTo(10);
		assertThat(product.getFechaCreacion()).isEqualTo(fecha);
	}

}
