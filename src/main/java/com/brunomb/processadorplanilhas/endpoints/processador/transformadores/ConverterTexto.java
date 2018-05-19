package com.brunomb.processadorplanilhas.endpoints.processador.transformadores;

import java.math.BigDecimal;

import com.brunomb.processadorplanilhas.endpoints.processador.Transformador;

public class ConverterTexto implements Transformador {

	@Override
	public String transformar(Object valor) {
		if (valor == null) {
			return null;
		} else if (valor instanceof String) {
			return ((String) valor).trim();
		} else if (valor instanceof Number) {
			return new BigDecimal(((Number) valor).toString()).toPlainString();
		} else {
			return null;
		}
	}
}
