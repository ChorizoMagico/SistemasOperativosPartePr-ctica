import java.util.*;
import java.io.*;

public class MLQScheduler {
    private List<Proceso> procesos;
    private List<Proceso> cola1; // RR(1)
    private List<Proceso> cola2; // RR(3)
    private List<Proceso> cola3; // SJF

    /*
    Constructor de la clase Scheduler
     */
    public MLQScheduler() {
        procesos = new ArrayList<>();
        cola1 = new ArrayList<>();
        cola2 = new ArrayList<>();
        cola3 = new ArrayList<>();
    }

    /*
    Carga una lista de procesos desde un archivo de texto o archivo plano. Ignora las líneas que empiezan con
    # o están vacías, y cada línea válida tiene los atributos de un proceso separado por punto y coma
 *
     */
    public void cargarProcesos(String ruta) throws IOException {
        System.out.println("Buscando archivo en: " + new java.io.File(".").getAbsolutePath());

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("#") || linea.trim().isEmpty()) continue;

                String[] datos = linea.split(";");
                String etiqueta = datos[0].trim();
                int bt = Integer.parseInt(datos[1].trim());
                int at = Integer.parseInt(datos[2].trim());
                int q = Integer.parseInt(datos[3].trim());
                int pr = Integer.parseInt(datos[4].trim());
                Proceso p = new Proceso(etiqueta, bt, at, q, pr);
                procesos.add(p);
            }
        }

        // Distribuir procesos según su cola
        for (Proceso p : procesos) {
            switch (p.getQueue()) {
                case 1 -> cola1.add(p);
                case 2 -> cola2.add(p);
                case 3 -> cola3.add(p);
            }
        }
    }

    /*
   Ejecuta las tres colas del schedular, cada una llamando al método correspondiente que ejecuta cada algoritmo
     */
    public void ejecutar() {
        int tiempoActual = 0;

        // RR(1)
        tiempoActual = ejecutarRoundRobin(cola1, 1, tiempoActual);

        // RR(3)
        tiempoActual = ejecutarRoundRobin(cola2, 3, tiempoActual);

        // SJF
        tiempoActual = ejecutarSJF(cola3, tiempoActual);
    }

    /*
     RR(1), el cual consiste en una cola (queue) que va ejecutando los burst time de cada proceso, el resultado los guarda en un diccionario
     con los tiempos restantes, y si el tiempo restante es mayor a 0 vuelve a colocar el proceso en la cola para que se ejecute de nuevo
     */
    private int ejecutarRoundRobin(List<Proceso> cola, int quantum, int tiempoActual) {
        if (cola.isEmpty()) return tiempoActual;

        // Orden inicial: primero por tiempo de llegada, luego por etiqueta
        cola.sort(Comparator
                .comparingInt(Proceso::getArrivalTime)
                .thenComparing(Proceso::getEtiqueta));

        // Crea una cola de procesos, lo cual es la abstracción correspondiente a RR
        Queue<Proceso> lista = new LinkedList<>();

        //A cada proceso se le asigna su tiempo restante
        Map<Proceso, Integer> restante = new HashMap<>();

        for (Proceso p : cola) {
            restante.put(p, p.getBurstTime());
        }

        int index = 0;

        // Agregar los procesos a la cola conforme van llegando
        while (index < cola.size() && cola.get(index).getArrivalTime() <= tiempoActual) {
            lista.add(cola.get(index));
            index++;
        }

        //Bucle principal que se realiza mientras no hayan procesos
        while (!lista.isEmpty()) {

            //Saca el primer proceso de la lista
            Proceso p = lista.poll();

            //Si se demora menos que el quantum, ejecutará lo que se demore. Sino, ejecuta el quantum
            int tiempo = Math.min(restante.get(p), quantum);

            // Si es la primera vez que se ejecuta, guarda el responde time
            if (restante.get(p) == p.getBurstTime()) {
                p.setResponseTime(tiempoActual);
            }

            //Ejecuta el proceso
            tiempoActual += tiempo;

            //Reasigna el tiempo restante
            restante.put(p, restante.get(p) - tiempo);


            // Agregar nuevos procesos que hayan llegado mientras tanto
            while (index < cola.size() && cola.get(index).getArrivalTime() <= tiempoActual) {
                lista.add(cola.get(index));
                index++;
            }

            if (restante.get(p) > 0) {
                lista.add(p); // vuelve al final
            } else {
                // Proceso completado
                p.setCompletionTime(tiempoActual);
                p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
                p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
            }
        }

        return tiempoActual;
    }

    // SJF
    private int ejecutarSJF(List<Proceso> cola, int tiempoActual) {
        //Caso en donde la cola no tenga procesos
        if (cola.isEmpty()) return tiempoActual;

        // Orden inicial por llegada y etiqueta
        cola.sort(Comparator
                .comparingInt(Proceso::getArrivalTime)
                .thenComparing(Proceso::getEtiqueta));

        //Lista con los procesos que ya han llegado
        List<Proceso> disponibles = new ArrayList<>();
        int index = 0;
        int n = cola.size();

        boolean primerProcesoSeleccionado = false; // 🔹 para detectar si es el inicio de la cola

        //Bucle principal que se ejecuta mientras hayan procesos pendientes
        while (index < n || !disponibles.isEmpty()) {

            // Agregar procesos que ya hayan llegado
            while (index < n && cola.get(index).getArrivalTime() <= tiempoActual) {
                disponibles.add(cola.get(index));
                index++;
            }

            if (disponibles.isEmpty()) {
                // Caso en el que no hayan procesos disponibles, el scheduler avanza el tiempo hasta que llegue uno
                tiempoActual = cola.get(index).getArrivalTime();
                continue;
            }

            Proceso p;

            if (!primerProcesoSeleccionado) {
                // 🔹 Al inicio de la cola, si hay varios disponibles, elegir por etiqueta
                disponibles.sort(Comparator.comparing(Proceso::getEtiqueta));
                p = disponibles.remove(0);
                primerProcesoSeleccionado = true;
            } else {
                // Escoger el proceso con menor BT (y si empatan, menor etiqueta)
                disponibles.sort(Comparator
                        .comparingInt(Proceso::getBurstTime)
                        .thenComparing(Proceso::getEtiqueta));
                p = disponibles.remove(0);
            }

            p.setResponseTime(tiempoActual);
            tiempoActual += p.getBurstTime();
            p.setCompletionTime(tiempoActual);
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }

        return tiempoActual;
    }

    // --- Guardar salida ---
    public void guardarSalida(String ruta) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {
            pw.println("# etiqueta; BT; AT; Q; Pr; WT; CT; RT; TAT");
            double totalWT = 0, totalCT = 0, totalRT = 0, totalTAT = 0;
            int n = procesos.size();

            for (Proceso p : procesos) {
                pw.println(p);
                totalWT += p.getWaitingTime();
                totalCT += p.getCompletionTime();
                totalRT += p.getResponseTime();
                totalTAT += p.getTurnaroundTime();
            }

            pw.printf("WT=%.2f; CT=%.2f; RT=%.2f; TAT=%.2f;%n",
                    totalWT / n, totalCT / n, totalRT / n, totalTAT / n);
        }
    }
}