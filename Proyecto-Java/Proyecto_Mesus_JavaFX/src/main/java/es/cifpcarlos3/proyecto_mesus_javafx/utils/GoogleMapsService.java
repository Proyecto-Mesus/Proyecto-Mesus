package es.cifpcarlos3.proyecto_mesus_javafx.utils;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class GoogleMapsService {

    private final GeoApiContext context;

    public GoogleMapsService() {
        this.context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCOmBjf8urXeoViTBHrKiUPDRe_CRdu2EE")
                .build();
    }

    public String obtenerDireccion(double lat, double lng) throws Exception {
        LatLng coordenadas = new LatLng(lat, lng);

        GeocodingResult[] results = GeocodingApi.reverseGeocode(context, coordenadas).await();

        if (results != null && results.length > 0) {
            return results[0].formattedAddress;
        } else {
            return "Dirección no encontrada";
        }
    }
}
