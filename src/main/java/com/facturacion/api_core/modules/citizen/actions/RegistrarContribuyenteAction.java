package com.facturacion.api_core.modules.citizen.actions;

import com.facturacion.api_core.modules.citizen.domain.model.Contribuyente;
import com.facturacion.api_core.modules.citizen.domain.model.Direccion;
import com.facturacion.api_core.modules.citizen.domain.repository.ContribuyenteRepository;
import com.facturacion.api_core.modules.citizen.dto.ContribuyenteResponse;
import com.facturacion.api_core.modules.citizen.dto.RegistrarContribuyenteRequest;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.ValidationException;
import com.facturacion.api_core.shared.security.DataSanitizer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
public class RegistrarContribuyenteAction {

    private final ContribuyenteRepository contribuyenteRepository;

    public RegistrarContribuyenteAction(ContribuyenteRepository contribuyenteRepository) {
        this.contribuyenteRepository = contribuyenteRepository;
    }

    @Transactional
    public ContribuyenteResponse execute(RegistrarContribuyenteRequest request) {
        Objects.requireNonNull(request, "La solicitud de registro no puede ser nula");
        
        String tenantId = TenantContext.getTenantId();
        String docNormalizado = DataSanitizer.normalizeDocument(request.numeroIdentificacion());

        if (contribuyenteRepository.existsByNumeroIdentificacionAndTenantId(docNormalizado, tenantId)) {
            throw new ValidationException(String.format(
                    "El contribuyente con identificacion '%s' ya se encuentra registrado en el GAD actual (%s)",
                    docNormalizado, tenantId));
        }

        Direccion direccion = request.direccion() != null ? request.direccion().toDomain() : null;

        Contribuyente nuevoContribuyente = new Contribuyente(
                request.tipoIdentificacion(),
                docNormalizado,
                request.nombres(),
                request.apellidos(),
                request.email(),
                request.telefono(),
                direccion
        );

        Contribuyente guardado = contribuyenteRepository.save(nuevoContribuyente);
        return ContribuyenteResponse.fromDomain(guardado);
    }
}
