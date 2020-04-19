package co.com.lotopunto.mslotopunto.services.impl;

import co.com.lotopunto.mslotopunto.dto.PersonResponse;
import co.com.lotopunto.mslotopunto.entities.Person;
import co.com.lotopunto.mslotopunto.repositories.PersonRepository;
import co.com.lotopunto.mslotopunto.services.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<PersonResponse> getAllLoto() {
        List<Person> people = personRepository.findAllActive();
        if (people.isEmpty()) {
            return null;
        }

        return people.parallelStream().map(t ->
                PersonResponse.builder().identificacion(t.getIdentificacion()).nombre(t.getNombre())
                        .fechaNacimiento(t.getFechaNacimiento()).concatenado(t.getConcatenado())
                        .sumaVariable(t.getSumaVariable()).fechaCrecion(t.getFechaCreacion()).build())
                .collect(Collectors.toList());
    }

    @Override
    public void saveLotoPersona(List<Person> people) {
        personRepository.saveAll(people);
    }
}
