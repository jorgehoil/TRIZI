package com.carloshoil.trizi.DTO;

import androidx.annotation.Keep;

@Keep
public class ConfigDTO {
    public boolean lBloqueado;
    public boolean lActivo;
    public boolean lGratuito;
    public boolean lConductor;
    public double iNumeroViajes;
    public int iNumeroReportes;
    public float fCalif;
    public float fTotalCalifcadores;
    public float fTotalEstrellas;

    public ConfigDTO(boolean lBloqueado, boolean lActivo, boolean lGratuito, boolean lConductor, double iNumeroViajes, int iNumeroReportes, float fCalif,float fTotalCalifcadores,float fTotalEstrellas) {
        this.lBloqueado = lBloqueado;
        this.lActivo = lActivo;
        this.lGratuito = lGratuito;
        this.lConductor = lConductor;
        this.iNumeroViajes = iNumeroViajes;
        this.iNumeroReportes = iNumeroReportes;
        this.fCalif = fCalif;
        this.fTotalCalifcadores=fTotalCalifcadores;
        this.fTotalEstrellas=fTotalEstrellas;

    }
    public ConfigDTO()
    {

    }
}
