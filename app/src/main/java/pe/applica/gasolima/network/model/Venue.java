package pe.applica.gasolima.network.model;

/**
 * Represents each gas station in Retrofit.
 * Created by jhoon on 1/11/16.
 */
public class Venue {
    public GasStation venue;

    public class GasStation {
        public String id;
        public String nombre;
        public String direccion;
        public String lat;
        public String lng;
        public String distancia;
        public String com;
        public String GAS84;
        public String GAS90;
        public String GAS95;
        public String GAS97;
        public String GAS98;
    }
}
