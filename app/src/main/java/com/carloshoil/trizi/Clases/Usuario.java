package com.carloshoil.trizi.Clases;

import androidx.annotation.Keep;

import com.carloshoil.trizi.DTO.ConfigDTO;
import com.carloshoil.trizi.DTO.DatosPagoDTO;

@Keep
public class Usuario {
    public String cNombres;
    public String cApellidos;
    public String cNombreCompleto;
    public String cDireccion;
    public int iTipoSexo;
    public int iEdad;
    public String cRutaFoto;
    public ConfigDTO configDTO;
    public DatosPagoDTO datosPagoDTO;


    public Usuario(String cNombres, String cApellidos, String cNombreCompleto, String cDireccion, int iTipoSexo, int iEdad, String cRutaFoto, ConfigDTO configDTO, DatosPagoDTO datosPagoDTO) {
        this.cNombres = cNombres;
        this.cApellidos = cApellidos;
        this.cNombreCompleto = cNombreCompleto;
        this.cDireccion = cDireccion;
        this.iTipoSexo = iTipoSexo;
        this.iEdad = iEdad;
        this.cRutaFoto = cRutaFoto;
        this.configDTO=configDTO;
        this.datosPagoDTO=datosPagoDTO;
    }

    public Usuario() {

    }
}
