package org.example.springbootboilerplate.model;

import jakarta.persistence.*;
import lombok.*;

import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import java.awt.*;

@Entity
@Table(name = "partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partner_seq")
    @SequenceGenerator(name = "partner_seq", sequenceName = "partner_id_seq", allocationSize = 50)
    private Long id;

    @Column(name = "trading_name", nullable = false)
    private String tradingName;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "document", nullable = false, unique = true)
    private String document;

    @Column(name = "coverage_area", columnDefinition = "geometry(MultiPolygon, 4326)", nullable = false)
    private MultiPolygon coverageArea;

    @Column(name = "address", columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point address;
}