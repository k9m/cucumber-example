package org.sytac.cucumber.example.steps;

import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.sytac.cucumber.example.model.User;
import org.sytac.cucumber.example.repo.UserRepo;
import org.sytac.cucumber.example.util.RestClient;

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
        assertEquals(nrExpectedUsers, userRepo.count());
    }

    @Given("^the following users are present in the system$")
    public void insertRows(final DataTable table){
        table.asList(User.class).forEach(user -> {
            log.info("{}", user);
            User savedUser = userRepo.save(user);
            assertNotNull(savedUser.getId());
        });
    }

    @Given("^the following users are registered$")
    public void registerUser(final DataTable table){
        final String registerPath = String.format("http://localhost:%d/users/register", serverPort);

        table.asList(User.class).forEach(user -> {
            ResponseEntity<User> savedUser = restClient.post(registerPath, user, User.class);
            User retrievedUser = userRepo.findOne(savedUser.getBody().getId());

            assertNotNull(savedUser.getBody().getId());
            assertEquals(savedUser.getBody(), retrievedUser);
        });
    }

    @After
    public void afterScenario() {
        log.info("After Scenario");
    }

}
