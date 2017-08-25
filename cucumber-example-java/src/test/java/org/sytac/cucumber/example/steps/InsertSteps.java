package org.sytac.cucumber.example.steps;

import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.sytac.cucumber.example.model.SearchResults;
import org.sytac.cucumber.example.model.User;
import org.sytac.cucumber.example.repo.UserRepo;
import org.sytac.cucumber.example.util.RestClient;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
@CukeSteps
public class InsertSteps {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RestClient restClient;

    @Before
    public void beforeScenario() {
        log.info("Before Scenario");
    }

    @Given("^all existing users are removed from the system$")
    public void allExistingUsersAreRemovedFromTheSystem() throws Throwable {
        userRepo.deleteAll();
    }

    @Then("^there should be (\\d+) users present in the system$")
    public void thereShouldBeUsersPresentInTheSystem(int nrExpectedUsers){
        final String endpoint = String.format("http://localhost:%d/users/search/findAllBy", serverPort);

        final SearchResults searchResults = restClient.get(endpoint, new HashMap<>(), SearchResults.class).getBody();
        assertEquals(nrExpectedUsers, searchResults.getEmbedded().getUsers().size());
    }

    @Given("^the following users are present in the system$")
    public void insertRows(final DataTable table){
        table.asList(User.class).forEach(user -> {
            User savedUser = userRepo.save(user);
            assertNotNull(savedUser.getId());
            assertEquals(user, savedUser);
        });
    }

    @Given("^the following users are registered$")
    public void registerUser(final DataTable table){
        final String registerPath = String.format("http://localhost:%d/users/register", serverPort);

        table.asList(User.class).forEach(user -> {
            User savedUser = restClient.post(registerPath, user, User.class).getBody();
            User retrievedUser = userRepo.findOne(savedUser.getId());

            assertNotNull(savedUser.getId());
            assertEquals(savedUser, retrievedUser);
        });
    }

    @After
    public void afterScenario() {
        log.info("After Scenario");
    }

}
