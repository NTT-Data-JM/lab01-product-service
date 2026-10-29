package com.nttdata.product.infrastructure.adapter.out.persistence.entity;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representación de persistencia del producto. Se mantiene separada del modelo de dominio para
 * poder cambiar el almacenamiento (p. ej. JPA/MongoDB) sin tocar el dominio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

	private String id;
	private String nombre;
	private String descripcion;
	private Double precio;
	private String categoria;
	private Integer stock;
	private OffsetDateTime fechaCreacion;

}
