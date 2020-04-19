package co.com.lotopunto.mslotopunto.processors;

import co.com.lotopunto.mslotopunto.entities.PersonLoto;
import co.com.lotopunto.mslotopunto.repositories.PersonLotoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PersonLotoDatabaseProcessor implements Processor {

    @Autowired
    private PersonLotoRepository personLotoRepository;

    @Override
    public void process(Exchange exchange) throws Exception {

        List<PersonLoto> personLotos = (List<PersonLoto>) exchange.getIn().getBody();
        personLotos = personLotos.parallelStream().map(t -> {
            Optional<PersonLoto> personLoto =  personLotoRepository.findByIdentificacion(t.getIdentificacion());
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
        exchange.getIn().setBody(personLotos);
    }

}
