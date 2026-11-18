//Carlos Raul Morales Escudero
public class Partido {
    private String local;
    private String visitante;
    private String cancha;
    private String fecha;
    private int hora;
    private String estado; // "PROGRAMADO", "REPROGRAMADO", "PERDIDO_NO_PRESENTACION"

    public Partido(String local, String visitante, String cancha, String fecha, int hora) {
        this.local = local;
        this.visitante = visitante;
        this.cancha = cancha;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "Pendiente";
    }

    public String getLocal() { return local; }
    public String getVisitante() { return visitante; }
    public String getCancha() { return cancha; }
    public String getFecha() { return fecha; }
    public int getHora() { return hora; }
    public String getEstado() { return estado; }

    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setHora(int hora) { this.hora = hora; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "[" + estado + "] " + local + " -vs- " + visitante + " | Cancha: " + cancha + " | Fecha: " + fecha + " | Hora: " + hora + ":00";
    }
}