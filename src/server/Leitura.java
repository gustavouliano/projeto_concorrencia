package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Importe XSSFWorkbook aqui
import org.apache.poi.ss.usermodel.*;

public class Leitura implements Runnable {

	private final String nomeArquivo;
	private HashMap<String, String> dadosQualitativos;
	private HashMap<String, Double> dadosQuantitativos;
	private ByteArrayOutputStream arquivoBytes;

	public Leitura(String nomeArquivo, ByteArrayOutputStream arquivoBytes, HashMap<String, String> dadosQualitativos,
			HashMap<String, Double> dadosQuantitativos) {
		this.nomeArquivo = nomeArquivo;
		this.dadosQualitativos = dadosQualitativos;
		this.dadosQuantitativos = dadosQuantitativos;
		this.arquivoBytes = arquivoBytes;
	}

	public void run() {
		double totalVendas = 0;
		Map<String, Integer> vendasPorProduto = new HashMap<>();
		Map<String, Integer> vendasPorRegiaoQuantitativo = new HashMap<>();
		Map<String, Double> vendasPorRegiaoMonetario = new HashMap<>();

		try {
			ByteArrayInputStream arquivoInputStream = new ByteArrayInputStream(arquivoBytes.toByteArray());
			Workbook livroDeTrabalhos = new XSSFWorkbook(arquivoInputStream);
			Sheet folha = livroDeTrabalhos.getSheetAt(0);

			Iterator<Row> iteradorLinhas = folha.iterator();

			System.out.println();

			while (iteradorLinhas.hasNext()) {
				Row linha = iteradorLinhas.next();
				if (linha.getRowNum() > 0) { // Começa a contar a partir da segunda linha
					Cell qtdCell = linha.getCell(3);
					Cell valorUnitarioCell = linha.getCell(4);
					Cell regiaoCell = linha.getCell(1);

					if (valorUnitarioCell != null && qtdCell != null && regiaoCell != null) {
						double valorUnitario = valorUnitarioCell.getNumericCellValue();
						int qtd = (int) qtdCell.getNumericCellValue();

						totalVendas += qtd * valorUnitario;

						// Atualiza a quantidade total vendida do produto
						String produto = linha.getCell(2).getStringCellValue(); // Supondo que o nome do produto esteja
																				// na coluna 2
						vendasPorProduto.put(produto, vendasPorProduto.getOrDefault(produto, 0) + qtd);

						// Atualiza as vendas por região (quantitativo)
						String regiao = regiaoCell.getStringCellValue();
						vendasPorRegiaoQuantitativo.put(regiao,
								vendasPorRegiaoQuantitativo.getOrDefault(regiao, 0) + qtd);

						// Atualiza as vendas por região (monetário)
						vendasPorRegiaoMonetario.put(regiao,
								vendasPorRegiaoMonetario.getOrDefault(regiao, 0.0) + (qtd * valorUnitario));

						System.out.printf("%d\t%.2f\n", qtd, valorUnitario); // Imprime a quantidade e o valor unitário
					} else {
						break; // Linha em branco = fim do arquivo
					}
				}
			}

			System.out.println("\nTotal de vendas: " + totalVendas + " do arquivo " + nomeArquivo);
			// Encontra o produto mais vendido
			String produtoMaisVendido = vendasPorProduto.entrySet().stream().max(Map.Entry.comparingByValue())
					.map(Map.Entry::getKey).orElse(null);
			double quantidadeMaisVendida = vendasPorProduto.get(produtoMaisVendido);

			System.out.println(
					"Produto mais vendido: " + produtoMaisVendido + " => " + quantidadeMaisVendida + " itens.");

			// Encontra a região com o maior valor total de vendas
			String regiaoMaisVendidaValorTotal = vendasPorRegiaoMonetario.entrySet().stream()
					.max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
			double totalVendasRegiaoMaisVendida = vendasPorRegiaoMonetario.get(regiaoMaisVendidaValorTotal);

			System.out.println("Região com o maior valor total de vendas: " + regiaoMaisVendidaValorTotal + " => "
					+ totalVendasRegiaoMaisVendida + " reais.");

			dadosQuantitativos.put(nomeArquivo + "_totalVendas", totalVendas);
			dadosQualitativos.put(nomeArquivo + "_prodMaisVendido", produtoMaisVendido);
			dadosQuantitativos.put(nomeArquivo + "_prodMaisVendido", quantidadeMaisVendida);
			dadosQualitativos.put(nomeArquivo + "_regiaoMaisVendido", regiaoMaisVendidaValorTotal);
			dadosQuantitativos.put(nomeArquivo + "_regiaoMaisVendido", totalVendasRegiaoMaisVendida);
			livroDeTrabalhos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
