package com.brunomb.processadorplanilhas.endpoints.processador.transformadores;

import com.brunomb.processadorplanilhas.endpoints.processador.Transformador;

public class ConverterNumero implements Transformador {

	@Override
	public Double transformar(Object valor) {
		if (valor == null) {
			return 0.0;
		} else if (valor instanceof String) {
			return Double.valueOf((String) valor);
		} else if (valor instanceof Number) {
			return ((Number) valor).doubleValue();
		} else {
			return 0.0;
		}
	}
}
