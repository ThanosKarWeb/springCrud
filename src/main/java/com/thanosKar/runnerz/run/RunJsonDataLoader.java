package com.thanosKar.runnerz.run;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class RunJsonDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RunJsonDataLoader.class);
    private final JdbcClientRunRepository runRepository;

    /*
    * ObjectMapper provides functionality for reading and writing JSON, either to and from
    *  basic POJOs (Plain Old Java Objects), or to and from a general-purpose JSON Tree Model
    *  (JsonNode), as well as related functionality for performing conversions
    * */
    private final ObjectMapper objectMapper;

    /*Dependency Injection: When an instance of this clas is created
    it will see it is dependent on RunRepository. Spring knows about
    that class (is annotated with @Repository) and will inject it into the constructor */
    public RunJsonDataLoader(JdbcClientRunRepository runRepository, ObjectMapper objectMapper) {
        this.runRepository = runRepository;
        this.objectMapper = objectMapper;
    }

    //This method reads JSON data and maps them to a Runs record made for that reason
    @Override
    public void run(String... args) throws Exception {
        if(runRepository.count() == 0){
            try(InputStream inputStream = TypeReference.class.getResourceAsStream("/data/runs.json")) {
                Runs allRuns = objectMapper.readValue(inputStream, Runs.class);
                log.info("Reading {} runs from JSON data and saving to a database", allRuns.runs().size());
                runRepository.saveAll(allRuns.runs());
            }catch(IOException e){
                throw new RuntimeException("Failed to read JSON data", e);
            }
        } else {
            log.info("Not loading Runs from JSON data because the collection contains data.");
        }
    }
}
