package com.nttdata.product.infrastructure.adapter.in.rest.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.nttdata.product.domain.exception.InvalidProductException;
import com.nttdata.product.infrastructure.adapter.in.rest.dto.ErrorResponse;

class ProductExceptionHandlerTest {

	private final ProductExceptionHandler handler = new ProductExceptionHandler();

	@Test
	void productoInvalidoDevuelve400ConElDetalle() {
		ResponseEntity<ErrorResponse> response = handler
				.handleInvalidProduct(new InvalidProductException("El nombre es obligatorio"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isEqualTo(new ErrorResponse().codigo("DATOS_INVALIDOS")
				.mensaje("Datos de entrada inválidos").detalle("El nombre es obligatorio"));
	}

	@Test
	void cuerpoIlegibleDevuelve400() {
		ResponseEntity<ErrorResponse> response = handler.handleUnreadableBody(mock(HttpMessageNotReadableException.class));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().getDetalle()).isEqualTo("El cuerpo de la petición no es un JSON válido");
	}

	@Test
	void recursoInexistenteDevuelve404() {
		ResponseEntity<Void> response = handler.handleMissingResource(mock(NoResourceFoundException.class));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void errorNoControladoDevuelve500SinExponerDetallesInternos() {
		ResponseEntity<ErrorResponse> response = handler.handleUnexpected(new IllegalStateException("fallo interno"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isEqualTo(new ErrorResponse().codigo("ERROR_INTERNO")
				.mensaje("Error interno del servidor").detalle("No fue posible completar la operación"));
	}

}
