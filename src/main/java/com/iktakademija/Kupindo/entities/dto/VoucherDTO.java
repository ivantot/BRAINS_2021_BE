package com.iktakademija.Kupindo.entities.dto;

import java.time.LocalDate;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

public class VoucherDTO {

	@Future(message = "Date inorrect, mustn't be a date in the past.")
	@NotNull(message = "Data mustn't be null.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate expirationDate;

	@NotNull(message = "Data mustn't be null.")
	@AssertFalse(message = "Field must be false.")
	private Boolean isUsed;

	public VoucherDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

}
