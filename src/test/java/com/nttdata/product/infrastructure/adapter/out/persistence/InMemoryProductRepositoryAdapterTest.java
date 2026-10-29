package com.nttdata.product.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.nttdata.product.domain.model.Product;
import com.nttdata.product.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;

class InMemoryProductRepositoryAdapterTest {

	private final InMemoryProductRepositoryAdapter repository = new InMemoryProductRepositoryAdapter(
			new ProductPersistenceMapper());

	private static Product product(String id) {
		return Product.builder().id(id).nombre("Producto " + id).descripcion("desc").precio(1.5).categoria("Cat")
				.stock(3).fechaCreacion(OffsetDateTime.parse("2026-02-18T10:30:00Z")).build();
	}

	@Test
	void repositorioNuevoEstaVacio() {
		assertThat(repository.findAll()).isEmpty();
	}

	@Test
	void guardaYRecuperaElProductoConTodosSusDatos() {
		Product product = product("sku-1");

		Product saved = repository.save(product);

		assertThat(saved).usingRecursiveComparison().isEqualTo(product);
		assertThat(repository.findById("sku-1")).get().usingRecursiveComparison().isEqualTo(product);
	}

	@Test
	void listaEnOrdenDeInsercion() {
		repository.save(product("sku-b"));
		repository.save(product("sku-a"));
		repository.save(product("sku-c"));

		assertThat(repository.findAll()).extracting(Product::getId).containsExactly("sku-b", "sku-a", "sku-c");
	}

	@Test
	void guardarElMismoSkuReemplazaElProducto() {
		repository.save(product("sku-1"));
		repository.save(product("sku-1").toBuilder().nombre("Actualizado").build());

		assertThat(repository.findAll()).singleElement().extracting(Product::getNombre).isEqualTo("Actualizado");
	}

	@Test
	void devuelveVacioCuandoElSkuNoExiste() {
		assertThat(repository.findById("no-existe")).isEmpty();
	}

}
