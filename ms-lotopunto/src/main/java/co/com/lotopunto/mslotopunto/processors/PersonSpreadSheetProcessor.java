package co.com.lotopunto.mslotopunto.processors;

import co.com.lotopunto.mslotopunto.entities.Person;
import co.com.lotopunto.mslotopunto.repositories.PersonRepository;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * That component read data from db and sheet check which must be to deleted.
 * @author jhovannycanas.
 * @see PersonRepository
 * @see Person
 */

@Component
public class PersonSpreadSheetProcessor implements Processor {

    private final PersonRepository personRepository;

    public PersonSpreadSheetProcessor(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        List<Person> personLotosSpreadSheet = (List<Person>) exchange.getIn().getBody();
        List<Person> personLotosDatabase = personRepository.findAllActive();

        List<Person> people = personLotosDatabase.parallelStream().map(t -> {
            for (Person personLoto: personLotosSpreadSheet) {
                if (personLoto.getIdentificacion().equals(t.getIdentificacion())){
                    t.setEstado("E");
                }
        }
            return t;
        }).filter(personLoto -> personLoto.getEstado().equals("A"))
                .map(personLoto -> {
                    personLoto.setEstado("I");
                return personLoto;})
                .collect(Collectors.toList());
        exchange.getIn().setBody(people);
    }
}
