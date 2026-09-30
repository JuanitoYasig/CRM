package com.facturacion.api_core.shared.context;

import com.facturacion.api_core.modules.auth.domain.model.UserPrincipal;

/**
 * Contexto de usuario autenticado basado en ThreadLocal.
 * Permite acceder a los datos del funcionario que ejecuta la operación actual.
 */
public final class UserContext {

    private static final ThreadLocal<UserPrincipal> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setCurrentUser(UserPrincipal user) {
        CURRENT_USER.set(user);
    }

    public static UserPrincipal getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
