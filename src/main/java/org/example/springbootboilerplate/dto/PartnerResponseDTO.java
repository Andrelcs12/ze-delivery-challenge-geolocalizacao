package org.example.springbootboilerplate.dto;


import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerResponseDTO {

    private Long id;

    private String tradingName;

    private String ownerName;

    private String document;

    private GeometryDTO coverageArea;

    private GeometryDTO address;
}
