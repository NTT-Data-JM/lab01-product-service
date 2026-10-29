package com.nttdata.product.domain.port.in;

/**
 * Datos de entrada para registrar un producto, independientes del contrato REST.
 */
public record RegisterProductCommand(
		String nombre,
		String descripcion,
		Double precio,
		String categoria,
		Integer stock) {
}
