package com.iktakademija.Kupindo.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.iktakademija.Kupindo.res.EOfferStatus;

@Entity
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@Table(name = "Offers")
public class OfferEntity {

	@Id
	@GeneratedValue
	private Integer id;
	@Column(nullable = false)
	private String offerName;
	@Column(nullable = false)
	private String offerDescription;
	@Column(nullable = false)
	private Date offerCreated;
	private Date offerExpires;
	@Column(nullable = false)
	private Double regularPrice;
	private Double actionPrice;
	@Column(nullable = false)
	private String imagePath;
	@Column(nullable = false)
	private Integer availableOffers;
	@Column(nullable = false)
	private Integer boughtOffers;
	@Column(nullable = false)
	private EOfferStatus offerStatus;
	@Version
	private Integer version;
	@JsonBackReference(value = "6")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "category")
	private CategoryEntity category;
	@JsonBackReference(value = "1")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user") // ime kolone ce biti address
	private UserEntity user;
	@JsonManagedReference(value = "5")
	@JsonIgnore
	@OneToMany(mappedBy = "offer", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<BillEntity> bills = new ArrayList<>();
	@JsonManagedReference(value = "4")
	@JsonIgnore
	@OneToMany(mappedBy = "offer", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<VoucherEntity> vouchers = new ArrayList<>();

	public OfferEntity() {
	}

	public CategoryEntity getCategory() {
		return category;
	}

	public void setCategory(CategoryEntity category) {
		this.category = category;
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

	public String getOfferName() {
		return offerName;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public String getOfferDescription() {
		return offerDescription;
	}

	public void setOfferDescription(String offerDescription) {
		this.offerDescription = offerDescription;
	}

	public Date getOfferCreated() {
		return offerCreated;
	}

	public void setOfferCreated(Date offerCreated) {
		this.offerCreated = offerCreated;
	}

	public Date getOfferExpires() {
		return offerExpires;
	}

	public void setOfferExpires(Date offerExpires) {
		this.offerExpires = offerExpires;
	}

	public Double getRegularPrice() {
		return regularPrice;
	}

	public void setRegularPrice(Double regularPrice) {
		this.regularPrice = regularPrice;
	}

	public Double getActionPrice() {
		return actionPrice;
	}

	public void setActionPrice(Double actionPrice) {
		this.actionPrice = actionPrice;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Integer getAvailableOffers() {
		return availableOffers;
	}

	public void setAvailableOffers(Integer availableOffers) {
		this.availableOffers = availableOffers;
	}

	public Integer getBoughtOffers() {
		return boughtOffers;
	}

	public void setBoughtOffers(Integer boughtOffers) {
		this.boughtOffers = boughtOffers;
	}

	public EOfferStatus getOfferStatus() {
		return offerStatus;
	}

	public void setOfferStatus(EOfferStatus offerStatus) {
		this.offerStatus = offerStatus;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<BillEntity> getBills() {
		return bills;
	}

	public void setBills(List<BillEntity> bills) {
		this.bills = bills;
	}

	public List<VoucherEntity> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<VoucherEntity> vouchers) {
		this.vouchers = vouchers;
	}

}
