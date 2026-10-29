package com.nttdata.product.infrastructure.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nttdata.product.domain.model.Product;
import com.nttdata.product.domain.port.in.FindProductUseCase;
import com.nttdata.product.domain.port.in.ListProductsUseCase;
import com.nttdata.product.domain.port.in.RegisterProductCommand;
import com.nttdata.product.domain.port.in.RegisterProductUseCase;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ProductoRequest;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ProductoResponse;
import com.nttdata.product.infrastructure.adapter.in.rest.mapper.ProductRestMapper;

@ExtendWith(MockitoExtension.class)
class ProductRestAdapterTest {

	@Mock
	private RegisterProductUseCase registerProductUseCase;

	@Mock
	private ListProductsUseCase listProductsUseCase;

	@Mock
	private FindProductUseCase findProductUseCase;

	private ProductRestAdapter adapter;

	private final Product product = Product.builder().id("sku-1").nombre("Laptop").precio(10.0).categoria("Tec").build();

	@BeforeEach
	void setUp() {
		adapter = new ProductRestAdapter(registerProductUseCase, listProductsUseCase, findProductUseCase,
				new ProductRestMapper());
	}

	@Test
	void registrarProductoDevuelve200ConElProductoCreado() {
		when(registerProductUseCase.register(any(RegisterProductCommand.class))).thenReturn(product);

		ResponseEntity<ProductoResponse> response = adapter
				.registrarProducto(new ProductoRequest().nombre("Laptop").precio(10.0).categoria("Tec"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getId()).isEqualTo("sku-1");
	}

	@Test
	void listarProductosDevuelve204CuandoNoHayProductos() {
		when(listProductsUseCase.list()).thenReturn(List.of());

		ResponseEntity<List<ProductoResponse>> response = adapter.listarProductos();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
	}

	@Test
	void listarProductosDevuelve200ConElCatalogo() {
		when(listProductsUseCase.list()).thenReturn(List.of(product));

		ResponseEntity<List<ProductoResponse>> response = adapter.listarProductos();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).extracting(ProductoResponse::getId).containsExactly("sku-1");
	}

	@Test
	void obtenerProductoPorIdDevuelve200CuandoExiste() {
		when(findProductUseCase.findBySku("sku-1")).thenReturn(Optional.of(product));

		ResponseEntity<ProductoResponse> response = adapter.obtenerProductoPorId("sku-1");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getNombre()).isEqualTo("Laptop");
	}

	@Test
	void obtenerProductoPorIdDevuelve204CuandoNoExiste() {
		when(findProductUseCase.findBySku("sku-x")).thenReturn(Optional.empty());

		ResponseEntity<ProductoResponse> response = adapter.obtenerProductoPorId("sku-x");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
	}

}
