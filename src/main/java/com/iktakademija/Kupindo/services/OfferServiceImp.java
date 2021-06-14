package com.iktakademija.Kupindo.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iktakademija.Kupindo.entities.OfferEntity;
import com.iktakademija.Kupindo.repositories.OfferRepository;

@Service
public class OfferServiceImp implements OfferService {

	@Autowired
	private OfferRepository offerRepository;

	@PersistenceContext
	private EntityManager em;

	@Override
	public String updateAvailableOffers(Integer id, Integer boughtOffers, Integer availableOffers) {
		// get offer by id from db
		Optional<OfferEntity> offerEntity = offerRepository.findById(id);
		if (!offerEntity.isPresent()) {
			return "Offer with the required id is not in the database!";
		}
		// not implemented! decrement available and increment bought offer count
		//offerEntity.get().setBoughtOffers(offerEntity.get().getBoughtOffers()+1);

		// set bought and available offers per user input
		offerEntity.get().setBoughtOffers(boughtOffers);
		offerEntity.get().setAvailableOffers(availableOffers);
		offerRepository.save(offerEntity.get());
		return "Offer updated!";
	}

	@Override
	public Boolean categoryInOffersExists(Integer id) {
		// query db to see if offers can be matched with provided category id
		String sql = "SELECT o FROM OfferEntity o WHERE o.category.id = :id";
		Query query = em.createQuery(sql);
		query.setParameter("id", id);
		List<OfferEntity> retVal = query.getResultList();
		if (retVal.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public String uploadOfferImage(MultipartFile file) throws IOException {

		// Check if file is empty
		if (file.isEmpty())
			return null;

		// If note empty, save file to disk
		byte[] bytes = file.getBytes();

		// bolje za naiv fajla GUID ili tmestamp + extenzija
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
		String fileName = String.format("%s_%s", UUID.randomUUID().toString(), LocalDate.now().format(formatter));

		Path path = Paths.get("C:\\Users\\admin\\Desktop\\Brains2021\\Spring\\kupindo\\src\\main\\java\\com\\iktakademija\\kupindo\\"
				+ "resources\\uploads\\" + fileName + file.getOriginalFilename());
		Files.write(path, bytes);

		return path.toAbsolutePath().toString();
	}
}
