package client;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Cliente {

	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;
	private static String caminho = "/home/gustavo/eclipse-workspace/projeto_final_1/src/files/";

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("localhost", 7001);
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			enviaArquivo(caminho + "Filial1.xlsx");
			enviaArquivo(caminho + "Filial2.xlsx");
			enviaArquivo(caminho + "Filial3.xlsx");
			enviaArquivo(caminho + "Filial4.xlsx");
			enviaArquivo(caminho + "Filial5.xlsx");

			System.out.println("Todos os arquivos foram enviados.");
			recebeArquivo(socket);
			dataInputStream.close();
			dataOutputStream.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void enviaArquivo(String caminho) throws Exception {
		int bytes = 0;
		File arquivo = new File(caminho);
		FileInputStream arquivEntrada = new FileInputStream(arquivo);

		// Envia o tamanho do arquivo
		dataOutputStream.writeLong(arquivo.length());
		// Envia o nome do arquivo
		dataOutputStream.writeUTF(arquivo.getName());
		// break file into chunks
		byte[] buffer = new byte[4 * 1024];
		while ((bytes = arquivEntrada.read(buffer)) != -1) {
			dataOutputStream.write(buffer, 0, bytes);
			dataOutputStream.flush();
		}
		arquivEntrada.close();
	}

    private static void recebeArquivo(Socket socket) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

        // Recebe o tamanho do arquivo
        long tamanhoArquivo = dataInputStream.readLong();
        // Recebe o nome do arquivo
        String nomeArquivo = dataInputStream.readUTF();

        // Prepara o buffer para receber o arquivo
        byte[] buffer = new byte[4 * 1024];
        try (FileOutputStream fileOutputStream = new FileOutputStream(caminho + nomeArquivo)) {
            int bytesRead;
            while (tamanhoArquivo > 0 && (bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, tamanhoArquivo))) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                tamanhoArquivo -= bytesRead;
            }
        }

        System.out.println("Arquivo " + nomeArquivo + " recebido com sucesso.");
    }

}