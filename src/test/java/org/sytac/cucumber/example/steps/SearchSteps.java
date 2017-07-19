package org.sytac.cucumber.example.steps;

import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.sytac.cucumber.example.model.SearchResults;
import org.sytac.cucumber.example.model.User;
import org.sytac.cucumber.example.util.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@Slf4j
@CukeSteps
public class SearchSteps {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private RestClient restClient;


    @Then("^searching using (.*) REST endpoint for parameter (.*) with value (.*) should return the following records$")
    public void searchingForEmailShouldReturnTheFollowingRecord(
            final String searchBy,
            final String param,
            final String value,
            final DataTable table){

        final String findByPath = String.format("http://localhost:%d/users/search/%s", serverPort, searchBy);
        final Map<String, String> params = new HashMap<>();
        params.put(param, value);

        final SearchResults searchResults = restClient.get(findByPath, params, SearchResults.class).getBody();

        final List<User> expectedUsers = table.asList(User.class);
        final List<User> retrievedUsers = searchResults.getEmbedded().getUsers();

        validateExpectedUsers(expectedUsers, retrievedUsers);

    }

    private static void validateExpectedUsers(final List<User> expectedUsers, final List<User> retrievedUsers){
        final AtomicInteger nrFound = new AtomicInteger(0);
        expectedUsers.forEach(eu -> {
            User foundUser = retrievedUsers.stream().filter(ru -> eu.getEmail().equals(ru.getEmail())).findAny().orElse(null);
            if(foundUser != null){
                assertEquals(eu, foundUser);
                nrFound.incrementAndGet();
            }
        });

        assertEquals(expectedUsers.size(), retrievedUsers.size());
        assertEquals(expectedUsers.size(), nrFound.get());
    }
}
