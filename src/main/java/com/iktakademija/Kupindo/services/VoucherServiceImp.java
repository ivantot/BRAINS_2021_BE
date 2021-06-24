package com.iktakademija.Kupindo.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iktakademija.Kupindo.dto.EmailObject;
import com.iktakademija.Kupindo.entities.BillEntity;
import com.iktakademija.Kupindo.entities.VoucherEntity;
import com.iktakademija.Kupindo.repositories.VoucherRepository;

@Service
public class VoucherServiceImp implements VoucherService {

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private EmailService emailService;

	@Override
	public VoucherEntity createVoucherAfterPayment(BillEntity bill) {

		// Trivial check
		if (bill == null)
			return null;

		// Create new voucher
		VoucherEntity voucherEntity = new VoucherEntity();
		voucherEntity.setExpirationDate(LocalDate.now().plusDays(10));
		voucherEntity.setIsUsed(false);
		voucherEntity.setUser(bill.getUser());
		voucherEntity.setOffer(bill.getOffer());

		// Update database
		voucherRepository.save(voucherEntity);

		// Send email		
		EmailObject object = new EmailObject();
		object.setTo(bill.getUser().getEmail());
		object.setSubject("Your voucher for " + bill.getOffer().getOfferName());
		object.setName(bill.getUser().getFirstName());
		object.setLastname(bill.getUser().getLastName());
		object.setOffer(bill.getOffer().getId());
		object.setPrice(bill.getOffer().getActionPrice());
		//add further check, which price is selling price, action or regular
		object.setDate(voucherEntity.getExpirationDate());
		try {
			emailService.sendTemplateMessage(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Return new voucher
		return voucherEntity;
	}

}
