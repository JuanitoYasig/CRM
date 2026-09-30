package com.facturacion.api_core.modules.citizen.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public final class Direccion implements Serializable {

    @Column(name = "calle_principal", length = 150)
    private String callePrincipal;

    @Column(name = "calle_secundaria", length = 150)
    private String calleSecundaria;

    @Column(name = "numero_predio", length = 30)
    private String numeroPredio;

    @Column(name = "referencia", length = 255)
    private String referencia;

    @Column(name = "parroquia", length = 100)
    private String parroquia;

    protected Direccion() {
    }

    public Direccion(String callePrincipal, String calleSecundaria, String numeroPredio, String referencia, String parroquia) {
        this.callePrincipal = callePrincipal;
        this.calleSecundaria = calleSecundaria;
        this.numeroPredio = numeroPredio;
        this.referencia = referencia;
        this.parroquia = parroquia;
    }

    public String getCallePrincipal() {
        return callePrincipal;
    }

    public String getCalleSecundaria() {
        return calleSecundaria;
    }

    public String getNumeroPredio() {
        return numeroPredio;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getParroquia() {
        return parroquia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direccion direccion = (Direccion) o;
        return Objects.equals(callePrincipal, direccion.callePrincipal) &&
                Objects.equals(calleSecundaria, direccion.calleSecundaria) &&
                Objects.equals(numeroPredio, direccion.numeroPredio) &&
                Objects.equals(referencia, direccion.referencia) &&
                Objects.equals(parroquia, direccion.parroquia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callePrincipal, calleSecundaria, numeroPredio, referencia, parroquia);
    }
}
