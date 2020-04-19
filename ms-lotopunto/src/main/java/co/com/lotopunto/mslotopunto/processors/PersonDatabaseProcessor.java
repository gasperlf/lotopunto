package co.com.lotopunto.mslotopunto.processors;

import co.com.lotopunto.mslotopunto.entities.Person;
import co.com.lotopunto.mslotopunto.repositories.PersonRepository;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * That component read data from db and check data that is coming from sheet.
 * @author jhovannycanas.
 * @see PersonRepository
 * @see Person
 */
@Component
public class PersonDatabaseProcessor implements Processor {

    private final PersonRepository personRepository;

    public PersonDatabaseProcessor(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        List<Person> people = (List<Person>) exchange.getIn().getBody();
        people = people.parallelStream().map(t -> {
            Optional<Person> personLoto =  personRepository.findByIdentificacion(t.getIdentificacion());
            if (personLoto.isPresent()){
                return personLoto.get();
            }
            else {
                return t;
            }
        }).map(personLoto -> {
            if (personLoto.getEstado().equals("")) {
                personLoto.setEstado("A");
                personLoto.setFechaCreacion(new Date());
            }
            return personLoto;
        }).collect(Collectors.toList());
        exchange.getIn().setBody(people);
    }

}
