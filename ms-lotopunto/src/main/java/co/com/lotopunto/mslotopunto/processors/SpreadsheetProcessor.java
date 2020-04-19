package co.com.lotopunto.mslotopunto.processors;

import co.com.lotopunto.mslotopunto.entities.Person;
import co.com.lotopunto.mslotopunto.repositories.PersonRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * That component read the sheet and become all rows in objects.
 * @author jhovannycanas.
 * @see PersonRepository
 * @see ObjectMapper
 * @see Person
 */
@Component
public class SpreadsheetProcessor implements Processor {

    private final PersonRepository personRepository;

    private ObjectMapper maper = new ObjectMapper();

    public SpreadsheetProcessor(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        JsonNode jsonNode = maper.readTree((String)exchange.getIn().getBody());
        JsonNode valuesCell =  jsonNode.get("values");
        List<Person> people = new ArrayList<>();
        for (JsonNode value:valuesCell) {
            people.add(Person.builder().identificacion(BigInteger.valueOf(value.get(0).asLong()))
                    .nombre(value.get(1).asText()).fechaNacimiento(format.parse(value.get(2).asText()))
                    .sumaVariable(concatenate(value.get(0).asText(),value.get(2).asText()))
                    .estado("")
                    .build());
        }

        people = people.parallelStream().map(t -> {
            t.setConcatenado(t.getNombre().concat("_").concat(t.getSumaVariable().toString()));
            t.setEstado("A");
            t.setFechaCreacion(new Date());
            return t;
        }).collect(Collectors.toList());

        exchange.getIn().setBody(people);
    }

    private Integer concatenate(String cellIdentification, String cellDate) {
        Integer lastDigit = Integer.parseInt(cellIdentification.substring(cellIdentification.length()-1));
        Integer dateDigit = Integer.parseInt(cellDate.substring(6,7));
        return lastDigit + dateDigit;
    }
}
