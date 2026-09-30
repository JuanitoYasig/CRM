package com.facturacion.api_core.modules.auth.infrastructure.web;

import com.facturacion.api_core.modules.auth.actions.AutenticarUsuarioAction;
import com.facturacion.api_core.modules.auth.actions.RegistrarUsuarioAction;
import com.facturacion.api_core.modules.auth.domain.model.UserPrincipal;
import com.facturacion.api_core.modules.auth.dto.LoginRequest;
import com.facturacion.api_core.modules.auth.dto.LoginResponse;
import com.facturacion.api_core.modules.auth.dto.RegistrarUsuarioRequest;
import com.facturacion.api_core.modules.auth.dto.UsuarioResponse;
import com.facturacion.api_core.modules.auth.services.UsuarioQueryService;
import com.facturacion.api_core.shared.context.UserContext;
import com.facturacion.api_core.shared.exception.TenantAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para autenticación institucional, control de sesiones y gestión de funcionarios.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AutenticarUsuarioAction loginAction;
    private final RegistrarUsuarioAction registerAction;
    private final UsuarioQueryService queryService;

    public AuthController(AutenticarUsuarioAction loginAction,
                          RegistrarUsuarioAction registerAction,
                          UsuarioQueryService queryService) {
        this.loginAction = loginAction;
        this.registerAction = registerAction;
        this.queryService = queryService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginAction.execute(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody RegistrarUsuarioRequest request) {
        UsuarioResponse response = registerAction.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserPrincipal> me() {
        UserPrincipal currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            throw new TenantAccessDeniedException("No se ha detectado una sesión institucional activa");
        }
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(queryService.listarTodos());
    }
}
