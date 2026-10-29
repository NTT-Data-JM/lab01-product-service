package com.nttdata.product.infrastructure.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nttdata.product.application.service.ProductApplicationService;
import com.nttdata.product.domain.port.out.ProductRepositoryPort;
import com.nttdata.product.domain.port.out.SkuGeneratorPort;

@Configuration
public class BeanConfiguration {

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	public ProductApplicationService productApplicationService(ProductRepositoryPort productRepositoryPort,
			SkuGeneratorPort skuGeneratorPort, Clock clock) {
		return new ProductApplicationService(productRepositoryPort, skuGeneratorPort, clock);
	}

}
