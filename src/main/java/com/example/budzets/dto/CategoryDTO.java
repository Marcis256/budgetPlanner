package com.example.budzets.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    @NotBlank(message = "Kategorijas nosaukums nedrīkst būt tukšs")
    private String name;
}