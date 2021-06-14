package com.iktakademija.Kupindo.services;

import java.time.format.DateTimeFormatter;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.iktakademija.Kupindo.dto.EmailObject;

@Service
public class EmailServiceImp implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendTemplateMessage(EmailObject object) throws Exception {
		MimeMessage mail = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);

		helper.setTo(object.getTo());
		helper.setSubject(object.getSubject());
		String text = "<table border='2'> <tbody> <tr>"
				+ "<td style='border-style: hidden;' colspan='2'><strong>Buyer</strong></td>"
				+ "<td style='border-style: hidden;'><strong>Offer</strong></td>"
				+ "<td style='border-style: hidden;'><strong>Price</strong></td>"
				+ "<td style='border-style: hidden;'><strong>Expires Date</strong></td> </tr> <tr>"
				+ "<td style='border-style: hidden;'>%s</td> <td style='border-style: hidden;'>%s</td>"
				+ "<td style='border-style: hidden;'>%s</td> <td style='border-style: hidden;'>%.1f</td>"
				+ "<td style='border-style: hidden;'>%s</td> </tr> </tbody></table>";

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
		text = String.format(text, object.getName(), object.getLastname(), "Offer " + object.getOffer().toString(),
				object.getPrice().floatValue(), object.getDate().format(formatter));
		helper.setText(text, true);
		mailSender.send(mail);

	}
}
