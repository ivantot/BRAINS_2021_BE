package com.iktakademija.Kupindo.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.security.Views;

@Entity
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@Table(name = "Categories")
public class CategoryEntity {

	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Integer id;
	
	@NotBlank(message = "Category name must be provided.")
	@NotNull(message = "Category name must be provided.")
	@Column(nullable = false)
	@JsonView(Views.Public.class)
	private String categoryName;
	
	@Column(nullable = false)
	@JsonView(Views.Public.class)
	@Size(max = 50, message = "Description must be maximum {max} characters long.")
	private String categoryDescription;
	
	@JsonView(Views.Public.class)
	@Version
	private Integer version;
	
	@JsonManagedReference(value = "6")
	@OneToMany(mappedBy = "category", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonView(Views.Public.class)
	private List<OfferEntity> offers = new ArrayList<>();

	public CategoryEntity() {
	}

	public List<OfferEntity> getOffers() {
		return offers;
	}

	public void setOffers(List<OfferEntity> offers) {
		this.offers = offers;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
