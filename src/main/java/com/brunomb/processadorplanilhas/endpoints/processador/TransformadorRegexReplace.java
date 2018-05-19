package com.brunomb.processadorplanilhas.endpoints.processador;

import java.util.regex.Pattern;

public class TransformadorRegexReplace implements Transformador {

	protected Pattern pattern;
	protected String replace;

	public TransformadorRegexReplace(String regex, String replace) {
		this.pattern = Pattern.compile(regex);
		this.replace = replace;
	}

	@Override
	public String transformar(Object valor) {
		if (valor == null) {
			return null;
		}

		return pattern.matcher((String) valor).replaceAll(this.replace);
	}

}
