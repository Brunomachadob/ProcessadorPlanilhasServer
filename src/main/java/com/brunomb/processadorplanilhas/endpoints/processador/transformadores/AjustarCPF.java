package com.brunomb.processadorplanilhas.endpoints.processador.transformadores;

import com.brunomb.processadorplanilhas.endpoints.processador.ProcessadorPlanilha;
import com.brunomb.processadorplanilhas.endpoints.processador.Transformador;

public class AjustarCPF implements Transformador {

	private static Transformador converterTexto = ProcessadorPlanilha.getTransformador("ConverterTexto");
	private static Transformador removerPontuacoes = ProcessadorPlanilha.getTransformador("RemoverPontuacoes");
	private static Transformador removerEspacos = ProcessadorPlanilha.getTransformador("RemoverEspacos");

	@Override
	public String transformar(Object valor) {
		if (valor == null) {
			return null;
		}

		String cpf = (String) converterTexto.transformar(valor);

		cpf = (String) removerPontuacoes.transformar(cpf);
		cpf = (String) removerEspacos.transformar(cpf);
		
		
		if (cpf.length() < 11) {
			StringBuilder sb = new StringBuilder(cpf);
			
			while (sb.length() < 11) {
				sb.insert(0, "0");
			}
			
			cpf = sb.toString();
		}

		return cpf;
	}
}
