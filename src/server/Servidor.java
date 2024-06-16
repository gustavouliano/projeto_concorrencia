package server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Importe XSSFWorkbook aqui
import org.apache.poi.ss.usermodel.*;

public class Servidor {

	public static void main(String[] args) {
		ServerSocket servidor = null;
		try {
			servidor = new ServerSocket(7001);
			System.out.println("Servidor ouvindo na porta: 7001.");

			while (true) {
				Socket conexao = servidor.accept();
				System.out.println(conexao + " conectado.\n");
				new Thread(() -> {
					try {
						trataCliente(conexao);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				servidor.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void trataCliente(Socket conexao) throws Exception {
		ArrayList<String> arquivos = new ArrayList<>();
		Thread.sleep(1000);
		ArrayList<ByteArrayOutputStream> arquivosBytes = recebeArquivos(conexao, arquivos);
		System.out.println("Fim do envio de arquivos.");

		HashMap<String, String> dadosQualitativos = new HashMap<String, String>();
		HashMap<String, Double> dadosQuantitativos = new HashMap<String, Double>();

		ExecutorService executorService = Executors.newCachedThreadPool();
		for (int i = 0; i < arquivosBytes.size(); i++) {
			executorService
					.execute(new Leitura(arquivos.get(i), arquivosBytes.get(i), dadosQualitativos, dadosQuantitativos));
		}
		executorService.shutdown();
		boolean tarefasFinalizadas = executorService.awaitTermination(90, TimeUnit.SECONDS);

		if (!tarefasFinalizadas) {
			System.out.println("Houveram tarefas não finalizadas a tempo");
		}
		ByteArrayOutputStream excelBytes = Relatorio.gerar(dadosQuantitativos, dadosQualitativos, arquivos);
		Email.enviaEmail("Relatório das Filiais", excelBytes);
		enviaArquivo(conexao, excelBytes);
		conexao.close();
	}

	private static ArrayList<ByteArrayOutputStream> recebeArquivos(Socket conexao, ArrayList<String> arquivos)
			throws Exception {
		DataInputStream dataInputStream = new DataInputStream(conexao.getInputStream());
		boolean hasMoreFiles = true;
		ArrayList<ByteArrayOutputStream> arquivosBytes = new ArrayList<>();
		while (hasMoreFiles) {
			try {
				if (dataInputStream.available() == 0) {
					break;
				}
				long tamanhoArquivo = dataInputStream.readLong();
				if (tamanhoArquivo == -1) {
					hasMoreFiles = false;
				} else {
					String arquivoNome = dataInputStream.readUTF();
					arquivos.add(arquivoNome);
					byte[] buffer = new byte[4 * 1024];
					ByteArrayOutputStream arquivoBytes = new ByteArrayOutputStream();
					while (tamanhoArquivo > 0) {
						int bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, tamanhoArquivo));
						arquivoBytes.write(buffer, 0, bytes);
						tamanhoArquivo -= bytes;
					}
					arquivosBytes.add(arquivoBytes);
					System.out.println("Arquivo " + arquivoNome + " recebido com sucesso.");
				}
			} catch (EOFException e) {
				hasMoreFiles = false;
			}
		}
		return arquivosBytes;
	}

	private static void enviaArquivo(Socket conexao, ByteArrayOutputStream excelBytes) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(conexao.getOutputStream());

		// Envia o tamanho do arquivo
		dataOutputStream.writeLong(excelBytes.size());
		// Envia o nome do arquivo
		dataOutputStream.writeUTF("RelatorioFiliais.xlsx");
		// Envia o conteúdo do arquivo
		dataOutputStream.write(excelBytes.toByteArray());
		dataOutputStream.flush();
	}

}
