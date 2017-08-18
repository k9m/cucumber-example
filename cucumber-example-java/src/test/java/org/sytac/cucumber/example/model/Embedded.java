package org.sytac.cucumber.example.model;

/**
 * Created by JacksonGenerator on 7/19/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Embedded {
    @JsonProperty("users")
    private List<User> users;
}