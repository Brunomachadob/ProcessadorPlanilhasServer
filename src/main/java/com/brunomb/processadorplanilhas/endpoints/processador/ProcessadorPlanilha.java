package com.brunomb.processadorplanilhas.endpoints.processador;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.brunomb.processadorplanilhas.util.PlanilhaUtil;
import com.brunomb.processadorplanilhas.util.ResultadoProcessamento;
import com.brunomb.processadorplanilhas.util.StringUtil;

public class ProcessadorPlanilha {

	private static final Logger LOGGER = Logger.getLogger(ProcessadorPlanilha.class.getName());

	private String nomePlanilha;
	private InputStream planilha;
	private ConfiguracaoProcessamento cfg;
	private ProcessamentoListener listener;

	private static final Map<String, Transformador> transformadoresCache;

	static {
		transformadoresCache = new HashMap<>();
	}

	public ProcessadorPlanilha(String nomePlanilha, InputStream planilha, ConfiguracaoProcessamento cfg,
			ProcessamentoListener listener) {
		this.nomePlanilha = nomePlanilha;
		this.planilha = planilha;
		this.cfg = cfg;
		this.listener = listener;
	}

	public ResultadoProcessamento processar() throws IOException {
		if (this.planilha == null) {
			throw new IllegalStateException("Arquivo a ser processado não foi informado.");
		}

		if (this.cfg == null) {
			throw new IllegalStateException("Configuração de processamento não foi informado.");
		}

		LOGGER.log(Level.INFO, "Iniciando processamento da planilha: {0}", nomePlanilha);

		ResultadoProcessamento resultado = new ResultadoProcessamento();

		try (XSSFWorkbook origem = new XSSFWorkbook(this.planilha); XSSFWorkbook destino = new XSSFWorkbook()) {

			XSSFSheet abaOrigem = origem.getSheetAt(0);

			XSSFSheet abaDestino = destino.createSheet(abaOrigem.getSheetName());

			LOGGER.log(Level.INFO, "Iniciando leitura da planilha: {0}", nomePlanilha);

			if (listener != null) {
				listener.iniciou(abaOrigem.getPhysicalNumberOfRows());
			}

			abaOrigem.rowIterator().forEachRemaining(linha -> {
				processarLinha(resultado, linha, abaDestino);

				if (listener != null) {
					listener.leuLinha(linha.getRowNum() + 1);
				}
			});

			LOGGER.log(Level.INFO, "Linhas da planilha processadas: {0}", nomePlanilha);

			if (resultado.erros.isEmpty()) {
				if (abaDestino.getPhysicalNumberOfRows() > 0) {
					Row linha = abaDestino.getRow(0);

					try {
						LOGGER.log(Level.INFO, "Ajustando largura das células de destino da planilha: {0}",
								nomePlanilha);

						Iterator<Cell> celulaIterator = linha.cellIterator();
						while (celulaIterator.hasNext()) {
							Cell celula = celulaIterator.next();
							abaDestino.autoSizeColumn(celula.getColumnIndex());
						}
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE,
								"Erro ao ajustar largura das celulas de destino da planilha: " + nomePlanilha, e);
					}
				}

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				destino.write(out);

				resultado.planilhaProcessada = out;
			}
		}

		return resultado;
	}

	public void processarLinha(ResultadoProcessamento resultado, Row linha, XSSFSheet abaDestino) {
		Row linhaDestino = abaDestino.createRow(linha.getRowNum());

		try {
			cfg.colunas.forEach(cfgColuna -> {
				int colIndex = CellReference.convertColStringToIndex(cfgColuna.nome);

				Cell celulaOrigem = linha.getCell(colIndex);
				Cell celulaDestino = linhaDestino.createCell(cfg.colunas.indexOf(cfgColuna));

				if (cfg.temCabecalho && linha.getRowNum() == 0) {
					if (StringUtil.isNotEmpty(cfgColuna.descricao)) {
						celulaDestino.setCellValue(cfgColuna.descricao);
					} else {
						PlanilhaUtil.setValorCelula(celulaDestino, PlanilhaUtil.getValorCelula(celulaOrigem));
					}
				} else {
					try {
						Object valor = processarCelula(celulaOrigem, cfgColuna.processadores);

						PlanilhaUtil.setValorCelula(celulaDestino, valor);
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Erro ao processar celula", e);
						PlanilhaUtil.tratarErroCelula("origem", resultado, celulaOrigem, e);
					}
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erro ao processar linha", e);
			PlanilhaUtil.tratarErroLinha("origem", resultado, linhaDestino, e);
		}
	}

	private Object processarCelula(Cell celulaOrigem, List<String> processadores) {
		Object valor = PlanilhaUtil.getValorCelula(celulaOrigem);

		if (valor != null && processadores != null) {
			for (Iterator<String> ite = processadores.iterator(); ite.hasNext();) {
				Transformador transformador = getTransformador(ite.next());

				valor = transformador.transformar(valor);
			}
		}

		return valor;
	}

	public static Transformador getTransformador(String nome) {
		Transformador transformador = transformadoresCache.get(nome);

		if (transformador == null) {
			try {
				transformador = (Transformador) Class
						.forName("com.brunomb.processadorplanilhas.endpoints.processador.transformadores." + nome)
						.newInstance();
				transformadoresCache.put(nome, transformador);
			} catch (Exception e) {
				throw new IllegalStateException(
						"Não foi possível carregar o transformador '" + nome + "', motivo: " + e.getMessage(), e);
			}
		}

		return transformador;
	}

	public interface ProcessamentoListener {
		public void iniciou(int quantidadeLinhas);

		public void leuLinha(int linha);
	}
}
