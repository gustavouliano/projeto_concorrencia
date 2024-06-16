package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import jakarta.activation.DataHandler;

public class Email {

	public static void enviaEmail(String assunto, ByteArrayOutputStream excelBytes) throws IOException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				try {
					return new PasswordAuthentication(getProp().getProperty("prop.email"), getProp().getProperty("prop.email.password"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(getProp().getProperty("prop.email")));
			Address[] toUser = InternetAddress.parse("e-mail que recebe");
			message.setRecipients(Message.RecipientType.TO, toUser);
			message.setSubject(assunto); // Assunto

			Multipart multipart = new MimeMultipart();
			MimeBodyPart bodyPart = new MimeBodyPart();

			// Parte do anexo Excel
			InputStream is = new ByteArrayInputStream(excelBytes.toByteArray());
			bodyPart = new MimeBodyPart();
			DataSource source = new javax.mail.util.ByteArrayDataSource(is,
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			bodyPart.setDataHandler(new javax.activation.DataHandler(source));

			bodyPart.setHeader("Content-Disposition", "attachment; filename=\"dados_filiais.xlsx\"");
			multipart.addBodyPart(bodyPart);

			message.setContent(multipart);

			Transport.send(message);
			System.out.println("E-mail enviado com sucesso.");
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
			System.out.println("Não foi possível enviar o e-mail");
		}

	}

	private static Properties getProp() throws IOException {
		Properties props = new Properties();
		FileInputStream file = new FileInputStream("/home/gustavo/eclipse-workspace/projeto_final_1/src/properties/dados.properties");
		props.load(file);
		return props;

	}

}
