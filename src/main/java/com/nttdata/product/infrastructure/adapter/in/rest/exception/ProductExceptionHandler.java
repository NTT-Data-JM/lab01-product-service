package com.nttdata.product.infrastructure.adapter.in.rest.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.nttdata.product.domain.exception.InvalidProductException;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ProductExceptionHandler {

	@ExceptionHandler(InvalidProductException.class)
	public ResponseEntity<ErrorResponse> handleInvalidProduct(InvalidProductException exception) {
		return ResponseEntity.badRequest().body(error("DATOS_INVALIDOS", "Datos de entrada inválidos", exception.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
		return ResponseEntity.badRequest()
				.body(error("DATOS_INVALIDOS", "Datos de entrada inválidos", "El cuerpo de la petición no es un JSON válido"));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Void> handleMissingResource(NoResourceFoundException exception) {
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
		log.error("Error no controlado", exception);
		return ResponseEntity.internalServerError()
				.body(error("ERROR_INTERNO", "Error interno del servidor", "No fue posible completar la operación"));
	}

	private ErrorResponse error(String codigo, String mensaje, String detalle) {
		return new ErrorResponse().codigo(codigo).mensaje(mensaje).detalle(detalle);
	}

}
