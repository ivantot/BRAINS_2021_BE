package com.iktakademija.Kupindo.entities;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.security.Views;

@Entity
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@Table(name = "Vouchers")
public class VoucherEntity {

	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Integer id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@Column(nullable = false)
	@JsonView(Views.Public.class)
	private LocalDate expirationDate;
	
	@Column(nullable = false)
	@JsonView(Views.Admin.class)
	private Boolean isUsed;
	
	@JsonView(Views.Public.class)
	@Version
	private Integer version;
	
	@JsonManagedReference(value = "4")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY) // ne stavljati json ignore kad nije lista
	@JoinColumn(name = "offer")
	@JsonView(Views.Private.class)
	private OfferEntity offer;
	
	@JsonManagedReference(value = "3")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY) // ne stavljati json ignore kad nije lista
	@JoinColumn(name = "user")
	@JsonView(Views.Private.class)
	private UserEntity user;
	

	public VoucherEntity() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public OfferEntity getOffer() {
		return offer;
	}

	public void setOffer(OfferEntity offer) {
		this.offer = offer;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

}
