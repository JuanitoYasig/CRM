package com.facturacion.api_core.modules.ticket.infrastructure.web;

import com.facturacion.api_core.modules.ticket.actions.CrearTramiteAction;
import com.facturacion.api_core.modules.ticket.actions.DerivarTramiteAction;
import com.facturacion.api_core.modules.ticket.actions.ResolverTramiteAction;
import com.facturacion.api_core.modules.ticket.domain.model.EstadoTramite;
import com.facturacion.api_core.modules.ticket.dto.*;
import com.facturacion.api_core.modules.ticket.services.TramiteQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tramites")
public class TramiteController {

    private final CrearTramiteAction crearAction;
    private final DerivarTramiteAction derivarAction;
    private final ResolverTramiteAction resolverAction;
    private final TramiteQueryService queryService;

    public TramiteController(CrearTramiteAction crearAction,
                             DerivarTramiteAction derivarAction,
                             ResolverTramiteAction resolverAction,
                             TramiteQueryService queryService) {
        this.crearAction = crearAction;
        this.derivarAction = derivarAction;
        this.resolverAction = resolverAction;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<TramiteResponse> crear(@RequestBody CrearTramiteRequest request) {
        TramiteResponse response = crearAction.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TramiteResponse>> listar(
            @RequestParam(required = false) EstadoTramite estado,
            @RequestParam(required = false) String departamento) {
        return ResponseEntity.ok(queryService.listar(estado, departamento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TramiteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<TramiteResponse> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(queryService.buscarPorCodigo(codigo));
    }

    @PatchMapping("/{id}/derivar")
    public ResponseEntity<TramiteResponse> derivar(
            @PathVariable Long id,
            @RequestBody DerivarTramiteRequest request) {
        return ResponseEntity.ok(derivarAction.execute(id, request));
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<TramiteResponse> resolver(
            @PathVariable Long id,
            @RequestBody ResolverTramiteRequest request) {
        return ResponseEntity.ok(resolverAction.execute(id, request));
    }

    @GetMapping("/dashboard/metricas")
    public ResponseEntity<DashboardMetricasResponse> obtenerMetricasDashboard() {
        return ResponseEntity.ok(queryService.obtenerMetricasDashboard());
    }
}
