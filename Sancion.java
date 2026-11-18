public class Sancion {
    private Jugador jugador;
    private String nombreEquipo;
    private String tipoTarjeta; // "Amarilla" o "Roja"
    private int minuto;
    private String motivo;

    public Sancion(Jugador jugador, String nombreEquipo, String tipoTarjeta, int minuto, String motivo) {
        this.jugador = jugador;
        this.nombreEquipo = nombreEquipo;
        this.tipoTarjeta = tipoTarjeta;
        this.minuto = minuto;
        this.motivo = motivo;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public int getMinuto() {
        return minuto;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString() {
        return "Minuto " + minuto + "' - Tarjeta " + tipoTarjeta + " a " + jugador.getNombre() + " " + jugador.getApellido() + " (" + nombreEquipo + ") | Motivo: " + motivo;
    }
}