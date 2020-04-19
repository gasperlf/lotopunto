package co.com.lotopunto.mslotopunto.services;

import co.com.lotopunto.mslotopunto.dto.PersonResponse;
import co.com.lotopunto.mslotopunto.entities.Person;

import java.util.List;

/**
 * Interface for service layer.
 * @author jhovannycanas
 */
public interface PersonService {

    /**
     * Get all persons actives in the db.
     *
     * @return List of persons.
     */
    List<PersonResponse> getAllLoto();

    /**
     * Save list of persons in the db.
     * @param people List of persons to save.
     */
    void saveLotoPersona(List<Person> people);
}
