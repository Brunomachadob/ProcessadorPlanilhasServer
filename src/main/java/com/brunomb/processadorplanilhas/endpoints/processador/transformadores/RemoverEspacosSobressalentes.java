package com.brunomb.processadorplanilhas.endpoints.processador.transformadores;

import com.brunomb.processadorplanilhas.endpoints.processador.TransformadorRegexReplace;

public class RemoverEspacosSobressalentes extends TransformadorRegexReplace {

	public RemoverEspacosSobressalentes() {
		super("\\s+", " ");
	}
	
	@Override
	public String transformar(Object valor) {
		return super.transformar(valor).trim();
	}
}

