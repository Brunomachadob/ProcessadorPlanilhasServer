package com.brunomb.processadorplanilhas.endpoints.processador.transformadores;

import java.text.Normalizer;
import java.util.regex.Pattern;

import com.brunomb.processadorplanilhas.endpoints.processador.Transformador;

public class RemoverAcentos implements Transformador {
	Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

	@Override
	public String transformar(Object valor) {
		if (valor == null) {
			return null;
		}

		String nfdNormalizedString = Normalizer.normalize((String) valor, Normalizer.Form.NFD);
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

}
