package org.example.springbootboilerplate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeometryDTO {

    @NotBlank(message = "O tipo da geometria é obrigatório.")
    private String Type;

    @NotNull(message = "As coordenadas são obrigatórias")
    private Object coordinates; // Aceita os arrays aninhados dinamicamente


}
