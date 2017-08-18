package org.sytac.cucumber.example.model;

/**
 * Created by JacksonGenerator on 7/19/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchResults {
    @JsonProperty("_embedded")
    private Embedded embedded;
    @JsonProperty("_links")
    private Links links;
}