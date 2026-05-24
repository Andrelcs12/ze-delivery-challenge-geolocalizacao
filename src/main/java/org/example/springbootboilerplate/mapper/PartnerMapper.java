package org.example.springbootboilerplate.mapper;

import org.example.springbootboilerplate.dto.GeometryDTO;
import org.example.springbootboilerplate.dto.PartnerRequestDTO;
import org.example.springbootboilerplate.dto.PartnerResponseDTO;
import org.example.springbootboilerplate.model.Partner;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class PartnerMapper {

    private static final int SRID = 4326;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID);
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            dto.setCoverageArea(new GeometryDTO("MultiPolygon", convertJtsMultiPolygonToLayers(partner.getCoverageArea())));
        }

        return dto;
    }

    @SuppressWarnings("rawtypes")
    private Point convertToJtsPoint(GeometryDTO geometryDTO) {
        try {
            List coords = objectMapper.convertValue(geometryDTO.getCoordinates(), List.class);
            double lon = ((Number) coords.get(0)).doubleValue();
            double lat = ((Number) coords.get(1)).doubleValue();
            return geometryFactory.createPoint(new Coordinate(lon, lat));
        } catch (Exception e) {
            throw new IllegalArgumentException("Estrutura de coordenadas inválida para Point. Use [longitude, latitude].");
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private MultiPolygon convertToJtsMultiPolygon(GeometryDTO geometryDTO) {
        try {
            List<List<List<List>>> rawMultiPolygon = objectMapper.convertValue(
                    geometryDTO.getCoordinates(),
                    List.class
            );

            Polygon[] polygons = new Polygon[rawMultiPolygon.size()];

            for (int p = 0; p < rawMultiPolygon.size(); p++) {
                List<List<List>> rawPolygon = rawMultiPolygon.get(p);
                LinearRing shell = null;
                LinearRing[] holes = new LinearRing[rawPolygon.size() - 1];

                for (int r = 0; r < rawPolygon.size(); r++) {
                    List<List> rawRing = rawPolygon.get(r);
                    Coordinate[] coords = new Coordinate[rawRing.size()];

                    for (int c = 0; c < rawRing.size(); c++) {
                        List coord = rawRing.get(c);
                        double lon = ((Number) coord.get(0)).doubleValue();
                        double lat = ((Number) coord.get(1)).doubleValue();
                        coords[c] = new Coordinate(lon, lat);
                    }

                    if (r == 0) {
                        shell = geometryFactory.createLinearRing(coords);
                    } else {
                        holes[r - 1] = geometryFactory.createLinearRing(coords);
                    }
                }
                polygons[p] = geometryFactory.createPolygon(shell, holes);
            }

            return geometryFactory.createMultiPolygon(polygons);

        } catch (Exception e) {
            throw new IllegalArgumentException("Estrutura de coordenadas inválida para MultiPolygon. Verifique os aninhamentos de arrays.");
        }
    }

    private List<List<List<List<Double>>>> convertJtsMultiPolygonToLayers(MultiPolygon multiPolygon) {
        List<List<List<List<Double>>>> rawMultiPolygon = new ArrayList<>();

        for (int p = 0; p < multiPolygon.getNumGeometries(); p++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(p);
            List<List<List<Double>>> rawPolygon = new ArrayList<>();

            rawPolygon.add(convertCoordinatesToList(polygon.getExteriorRing().getCoordinates()));

            for (int h = 0; h < polygon.getNumInteriorRing(); h++) {
                rawPolygon.add(convertCoordinatesToList(polygon.getInteriorRingN(h).getCoordinates()));
            }

            rawMultiPolygon.add(rawPolygon);
        }

        return rawMultiPolygon;
    }

    private List<List<Double>> convertCoordinatesToList(Coordinate[] coordinates) {
        List<List<Double>> rawRing = new ArrayList<>();
        for (Coordinate coord : coordinates) {
            List<Double> point = new ArrayList<>();
            point.add(coord.getX());
            point.add(coord.getY());
            rawRing.add(point);
        }
        return rawRing;
    }
}