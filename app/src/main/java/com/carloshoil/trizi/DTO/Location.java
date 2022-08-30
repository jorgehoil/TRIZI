package com.carloshoil.trizi.DTO;

import androidx.annotation.Keep;

@Keep
public class Location {
    public Double dLatitud;
    public Double dLongitud;

    public Location(Double dLatitud, Double dLongitud) {
        this.dLatitud = dLatitud;
        this.dLongitud = dLongitud;
    }
    public Location()
    {

    }
}
