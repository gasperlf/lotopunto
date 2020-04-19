package co.com.lotopunto.mslotopunto.services;

import co.com.lotopunto.mslotopunto.dto.LotoResponse;
import co.com.lotopunto.mslotopunto.entities.PersonLoto;

import java.util.List;

public interface LotoPuntoService {

    List<LotoResponse> getAllLoto();

    void saveLotoPersona(List<PersonLoto> personLotos);
}
