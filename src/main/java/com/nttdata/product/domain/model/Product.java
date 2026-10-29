package com.nttdata.product.domain.model;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Entidad de dominio inmutable. Dos productos son iguales si comparten el mismo SKU ({@code id}).
 */
@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

	@EqualsAndHashCode.Include
	String id;
	String nombre;
	String descripcion;
	Double precio;
	String categoria;
	Integer stock;
	OffsetDateTime fechaCreacion;

}
