package com.brunomb.processadorplanilhas.endpoints.processador.transformadores;

import com.brunomb.processadorplanilhas.endpoints.processador.TransformadorRegexReplace;

public class RemoverEspacos extends TransformadorRegexReplace {

	public RemoverEspacos() {
		super("\\s", "");
	}
}

