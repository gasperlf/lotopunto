package co.com.lotopunto.mslotopunto.services;

import co.com.lotopunto.mslotopunto.dto.LotoResponse;
import co.com.lotopunto.mslotopunto.entities.PersonLoto;

import java.util.List;

/**
 * Interface for service layer.
 * @author jhovannycanas
 */
public interface LotoPuntoService {

    /**
     * Get all persons actives in the db.
     *
     * @return List of persons.
     */
    List<LotoResponse> getAllLoto();

    /**
     * Save list of persons in the db.
     * @param personLotos List of persons to save.
     */
    void saveLotoPersona(List<PersonLoto> personLotos);
}
