package com.iktakademija.Kupindo.entities;

import java.util.Date;

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
@Table(name = "Bills")
public class BillEntity {

	@GeneratedValue
	@Id
	private Integer id;
	@Column(nullable = false)
	private Boolean paymentMade;
	@Column(nullable = false)
	private Boolean paymentCanceled;
	@Column(nullable = false)
	private Date billCreated;
	@JsonBackReference(value = "5")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "offer")
	private OfferEntity offer;
	@JsonBackReference(value = "2")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private UserEntity user;
	
	public BillEntity() {
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


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Date getBillCreated() {
		return billCreated;
	}

	public void setBillCreated(Date billCreated) {
		this.billCreated = billCreated;
	}

}
