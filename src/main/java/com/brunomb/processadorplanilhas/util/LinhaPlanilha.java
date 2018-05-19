package com.brunomb.processadorplanilhas.util;

import java.util.Arrays;
import java.util.List;

public class LinhaPlanilha {
	protected int hash = -1;
	protected int indice;
	
	protected List<Object> valores;

	public LinhaPlanilha(int indice, List<Object> valores) {
		this.indice = indice;
		this.valores = valores;
	}
	
	public int getIndice() {
		return indice;
	}
	
	public List<Object> getValores() {
		return valores;
	}

	@Override
	public int hashCode() {
		if (hash == -1) {
			hash = valores.hashCode();
		}

		return hash;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(valores.toArray());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;

		return this.hashCode() == ((LinhaPlanilha) obj).hashCode();
	}
}
