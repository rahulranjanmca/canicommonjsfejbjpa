package com.canigenus.common.controller;

import java.io.Serializable;
import java.util.Properties;

import javax.mail.Message; 
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.canigenus.common.model.AbstractApplicationProperties;
import com.canigenus.common.service.GenericServiceImpl;

public abstract class AbstractMailBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract GenericServiceImpl<? extends AbstractApplicationProperties,? extends AbstractApplicationProperties> getCommonService();

	public abstract String getSMTPAddress();

	public abstract int getPort();

	public abstract Class<? extends AbstractApplicationProperties> getApplicationPropertiesClass();

	public boolean sendMail(String fromMail, String fromName, String toEmail,
			String message, String subject, boolean isHtml) {

		try {
			Properties propsTLS = new Properties();

			propsTLS.put("mail.transport.protocol", "smtp");

			propsTLS.put("mail.smtp.host", getSMTPAddress());

			propsTLS.put("mail.smtp.auth", "true");

			Session sessionTLS = Session.getInstance(propsTLS);

			MimeMessage messageTLS = new MimeMessage(sessionTLS);

			messageTLS.setFrom(new InternetAddress(fromMail, fromName));

			messageTLS.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toEmail));

			messageTLS.setSubject(subject);

			if (isHtml) {
				messageTLS.setText(message, "utf-8", "html");
			} else {
				messageTLS.setText(message);
			}

			Transport transportTLS = sessionTLS.getTransport();

			transportTLS.connect(
					getSMTPAddress(),
					getPort(),
					fromMail,
					getCommonService().getEntityByColumnNameAndValue(
							getApplicationPropertiesClass(), "key",
							fromMail + "_PASSWORD").getValue());

			transportTLS.sendMessage(messageTLS, messageTLS.getAllRecipients());

			transportTLS.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
