package com.iktakademija.Kupindo.entities.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.security.Views;

//TODO check why rootName doesn't work
@JsonRootName(value = "Sales report by category")
@JsonPropertyOrder({ "categoryName", "reportItems",  "sumOfIncomes", "totalNumberOfSoldOffers"})
public class ReportDTO {
	@JsonView(Views.Admin.class)
	@JsonProperty("Category name")
	private String categoryName;
	@JsonView(Views.Admin.class)
	@JsonProperty("Daily reports")
	private List<ReportItemDTO> reportItems;
	@JsonView(Views.Admin.class)
	@JsonProperty("Total income")
	private Double sumOfIncomes;
	@JsonView(Views.Admin.class)
	@JsonProperty("Total number of sold offers")
	private Integer totalNumberOfSoldOffers;

	public ReportDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<ReportItemDTO> getReportItems() {
		return reportItems;
	}

	public void setReportItems(List<ReportItemDTO> reportItems) {
		this.reportItems = reportItems;
	}

	public Double getSumOfIncomes() {
		return sumOfIncomes;
	}

	public void setSumOfIncomes(Double sumOfIncomes) {
		this.sumOfIncomes = sumOfIncomes;
	}

	public Integer getTotalNumberOfSoldOffers() {
		return totalNumberOfSoldOffers;
	}

	public void setTotalNumberOfSoldOffers(Integer totalNumberOfSoldOffers) {
		this.totalNumberOfSoldOffers = totalNumberOfSoldOffers;
	}

}
