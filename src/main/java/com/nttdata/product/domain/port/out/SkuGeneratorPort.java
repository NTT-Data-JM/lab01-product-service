package com.nttdata.product.domain.port.out;

/**
 * Genera el SKU (campo {@code _id}) que identifica a cada producto nuevo.
 */
public interface SkuGeneratorPort {

	String nextSku();

}
