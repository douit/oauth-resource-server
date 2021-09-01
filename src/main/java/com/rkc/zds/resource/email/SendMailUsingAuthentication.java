package com.rkc.zds.resource.email;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.web.multipart.MultipartFile;

public class SendMailUsingAuthentication {

	public void postMail(String[] recipients, String subject, String message, String from, List<MultipartFile> list)
			throws MessagingException {
		boolean debug = false;

		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.zdslogic.com");
		props.put("mail.smtp.auth", "true");

		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getDefaultInstance(props, auth);

		session.setDebug(debug);

		MimeMessage msg = new MimeMessage(session);

		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		msg.setSubject(subject);
		// msg.setContent(message, "text/plain");

		// Set the email msg text.
		MimeBodyPart messagePart = new MimeBodyPart();
		// messagePart.setText(message);
		messagePart.setContent(message, "text/html");

		// Create Multipart E-Mail.
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messagePart);

		if (list != null) {

			for (MultipartFile temp : list) {

				// Set the email attachment file
				MultipartFile attachment = temp;

				String original = attachment.getOriginalFilename();

				String fileName = attachment.getName();

				File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + original);
				try {
					attachment.transferTo(convFile);
				} catch (IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				FileDataSource fileDataSource = new FileDataSource(convFile);

				MimeBodyPart attachmentPart = new MimeBodyPart();
				attachmentPart.setDataHandler(new DataHandler(fileDataSource));
				attachmentPart.setFileName(original);

				multipart.addBodyPart(attachmentPart);

			}
		}

		msg.setContent(multipart);

		Transport.send(msg);
	}

}
