package com.brunomb.processadorplanilhas.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ResultadoProcessamento {

	public ByteArrayOutputStream planilhaProcessada;
	public final List<ErroImportacao> erros;
	
	public ResultadoProcessamento() {
		this.erros = new ArrayList<>();
	}
	
	public boolean contemErros() {
		return !this.erros.isEmpty();
	}
	
	public static class ErroImportacao {
		public String identificador;
		public String mensagem;
		
		public ErroImportacao(String identificador, String mensagem) {
			this.identificador = identificador;
			this.mensagem = mensagem;
		}
	}
}
