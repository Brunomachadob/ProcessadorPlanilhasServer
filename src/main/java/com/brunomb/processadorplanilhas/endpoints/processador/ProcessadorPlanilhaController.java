package com.brunomb.processadorplanilhas.endpoints.processador;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.brunomb.processadorplanilhas.endpoints.processador.ConfiguracaoProcessamento.ConfiguracaoColuna;
import com.brunomb.processadorplanilhas.services.FileStorageService;
import com.brunomb.processadorplanilhas.util.ResultadoProcessamento;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/processador")
public class ProcessadorPlanilhaController {

	private static final Logger LOGGER = Logger.getLogger(ProcessadorPlanilhaController.class.getName());

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping()
	public ResponseEntity<Object> handle(@RequestParam("file") MultipartFile file,
			@RequestParam("config") String config) throws IOException {
		ConfiguracaoProcessamento cfg = buildCfg(config);
		
		ProcessadorPlanilha processador = new ProcessadorPlanilha(file.getInputStream(), cfg, null);
		ResultadoProcessamento resultado = processador.processar();

		if (resultado.erros.isEmpty()) {
			byte[] bytes = resultado.planilhaProcessada.toByteArray();

			String fileName = fileStorageService.storeFile(file.getOriginalFilename(), bytes);

			return ResponseEntity.ok().body(fileName);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado.erros);
		}

	}

	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request)
			throws IOException {

		Resource resource = fileStorageService.loadFileAsResource(fileName);

		String contentType = null;

		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			LOGGER.info("Could not determine file type.");
		}

		if (contentType == null) {
			contentType = "application/vnd.ms-excel";
		}
		
		String realFileName = resource.getFilename().split("_")[0];
		realFileName += "Processada.xlsx";
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + realFileName + "\"")
				.header("file-name", realFileName)
				.body(resource);
	}

	@ExceptionHandler
	private void handleIllegalArgumentException(Exception e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}

	private ConfiguracaoProcessamento buildCfg(String config) {
		Constructor constructor = new Constructor(ConfiguracaoProcessamento.class);
		TypeDescription configDesc = new TypeDescription(ConfiguracaoProcessamento.class);
		configDesc.putListPropertyType("colunas", ConfiguracaoColuna.class);
		constructor.addTypeDescription(configDesc);

		return new Yaml(constructor).loadAs(config, ConfiguracaoProcessamento.class);
	}
}
