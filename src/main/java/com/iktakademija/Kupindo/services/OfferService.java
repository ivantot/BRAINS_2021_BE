package com.iktakademija.Kupindo.services;

public interface OfferService {
	public String updateAvailableOffers(Integer id, Integer boughtOffers, Integer availableOffers);
	public Boolean categoryInOffersExists(Integer categoryId);
}
