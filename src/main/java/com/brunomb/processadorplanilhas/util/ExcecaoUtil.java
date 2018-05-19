package com.brunomb.processadorplanilhas.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcecaoUtil {

	private static Pattern pClassCastException = Pattern.compile("\\.(\\w+) cannot be cast to\\s+[\\w+\\.]+\\.(\\w+)",
			Pattern.DOTALL);

	public static String tratarMensagemErro(Throwable erro) {
		if (NullPointerException.class.isInstance(erro)) {
			return "Erro interno (NPE). " + erro.getMessage();
		} else if (ClassCastException.class.isInstance(erro)) {
			Matcher m = pClassCastException.matcher(erro.getMessage());

			if (m != null && m.find()) {
				String tipoRecebido = getDescricaoClasse(m.group(1));
				String tipoEsperado = getDescricaoClasse(m.group(2));

				return "Esperava-se um valor do tipo '" + tipoEsperado + "' mas recebeu '" + tipoRecebido + "'";
			}

		}

		return erro.getMessage();
	}

	private static String getDescricaoClasse(String classe) {
		switch (classe) {
		case "String":
			return "Texto";
		
		case "Double":
		case "Integer":
		case "Float":
			return "Número";
			
		case "Date":
			return "Data";

		default:
			return classe;
		}
	}

}
