package com.carloshoil.trizi.DTO;

import androidx.annotation.Keep;

@Keep
public class DatosPagoDTO {
    public boolean lPagado;
    public String cUltimoPago;
    public String cFechaVencimiento;


    public DatosPagoDTO(boolean lPagado, String cUltimoPago, String cFechaVencimiento) {
        this.lPagado = lPagado;
        this.cUltimoPago = cUltimoPago;
        this.cFechaVencimiento = cFechaVencimiento;
    }
    public DatosPagoDTO()
    {

    }
}
