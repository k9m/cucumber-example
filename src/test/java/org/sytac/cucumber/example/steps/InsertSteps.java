package org.sytac.cucumber.example.steps;

import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.sytac.cucumber.example.model.SearchResults;
import org.sytac.cucumber.example.model.User;
import org.sytac.cucumber.example.repo.UserRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
@CukeSteps
public class InsertSteps {

    @LocalServerPort
    private int serverPort;

    @Autowired
    UserRepo userRepo;

    private String registerPath;
    private RestTemplate restTemplate;
    private String findByEmailPath;

    @Before
    public void beforeScenario() {
        restTemplate = new RestTemplate();
        registerPath = String.format("http://localhost:%d/users/register", serverPort);
        findByEmailPath = String.format("http://localhost:%d/users/search/findByEmail", serverPort);
    }

    @Given("^the following users are present in the system$")
    public void insertRows(final DataTable table) throws Throwable {
        table.asList(User.class).forEach(user -> {
            log.info("{}", user);
            User savedUser = userRepo.save(user);
            assertNotNull(savedUser.getId());
        });
    }

    @Given("^the following users are registered$")
    public void registerUser(final DataTable table) throws Throwable {
        table.asList(User.class).forEach(user -> {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity(user, headers);

            ResponseEntity<User> savedUser = restTemplate.exchange(registerPath, HttpMethod.POST, entity, User.class);
            User retrievedUser = userRepo.findOne(savedUser.getBody().getId());

            assertNotNull(savedUser.getBody().getId());
            assertEquals(savedUser.getBody(), retrievedUser);
        });
    }


    @Then("^there should be (\\d+) users present in the system$")
    public void thereShouldBeUsersPresentInTheSystem(int nrExpectedUsers) throws Throwable {

        List<User> list = new ArrayList<>();
        userRepo.findAll().forEach(list::add);

        assertEquals(nrExpectedUsers, userRepo.count());
    }

    @Given("^all existing users are removed from the system$")
    public void allExistingUsersAreRemovedFromTheSystem() throws Throwable {
        userRepo.deleteAll();
    }

    @Then("^searching for email (.*) should return the following record$")
    public void searchingForEmailShouldReturnTheFollowingRecord(final String email, final DataTable table) throws Throwable {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(findByEmailPath).queryParam("email", email);
        HttpEntity<SearchResults> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                SearchResults.class
        );

        final List<User> expectedUsers = table.asList(User.class);
        final List<User> retrievedUsers = response.getBody().getEmbedded().getUsers();

        validateExpectedUsers(expectedUsers, retrievedUsers);

    }

    private static void validateExpectedUsers(final List<User> expectedUsers, final List<User> retrievedUsers){
        final AtomicInteger nrFound = new AtomicInteger(0);
        expectedUsers.forEach(eu -> {
            User foundUser = retrievedUsers.stream().filter(ru -> eu.getEmail().equals(ru.getEmail())).findAny().orElse(null);
            if(foundUser != null){
                assertEquals(eu.getFirstName(), foundUser.getFirstName());
                assertEquals(eu.getLastName(), foundUser.getLastName());
                assertEquals(eu.getEmail(), foundUser.getEmail());
                assertEquals(eu.getPassword(), foundUser.getPassword());

                nrFound.incrementAndGet();
            }
        });

        assertEquals(expectedUsers.size(), retrievedUsers.size());
        assertEquals(expectedUsers.size(), nrFound.get());
    }
}
