package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Relatorio {

	public static ByteArrayOutputStream gerar(HashMap<String, Double> dadosQuantitativos,
			HashMap<String, String> dadosQualitativos, ArrayList<String> arquivos) {
		ByteArrayOutputStream excelBytes = criaArquivoExcel();

		try {
			// Ler o Workbook do ByteArrayOutputStream
			ByteArrayInputStream excelBytesInput = new ByteArrayInputStream(excelBytes.toByteArray());

			Workbook workbook = new XSSFWorkbook(excelBytesInput);
			Sheet sheet = workbook.getSheet("Dados Vendas");

			// Adicionar linhas de dados
			for (int i = 0; i < arquivos.size(); i++) {
				String arquivo = arquivos.get(i);
				int lastRowNum = sheet.getLastRowNum();
				Row row = sheet.createRow(lastRowNum + 1);
				row.createCell(0).setCellValue(getNomeFilialSemExtensao(arquivo));
				row.createCell(1).setCellValue(dadosQuantitativos.get(arquivo + "_totalVendas"));
				row.createCell(2).setCellValue(dadosQualitativos.get(arquivo + "_prodMaisVendido"));
				row.createCell(3).setCellValue(dadosQuantitativos.get(arquivo + "_prodMaisVendido"));
				row.createCell(4).setCellValue(dadosQualitativos.get(arquivo + "_regiaoMaisVendido"));
				row.createCell(5).setCellValue(dadosQuantitativos.get(arquivo + "_regiaoMaisVendido"));
			}

			// Escrever de volta para o ByteArrayOutputStream
			workbook.write(excelBytes);
			excelBytes.flush();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return excelBytes;
	}

	private static ByteArrayOutputStream criaArquivoExcel() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			// Criar um novo Workbook e adicionar uma nova Sheet
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Dados Vendas");

			// Adicionar cabeçalhos das colunas
			String[][] headers = { { "Filial", "Total Vendas", "Produto mais vendido", "Quantidade produto vendido",
					"Região com maior venda", "Valor total região com maior venda" } };

			int rowCount = 0;

			// Adicionar cabeçalhos
			Row headerRow = sheet.createRow(rowCount++);
			for (int i = 0; i < headers[0].length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[0][i]);
			}

			// Escrever o workbook no OutputStream
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outputStream;
	}

	private static String getNomeFilialSemExtensao(String nomeArquivo) {
		Pattern regexFilial = Pattern.compile("^(.*?)(?=\\.)");
		Matcher matcher = regexFilial.matcher(nomeArquivo);
		String nomeFilial = "";
		if (matcher.find()) {
			nomeFilial = matcher.group(0);
		}
		return nomeFilial;
	}

}
