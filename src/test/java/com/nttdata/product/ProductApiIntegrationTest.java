package com.nttdata.product;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Prueba end-to-end del contrato HTTP (api.yml) recorriendo todas las capas con el repositorio en memoria.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private static String createdSku;

	@Test
	@Order(1)
	void swaggerUiEstaDisponible() throws Exception {
		mockMvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
	}

	@Test
	@Order(2)
	void contratoOpenApiEstaPublicado() throws Exception {
		mockMvc.perform(get("/api.yml")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/yaml"))
				.andExpect(content().string(containsString("API Productos")));
	}

	@Test
	@Order(3)
	void listarProductosDevuelve204CuandoElCatalogoEstaVacio() throws Exception {
		mockMvc.perform(get("/api/product")).andExpect(status().isNoContent());
	}

	@Test
	@Order(4)
	void obtenerProductoPorSkuDevuelve204CuandoNoExiste() throws Exception {
		mockMvc.perform(get("/api/product/{id}", "sku-inexistente")).andExpect(status().isNoContent());
	}

	@Test
	@Order(5)
	void registrarProductoDevuelve400CuandoLosDatosSonInvalidos() throws Exception {
		mockMvc.perform(post("/api/product").contentType(MediaType.APPLICATION_JSON).content("""
				{"nombre":" ","precio":-1}
				""")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
				.andExpect(jsonPath("$.detalle").value("El nombre es obligatorio"));
	}

	@Test
	@Order(6)
	void registrarProductoDevuelve400CuandoElJsonEsInvalido() throws Exception {
		mockMvc.perform(post("/api/product").contentType(MediaType.APPLICATION_JSON).content("{nombre:"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
				.andExpect(jsonPath("$.detalle").value("El cuerpo de la petición no es un JSON válido"));
	}

	@Test
	@Order(7)
	void rutaInexistenteDevuelve404() throws Exception {
		mockMvc.perform(get("/ruta/inexistente")).andExpect(status().isNotFound());
	}

	@Test
	@Order(8)
	void registrarProductoDevuelve200ConSkuGenerado() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/product").contentType(MediaType.APPLICATION_JSON).content("""
				{
				  "nombre": "Laptop Lenovo ThinkPad",
				  "descripcion": "Laptop empresarial con 16GB RAM",
				  "precio": 1200.50,
				  "categoria": "Tecnología",
				  "stock": 10
				}
				""")).andExpect(status().isOk()).andExpect(jsonPath("$._id").value("SKU-000001"))
				.andExpect(jsonPath("$.id").doesNotExist())
				.andExpect(jsonPath("$.nombre").value("Laptop Lenovo ThinkPad"))
				.andExpect(jsonPath("$.precio").value(1200.50)).andExpect(jsonPath("$.categoria").value("Tecnología"))
				.andExpect(jsonPath("$.stock").value(10)).andExpect(jsonPath("$.fechaCreacion").isNotEmpty())
				.andExpect(content().string(startsWith("{\"_id\":"))).andReturn();

		String body = result.getResponse().getContentAsString();
		createdSku = body.replaceAll("(?s).*\"_id\"\\s*:\\s*\"([^\"]+)\".*", "$1");
	}

	@Test
	@Order(9)
	void listarProductosDevuelve200ConElCatalogo() throws Exception {
		mockMvc.perform(get("/api/product")).andExpect(status().isOk()).andExpect(jsonPath("$[0]._id").value(createdSku))
				.andExpect(jsonPath("$[0].nombre").value("Laptop Lenovo ThinkPad"));
	}

	@Test
	@Order(10)
	void obtenerProductoPorSkuDevuelve200CuandoExiste() throws Exception {
		mockMvc.perform(get("/api/product/{id}", createdSku)).andExpect(status().isOk())
				.andExpect(jsonPath("$._id").value(createdSku)).andExpect(jsonPath("$.nombre").value("Laptop Lenovo ThinkPad"));
	}

}
