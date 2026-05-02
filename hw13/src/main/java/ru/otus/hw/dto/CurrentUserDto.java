package ru.otus.hw.dto;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CurrentUserDto(
    boolean authenticated,
    String username,
    Collection<String> roles
) {    
}