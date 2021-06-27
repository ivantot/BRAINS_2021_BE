package com.iktakademija.Kupindo.entities.dto;

import java.time.LocalDate;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.security.Views;

public class BillDTO {

	@JsonView(Views.Private.class)
	@NotNull(message = "Data mustn't be null.")
	@AssertFalse(message = "Field must be false.")
	private Boolean paymentMade;

	@JsonView(Views.Private.class)
	@NotNull(message = "Data mustn't be null.")
	@AssertFalse(message = "Field must be false.")
	private Boolean paymentCanceled;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@JsonView(Views.Private.class)
	@Past(message = "Date inorrect, must be a date in the past.")
	private LocalDate billCreated;

	public BillDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Boolean getPaymentMade() {
		return paymentMade;
	}

	public void setPaymentMade(Boolean paymentMade) {
		this.paymentMade = paymentMade;
	}

	public Boolean getPaymentCanceled() {
		return paymentCanceled;
	}

	public void setPaymentCanceled(Boolean paymentCanceled) {
		this.paymentCanceled = paymentCanceled;
	}

	public LocalDate getBillCreated() {
		return billCreated;
	}

	public void setBillCreated(LocalDate billCreated) {
		this.billCreated = billCreated;
	}

}
