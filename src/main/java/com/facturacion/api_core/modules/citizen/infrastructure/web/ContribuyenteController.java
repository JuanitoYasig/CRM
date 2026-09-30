package com.facturacion.api_core.modules.citizen.infrastructure.web;

import com.facturacion.api_core.modules.citizen.actions.RegistrarContribuyenteAction;
import com.facturacion.api_core.modules.citizen.dto.ContribuyenteResponse;
import com.facturacion.api_core.modules.citizen.dto.RegistrarContribuyenteRequest;
import com.facturacion.api_core.modules.citizen.services.ContribuyenteQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contribuyentes")
public class ContribuyenteController {

    private final RegistrarContribuyenteAction registrarAction;
    private final ContribuyenteQueryService queryService;

    public ContribuyenteController(RegistrarContribuyenteAction registrarAction, ContribuyenteQueryService queryService) {
        this.registrarAction = registrarAction;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<ContribuyenteResponse> registrar(@RequestBody RegistrarContribuyenteRequest request) {
        ContribuyenteResponse response = registrarAction.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContribuyenteResponse>> listarTodos() {
        return ResponseEntity.ok(queryService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContribuyenteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.buscarPorId(id));
    }

    @GetMapping("/identificacion/{documento}")
    public ResponseEntity<ContribuyenteResponse> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(queryService.buscarPorDocumento(documento));
    }
}
