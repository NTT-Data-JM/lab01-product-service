package com.nttdata.product.infrastructure.adapter.out.persistence;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.nttdata.product.domain.port.out.SkuGeneratorPort;

/**
 * Secuencia en memoria que produce SKU correlativos: SKU-000001, SKU-000002, ...
 */
@Component
public class InMemorySkuGeneratorAdapter implements SkuGeneratorPort {

	private static final String SKU_FORMAT = "SKU-%06d";

	private final AtomicLong sequence = new AtomicLong();

	@Override
	public String nextSku() {
		return String.format(SKU_FORMAT, sequence.incrementAndGet());
	}

}
