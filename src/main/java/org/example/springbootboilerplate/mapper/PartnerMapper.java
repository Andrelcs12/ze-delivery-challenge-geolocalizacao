package org.example.springbootboilerplate.mapper;

import org.example.springbootboilerplate.dto.GeometryDTO;
import org.example.springbootboilerplate.dto.PartnerRequestDTO;
import org.example.springbootboilerplate.dto.PartnerResponseDTO;
import org.example.springbootboilerplate.model.Partner;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class PartnerMapper {

    private static final int SRID = 4326;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID);
    private final tools.jackson.databind.ObjectMapper objectMapper = new ObjectMapper();

    public Partner toEntity(PartnerRequestDTO dto) {
        if (dto == null) return null;

        Partner partner = new Partner();
        partner.setTradingName(dto.getTradingName());
        partner.setOwnerName(dto.getOwnerName());
        partner.setDocument(dto.getDocument());

        partner.setAddress(convertToJtsPoint(dto.getAddress()));
        partner.setCoverageArea(convertToJtsMultiPolygon(dto.getCoverageArea()));

        return partner;
    }

    public PartnerResponseDTO toResponseDTO(Partner partner) {
        if (partner == null) return null;

        PartnerResponseDTO dto = new PartnerResponseDTO();
        dto.setId(partner.getId());
        dto.setTradingName(partner.getTradingName());
        dto.setOwnerName(partner.getOwnerName());
        dto.setDocument(partner.getDocument());

        if (partner.getAddress() != null) {
            double[] pointCoords = new double[]{
                    partner.getAddress().getX(),
                    partner.getAddress().getY()
            };
            dto.setAddress(new GeometryDTO("Point", pointCoords));
        }

        if (partner.getCoverageArea() != null) {
            dto.setCoverageArea(new GeometryDTO("MultiPolygon", partner.getCoverageArea().getCoordinates()));
        }

        return dto;
    }

    private Point convertToJtsPoint(GeometryDTO geometryDTO) {
        try {
            List<Number> coords = objectMapper.convertValue(geometryDTO.getCoordinates(), List.class);
            double lon = coords.get(0).doubleValue();
            double lat = coords.get(1).doubleValue();
            return geometryFactory.createPoint(new Coordinate(lon, lat));
        } catch (Exception e) {
            throw new IllegalArgumentException("Estrutura de coordenadas inválida para Point. Use [longitude, latitude].");
        }
    }


    private MultiPolygon convertToJtsMultiPolygon(GeometryDTO geometryDTO) {
        try {

            String geoJsonStr = objectMapper.writeValueAsString(geometryDTO);

            org.locationtech.jts.io.geojson.GeoJsonReader reader = new org.locationtech.jts.io.geojson.GeoJsonReader(geometryFactory);

            return (MultiPolygon) reader.read(geoJsonStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Estrutura de coordenadas inválida para o MultiPolygon do GeoJSON.");
        }
    }
}