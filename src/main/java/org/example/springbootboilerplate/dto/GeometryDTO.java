package org.example.springbootboilerplate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("type")
    private String type;

    @NotNull(message = "As coordenadas são obrigatórias")
    private Object coordinates;
}