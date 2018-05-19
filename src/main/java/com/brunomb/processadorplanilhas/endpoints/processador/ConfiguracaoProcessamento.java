package com.brunomb.processadorplanilhas.endpoints.processador;

import java.util.List;

public class ConfiguracaoProcessamento {
	public boolean temCabecalho;
	public List<ConfiguracaoColuna> colunas;
	
	public static class ConfiguracaoColuna {
		public String nome;
		public String descricao;
		public List<String> processadores;
		
	}
}
