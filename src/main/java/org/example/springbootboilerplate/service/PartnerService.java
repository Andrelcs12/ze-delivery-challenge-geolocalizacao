package org.example.springbootboilerplate.service;


import jakarta.transaction.Transactional;
import org.example.springbootboilerplate.dto.PartnerRequestDTO;
import org.example.springbootboilerplate.dto.PartnerResponseDTO;
import org.example.springbootboilerplate.exception.PartnerNotFoundException;
import org.example.springbootboilerplate.exception.UniqueDocumentException;
import org.example.springbootboilerplate.mapper.PartnerMapper;
import org.example.springbootboilerplate.model.Partner;
import org.example.springbootboilerplate.repository.PartnerRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    private final GeometryFactory geometryFactory;

    public PartnerService(PartnerRepository partnerRepository, PartnerMapper partnerMapper) {
        this.partnerRepository = partnerRepository;
        this.partnerMapper = partnerMapper;
        // Centraliza o SRID 4326 para as buscas
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    @Transactional
    public PartnerResponseDTO createPartner(PartnerRequestDTO partnerRequestDTO) {
        if(partnerRepository.existsByDocument(partnerRequestDTO.getDocument())) {
            throw new UniqueDocumentException("Loja Partner com documento " + partnerRequestDTO.getDocument() + " já existe.");
        }

        Partner partner = partnerMapper.toEntity(partnerRequestDTO);
        Partner savedPartner = partnerRepository.save(partner);

        return partnerMapper.toResponseDTO(savedPartner);
    }

    @Transactional
    public PartnerResponseDTO getPartnerById(Long id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new PartnerNotFoundException("Partner não encontrado com o ID: " + id));
        return partnerMapper.toResponseDTO(partner);
    }

    @Transactional
    public PartnerResponseDTO searchNearestPartner(double lng, double lat) {
        Point searchPoint = geometryFactory.createPoint(new Coordinate(lng, lat));

        Partner nearestPartner = partnerRepository.findNearestPartnerCoveringLocation(searchPoint)
                .orElseThrow(() -> new PartnerNotFoundException("Nenhum parceiro atende a essa localização: [" + lng + ", " + lat + "]"));

        return partnerMapper.toResponseDTO(nearestPartner);
    }
}
