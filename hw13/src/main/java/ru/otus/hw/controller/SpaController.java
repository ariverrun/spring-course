package ru.otus.hw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
    
    @GetMapping(value = {
        "/", 
        "/books", 
        "/books/**", 
        "/authors", 
        "/authors/**", 
        "/genres", 
        "/genres/**", 
        "/login", 
        "/logout"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}