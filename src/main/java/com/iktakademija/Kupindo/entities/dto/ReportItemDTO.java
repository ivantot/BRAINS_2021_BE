package com.iktakademija.Kupindo.entities.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.security.Views;

// TODO check why rootName doesn't work
@JsonRootName(value = "Daily report of sales")
@JsonPropertyOrder({ "date", "income",  "numberOfOffers"})
public class ReportItemDTO {
	@JsonView(Views.Admin.class)
	@JsonProperty("Date of report")
	private LocalDate date;
	
	@JsonView(Views.Admin.class)
	@JsonProperty("Daily income")
	private Double income;
	
	@JsonView(Views.Admin.class)
	@JsonProperty("Daily number of offers")
	private Integer numberOfOffers;

	public ReportItemDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Double getIncome() {
		return income;
	}

	public void setIncome(Double income) {
		this.income = income;
	}

	public Integer getNumberOfOffers() {
		return numberOfOffers;
	}

	public void setNumberOfOffers(Integer numberOfOffers) {
		this.numberOfOffers = numberOfOffers;
	}

}
