package org.example.springbootboilerplate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerRequestDTO {

    @NotBlank(message = "O nome fantasia (tradingName) é obrigatório")
    private String tradingName;

    @NotBlank(message = "O nome do proprietário (ownerName) é obrigatório")
    private String ownerName;

    @NotBlank(message = "O documento (CNPJ/CPF) é obrigatório")
    private String document;

    @NotNull(message = "A área de cobertura (coverageArea) é obrigatória")
    private GeometryDTO coverageArea;

    @NotNull(message = "O endereço (address) é obrigatório")
    private GeometryDTO address;

}
