package org.example.springbootboilerplate.repository;

import org.example.springbootboilerplate.model.Partner;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {


    boolean existsByDocument(String document);

    @Query(value = """
        SELECT p.* 
        FROM partners p 
        WHERE ST_Contains(p.coverage_area, :location) = true 
        ORDER BY ST_Distance(p.address, :location) ASC 
        LIMIT 1
        """, nativeQuery = true)
    Optional<Partner> findNearestPartnerCoveringLocation(@Param("location") Point location);
}