package com.brunomb.processadorplanilhas.endpoints.processador.transformadores;

import com.brunomb.processadorplanilhas.endpoints.processador.Transformador;

public class CaixaAlta implements Transformador {

	@Override
	public String transformar(Object valor) {
		if (valor == null) {
			return null;
		}

		return ((String) valor).toUpperCase();
	}

}
