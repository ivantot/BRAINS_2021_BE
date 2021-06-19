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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@Table(name = "Vouchers")
public class VoucherEntity {

	@Id
	@GeneratedValue
	private Integer id;
	@Column(nullable = false)
	private LocalDate expirationDate;
	@Column(nullable = false)
	private Boolean isUsed;
	@JsonBackReference(value = "4")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY) // ne stavljati json ignore kad nije lista
	@JoinColumn(name = "offer")
	private OfferEntity offer;
	@JsonBackReference(value = "3")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY) // ne stavljati json ignore kad nije lista
	@JoinColumn(name = "user")
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
