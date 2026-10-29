package com.nttdata.product.domain.port.in;

import lombok.Builder;
import lombok.Value;

/**
 * Datos de entrada para registrar un producto, independientes del contrato REST.
 */
@Value
@Builder
public class RegisterProductCommand {

	String nombre;
	String descripcion;
	Double precio;
	String categoria;
	Integer stock;

}
