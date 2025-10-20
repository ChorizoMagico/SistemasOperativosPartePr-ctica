public class Proceso {
    private String etiqueta;
    private int burstTime;
    private int arrivalTime;
    private int queue;
    private int prioridad;

    // Atributos resultados
    private int waitingTime;
    private int completionTime;
    private int responseTime;
    private int turnaroundTime;

    public Proceso(String etiqueta, int burstTime, int arrivalTime, int queue, int prioridad) {
        this.etiqueta = etiqueta;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.queue = queue;
        this.prioridad = prioridad;
    }

    // Getters y setters
    public String getEtiqueta() { return etiqueta; }
    public int getBurstTime() { return burstTime; }
    public int getArrivalTime() { return arrivalTime; }
    public int getQueue() { return queue; }
    public int getPrioridad() { return prioridad; }

    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }

    public int getCompletionTime() { return completionTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }

    public int getResponseTime() { return responseTime; }
    public void setResponseTime(int responseTime) { this.responseTime = responseTime; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }

    @Override
    public String toString() {
        return etiqueta + ";" + burstTime + ";" + arrivalTime + ";" + queue + ";" + prioridad +
                ";" + waitingTime + ";" + completionTime + ";" + responseTime + ";" + turnaroundTime;
    }
}
