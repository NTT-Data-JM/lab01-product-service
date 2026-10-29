package com.nttdata.product.application.service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.nttdata.product.domain.exception.InvalidProductException;
import com.nttdata.product.domain.model.Product;
import com.nttdata.product.domain.port.in.FindProductUseCase;
import com.nttdata.product.domain.port.in.ListProductsUseCase;
import com.nttdata.product.domain.port.in.RegisterProductCommand;
import com.nttdata.product.domain.port.in.RegisterProductUseCase;
import com.nttdata.product.domain.port.out.ProductRepositoryPort;
import com.nttdata.product.domain.port.out.SkuGeneratorPort;

import lombok.RequiredArgsConstructor;

/**
 * Implementa los casos de uso de productos. No depende de Spring: se registra como bean en
 * {@code infrastructure.config.BeanConfiguration}.
 */
@RequiredArgsConstructor
public class ProductApplicationService implements RegisterProductUseCase, ListProductsUseCase, FindProductUseCase {

	private final ProductRepositoryPort productRepositoryPort;
	private final SkuGeneratorPort skuGeneratorPort;
	private final Clock clock;

	@Override
	public Product register(RegisterProductCommand command) {
		validate(command);
		Product product = Product.builder()
				.id(skuGeneratorPort.nextSku())
				.nombre(command.getNombre().trim())
				.descripcion(trimToNull(command.getDescripcion()))
				.precio(command.getPrecio())
				.categoria(command.getCategoria().trim())
				.stock(command.getStock())
				.fechaCreacion(OffsetDateTime.now(clock).truncatedTo(ChronoUnit.MILLIS))
				.build();
		return productRepositoryPort.save(product);
	}

	@Override
	public List<Product> list() {
		return productRepositoryPort.findAll();
	}

	@Override
	public Optional<Product> findBySku(String sku) {
		if (sku == null || sku.isBlank()) {
			return Optional.empty();
		}
		return productRepositoryPort.findById(sku);
	}

	private void validate(RegisterProductCommand command) {
		if (command == null) {
			throw new InvalidProductException("El producto es obligatorio");
		}
		if (command.getNombre() == null || command.getNombre().isBlank()) {
			throw new InvalidProductException("El nombre es obligatorio");
		}
		if (command.getPrecio() == null || command.getPrecio() < 0) {
			throw new InvalidProductException("El precio debe ser mayor o igual a cero");
		}
		if (command.getCategoria() == null || command.getCategoria().isBlank()) {
			throw new InvalidProductException("La categoría es obligatoria");
		}
		if (command.getStock() != null && command.getStock() < 0) {
			throw new InvalidProductException("El stock no puede ser negativo");
		}
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

}
