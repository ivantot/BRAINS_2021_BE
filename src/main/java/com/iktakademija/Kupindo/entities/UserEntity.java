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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.Kupindo.res.ERole;
import com.iktakademija.Kupindo.security.Views;

@Entity
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@Table(name = "Users")
public class UserEntity {

	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Integer id;
	
	@Column(nullable = false)
	@JsonView(Views.Private.class)
	private String firstName;
	
	@Column(nullable = false)
	@JsonView(Views.Private.class)
	private String lastName;
	
	@Column(unique = true, nullable = false)
	@JsonView(Views.Public.class)
	private String username;
	
	@JsonIgnore
	@Column(nullable = false)
	private String password;
	
	@Column(unique = true)
	@JsonView(Views.Private.class)
	private String email;
	
	@Column(nullable = false)
	@JsonView(Views.Admin.class)
	private ERole userRole;
	
	@Version
	@JsonView(Views.Public.class)
	private Integer version;
	
	@JsonIgnore
	@JsonBackReference(value = "1")
	@OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonView(Views.Private.class)
	private List<OfferEntity> offers = new ArrayList<>();
	
	@JsonIgnore
	@JsonBackReference(value = "2")
	@OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonView(Views.Private.class)
	private List<BillEntity> bills = new ArrayList<>();
	
	@JsonIgnore
	@JsonBackReference(value = "3")
	@OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonView(Views.Private.class)
	private List<VoucherEntity> vouchers = new ArrayList<>();

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<OfferEntity> getOffers() {
		return offers;
	}

	public void setOffers(List<OfferEntity> offers) {
		this.offers = offers;
	}

	public UserEntity() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ERole getUserRole() {
		return userRole;
	}

	public void setUserRole(ERole userRole) {
		this.userRole = userRole;
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
