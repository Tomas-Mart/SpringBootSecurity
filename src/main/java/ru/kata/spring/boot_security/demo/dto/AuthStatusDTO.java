package ru.kata.spring.boot_security.demo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class AuthStatusDTO {

    private boolean authenticated;
    private String email;
    private boolean admin;
}