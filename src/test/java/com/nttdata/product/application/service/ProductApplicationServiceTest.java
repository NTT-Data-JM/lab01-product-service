package com.nttdata.product.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nttdata.product.domain.exception.InvalidProductException;
import com.nttdata.product.domain.model.Product;
import com.nttdata.product.domain.port.in.RegisterProductCommand;
import com.nttdata.product.domain.port.out.ProductRepositoryPort;
import com.nttdata.product.domain.port.out.SkuGeneratorPort;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

	private static final Instant NOW = Instant.parse("2026-02-18T10:30:00.123456789Z");

	@Mock
	private ProductRepositoryPort productRepositoryPort;

	@Mock
	private SkuGeneratorPort skuGeneratorPort;

	private ProductApplicationService service;

	@BeforeEach
	void setUp() {
		service = new ProductApplicationService(productRepositoryPort, skuGeneratorPort, Clock.fixed(NOW, ZoneOffset.UTC));
	}

	private static RegisterProductCommand.RegisterProductCommandBuilder validCommand() {
		return RegisterProductCommand.builder().nombre("Laptop Lenovo ThinkPad").descripcion("Laptop empresarial")
				.precio(1200.50).categoria("Tecnología").stock(10);
	}

	@Test
	void registraProductoNormalizandoLosDatos() {
		when(skuGeneratorPort.nextSku()).thenReturn("SKU-000001");
		when(productRepositoryPort.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Product result = service.register(validCommand().nombre("  Laptop  ").descripcion("  16GB  ")
				.categoria("  Tecnología ").build());

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
		verify(productRepositoryPort).save(captor.capture());
		Product saved = captor.getValue();
		assertThat(saved.getId()).isEqualTo("SKU-000001");
		assertThat(saved.getNombre()).isEqualTo("Laptop");
		assertThat(saved.getDescripcion()).isEqualTo("16GB");
		assertThat(saved.getCategoria()).isEqualTo("Tecnología");
		assertThat(saved.getPrecio()).isEqualTo(1200.50);
		assertThat(saved.getStock()).isEqualTo(10);
		assertThat(saved.getFechaCreacion()).isEqualTo(OffsetDateTime.parse("2026-02-18T10:30:00.123Z"));
		assertThat(result).isSameAs(saved);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "   ")
	void registraProductoConDescripcionVaciaComoNula(String descripcion) {
		when(skuGeneratorPort.nextSku()).thenReturn("SKU-000001");
		when(productRepositoryPort.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Product result = service.register(validCommand().descripcion(descripcion).build());

		assertThat(result.getDescripcion()).isNull();
	}

	@Test
	void registraProductoSinStockYConPrecioCero() {
		when(skuGeneratorPort.nextSku()).thenReturn("SKU-000001");
		when(productRepositoryPort.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Product result = service.register(validCommand().stock(null).precio(0.0).build());

		assertThat(result.getStock()).isNull();
		assertThat(result.getPrecio()).isZero();
	}

	@Test
	void rechazaComandoNulo() {
		assertThatThrownBy(() -> service.register(null)).isInstanceOf(InvalidProductException.class)
				.hasMessage("El producto es obligatorio");
		verifyNoInteractions(productRepositoryPort);
	}

	static Stream<Arguments> comandosInvalidos() {
		return Stream.of(
				Arguments.of(validCommand().nombre(null).build(), "El nombre es obligatorio"),
				Arguments.of(validCommand().nombre("  ").build(), "El nombre es obligatorio"),
				Arguments.of(validCommand().precio(null).build(), "El precio debe ser mayor o igual a cero"),
				Arguments.of(validCommand().precio(-0.01).build(), "El precio debe ser mayor o igual a cero"),
				Arguments.of(validCommand().categoria(null).build(), "La categoría es obligatoria"),
				Arguments.of(validCommand().categoria("").build(), "La categoría es obligatoria"),
				Arguments.of(validCommand().stock(-1).build(), "El stock no puede ser negativo"));
	}

	@ParameterizedTest
	@MethodSource("comandosInvalidos")
	void rechazaDatosInvalidos(RegisterProductCommand command, String expectedMessage) {
		assertThatThrownBy(() -> service.register(command)).isInstanceOf(InvalidProductException.class)
				.hasMessage(expectedMessage);
		verify(productRepositoryPort, never()).save(any());
		verifyNoInteractions(skuGeneratorPort);
	}

	@Test
	void listaLosProductosDelRepositorio() {
		List<Product> products = List.of(Product.builder().id("sku-1").build(), Product.builder().id("sku-2").build());
		when(productRepositoryPort.findAll()).thenReturn(products);

		assertThat(service.list()).containsExactlyElementsOf(products);
	}

	@Test
	void buscaProductoPorSku() {
		Product product = Product.builder().id("sku-1").build();
		when(productRepositoryPort.findById("sku-1")).thenReturn(Optional.of(product));

		assertThat(service.findBySku("sku-1")).contains(product);
	}

	@Test
	void devuelveVacioCuandoElSkuNoExiste() {
		when(productRepositoryPort.findById("sku-x")).thenReturn(Optional.empty());

		assertThat(service.findBySku("sku-x")).isEmpty();
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "  ")
	void devuelveVacioSinConsultarCuandoElSkuEsBlanco(String sku) {
		assertThat(service.findBySku(sku)).isEmpty();
		verifyNoInteractions(productRepositoryPort);
	}

}
