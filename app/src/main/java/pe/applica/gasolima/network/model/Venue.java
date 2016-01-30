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
        public String generateGasesString() {
            String result = "";
            if (!GAS84.equals("-1")) {
                result += "G84, ";
            }
            if (!GAS90.equals("-1")) {
                result += "G90, ";
            }
            if (!GAS95.equals("-1")) {
                result += "G95, ";
            }
            if (!GAS97.equals("-1")) {
                result += "G97, ";
            }
            if (!GAS98.equals("-1")) {
                result += "G98, ";
            }
            if (result.length() > 0) {
                result = result.substring(0, result.length()-2);
            }
            return result;
        }
    }
}
