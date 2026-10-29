package com.nttdata.product.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class InMemorySkuGeneratorAdapterTest {

	private final InMemorySkuGeneratorAdapter generator = new InMemorySkuGeneratorAdapter();

	@Test
	void generaSkuCorrelativosConSeisDigitos() {
		assertThat(generator.nextSku()).isEqualTo("SKU-000001");
		assertThat(generator.nextSku()).isEqualTo("SKU-000002");
		assertThat(generator.nextSku()).isEqualTo("SKU-000003");
	}

	@Test
	void noRepiteSkuConPeticionesConcurrentes() {
		Set<String> skus = ConcurrentHashMap.newKeySet();

		IntStream.range(0, 1_000).parallel().forEach(i -> skus.add(generator.nextSku()));

		assertThat(skus).hasSize(1_000).allMatch(sku -> sku.matches("SKU-\\d{6}"));
	}

}
