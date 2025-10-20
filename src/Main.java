public class Main {
    public static void main(String[] args) {
        MLQScheduler mlq = new MLQScheduler();
        try {
            mlq.cargarProcesos("mlq003.txt");
            mlq.ejecutar();
            mlq.guardarSalida("salida_mlq003.txt");
            System.out.println("Ejecución completada. Revisa el archivo salida_mlq001.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}