package co.com.lotopunto.mslotopunto.services.impl;

import co.com.lotopunto.mslotopunto.dto.LotoResponse;
import co.com.lotopunto.mslotopunto.entities.PersonLoto;
import co.com.lotopunto.mslotopunto.repositories.PersonLotoRepository;
import co.com.lotopunto.mslotopunto.services.LotoPuntoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class LotoPuntoServiceImpl implements LotoPuntoService {

    private final PersonLotoRepository personLotoRepository;

    public LotoPuntoServiceImpl(PersonLotoRepository personLotoRepository) {
        this.personLotoRepository = personLotoRepository;
    }

    @Override
    public List<LotoResponse> getAllLoto() {
        List<PersonLoto> personLotos = personLotoRepository.findAllActive();
        if (personLotos.isEmpty()) {
            return null;
        }

        return personLotos.parallelStream().map(t ->
                LotoResponse.builder().identificacion(t.getIdentificacion()).nombre(t.getNombre())
                        .fechaNacimiento(t.getFechaNacimiento()).concatenado(t.getConcatenado())
                        .sumaVariable(t.getSumaVariable()).fechaCrecion(t.getFechaCreacion()).build())
                .collect(Collectors.toList());
    }

    @Override
    public void saveLotoPersona(List<PersonLoto> personLotos) {
        personLotoRepository.saveAll(personLotos);
    }
}
