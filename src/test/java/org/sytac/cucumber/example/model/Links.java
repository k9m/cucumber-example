package org.sytac.cucumber.example.model;

/**
 * Created by JacksonGenerator on 7/19/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Links {
    @JsonProperty("self")
    private Self self;
    @JsonProperty("user")
    private User user;
}