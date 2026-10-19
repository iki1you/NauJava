package ru.vladimir.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties("passwordHash")
public record UserRequest(String login, String firstName, String lastName,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password) {
}
