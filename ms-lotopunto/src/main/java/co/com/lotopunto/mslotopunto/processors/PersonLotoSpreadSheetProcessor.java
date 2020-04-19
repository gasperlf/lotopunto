package co.com.lotopunto.mslotopunto.processors;

import co.com.lotopunto.mslotopunto.entities.PersonLoto;
import co.com.lotopunto.mslotopunto.repositories.PersonLotoRepository;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonLotoSpreadSheetProcessor implements Processor {

    @Autowired
    private PersonLotoRepository personLotoRepository;

    @Override
    public void process(Exchange exchange) throws Exception {

        List<PersonLoto> personLotosSpreadSheet = (List<PersonLoto>) exchange.getIn().getBody();
        List<PersonLoto> personLotosDatabase = personLotoRepository.findAllActive();

        List<PersonLoto> personLotos = personLotosDatabase.parallelStream().map(t -> {
            for (PersonLoto personLoto: personLotosSpreadSheet) {
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
        exchange.getIn().setBody(personLotos);
    }
}
