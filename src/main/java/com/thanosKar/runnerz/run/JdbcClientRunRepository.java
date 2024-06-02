package com.thanosKar.runnerz.run;

import org.springframework.jdbc.core.simple.JdbcClient;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

//Repository Annotation
/*
 *Indicates that an annotated class is a "Repository", originally defined by Domain-Driven Design (Evans, 2003) as
 *"a mechanism for encapsulating storage, retrieval, and search behavior which emulates a collection of objects".
 *Teams implementing traditional Jakarta EE patterns such as "Data Access Object" may also apply this stereotype to DAO classes,
 *though care should be taken to understand the distinction between Data Access Object and DDD-style repositories before doing so.
 *This annotation is a general-purpose stereotype and individual teams may narrow their semantics and use as appropriate.
 *A class thus annotated is eligible for Spring DataAccessException translation when used in conjunction with a
 *PersistenceExceptionTranslationPostProcessor. The annotated class is also clarified as to its role in the overall application
 *architecture for the purpose of tooling, aspects, etc.
 *
 *This annotation also serves as a specialization of @Component, allowing for implementation classes to be auto-detected through
 *classpath scanning.
 */
@Repository
public class JdbcClientRunRepository {

    //JdbcClient
/*
A fluent JdbcClient with common JDBC query and update operations, supporting JDBC-style positional
as well as Spring-style named parameters with a convenient unified facade for JDBC PreparedStatement
execution.
    An example for retrieving a query result as a java.util.Optional:
    Optional<Integer> value = client.sql("SELECT AGE FROM CUSTOMER WHERE ID = :id")
    .param("id", 3)
    .query(Integer.class)
    .optional();
    */
    private final JdbcClient jdbcClient;

    public JdbcClientRunRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;//dependency injection
    }

    //Return all records
    public List<Run> findAll(){
        return jdbcClient.sql("select * from run")
                .query(Run.class)
                .list();
    }

    //Return a single record by its id
    public Optional<Run> findById(Integer id){
        return jdbcClient.sql("SELECT id, title,started_on,completed_on,miles,location FROM Run WHERE id = :id")
                .param("id", id)
                .query(Run.class)
                .optional();
    }

    //Create a new record
    public void create(Run run){
        var updated = jdbcClient.sql("INSERT INTO Run(id, title, started_on, completed_on, miles, location) VALUES(?,?,?,?,?,?)")
                .params(List.of(run.id(),run.title(),run.startedOn(),run.completedOn(),run.miles(),run.location().toString()))
                .update();//inserting/updating/deleting something

        Assert.state(updated == 1, "Failed to create run " + run.title());
    }

    //Update a record given the id
    public void update(Run run, Integer id){
        var updated = jdbcClient.sql("UPDATE Run SET title = ?, started_on = ?, completed_on = ?, miles = ?, location = ? WHERE id = ?")
                .params(List.of(run.title(),run.startedOn(),run.completedOn(),run.miles(),run.location().toString(),id))
                .update();

        Assert.state(updated == 1, "Failed to update run " + run.title());
    }

    //Delete a record given the id
    public void delete(Integer id){
        var updated = jdbcClient.sql("DELETE FROM Run WHERE id = :id")
                .param("id",id)
                .update();

        Assert.state(updated == 1, "Failed to delete run " + id);
    }

    //Return the amount of records
    public int count() {
        return jdbcClient.sql("SELECT * FROM Run").query().listOfRows().size();
    }

    //Save all records
    public void saveAll(List<Run> runs){
        runs.stream().forEach(this::create);
    }

    //Return all records given the location
    public List<Run> findByLocation(String location){
        return jdbcClient.sql("SELECT * FROM Run WHERE location = :location")
                .param("location",location)
                .query(Run.class)
                .list();

    }


    // Code for running on local repository
    /*
    private List<Run> runs = new ArrayList<>();

    List<Run> findAll(){
        return runs;
    }

    Optional<Run> findById(int id) {
        return runs.stream()
                .filter(run -> run.id()==id)
                .findFirst();
    }

    void create(Run run){
        runs.add(run);
    }

    void update(Run run, Integer id){
        Optional<Run> existingRun = findById(id);
        if(existingRun.isPresent()){
            runs.set(runs.indexOf(existingRun.get()), run);
        }
    }

    void delete(Integer id){
        runs.removeIf(run -> run.id().equals(id));
    }

    @PostConstruct
    private void init(){
        runs.add(new Run(1,
                "Monday Morning Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES),
                3,
                Location.INDOOR));
        runs.add(new Run(2,
                "Wednesday Evening Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(60, ChronoUnit.MINUTES),
                6,
                Location.INDOOR));}

     */
}
