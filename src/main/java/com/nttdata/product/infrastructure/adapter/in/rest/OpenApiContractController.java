package com.nttdata.product.infrastructure.adapter.in.rest;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenApiContractController {

	@GetMapping("/api.yml")
	public ResponseEntity<Resource> contract() {
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/yaml")).body(new ClassPathResource("api.yml"));
	}

}
