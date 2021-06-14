package com.iktakademija.Kupindo.services;

import com.iktakademija.Kupindo.dto.EmailObject;

public interface EmailService {
	
	public void sendTemplateMessage(EmailObject object) throws Exception;

}
