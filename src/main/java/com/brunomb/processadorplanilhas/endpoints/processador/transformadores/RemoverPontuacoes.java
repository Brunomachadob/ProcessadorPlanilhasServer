package com.brunomb.processadorplanilhas.endpoints.processador.transformadores;

import com.brunomb.processadorplanilhas.endpoints.processador.TransformadorRegexReplace;

public class RemoverPontuacoes extends TransformadorRegexReplace {

	public RemoverPontuacoes() {
		super("([^a-zA-Z0-9])", "");
	}
}
