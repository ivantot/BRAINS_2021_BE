package com.iktakademija.Kupindo.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface OfferService {
	public String updateAvailableOffers(Integer id, Integer boughtOffers, Integer availableOffers);
	public Boolean categoryInOffersExists(Integer categoryId);
	public String uploadOfferImage(MultipartFile file)throws IOException;
}
