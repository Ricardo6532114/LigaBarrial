//Ricardo Quelal
//Ricardo Quelal
public class Jugador extends Persona {
    private boolean esCapitan;

    // --- ATRIBUTOS AÑADIDOS PARA EL MÓDULO 3 (SIN MODIFICAR LO DEMÁS) ---
    private int amarillasPartido = 0;
    private boolean expulsado = false;
    private int partidosSuspension = 0;

    public Jugador(String nombre, String apellido, int edad, String cedula, String telefono, boolean esCapitan) {
        super(nombre, apellido, edad, cedula, telefono);
        this.esCapitan = esCapitan;
    }

    public boolean puedeJugar() {
        return partidosSuspension == 0 && !expulsado;
    }

    public void cumplirFechaSuspension() {
        if (partidosSuspension > 0) {
            partidosSuspension--;
        }
        this.amarillasPartido = 0;
        this.expulsado = false;
    }

    public boolean isEsCapitan() {
        return esCapitan;
    }

    public void setEsCapitan(boolean esCapitan) {
        this.esCapitan = esCapitan;
    }

    // --- GETTERS Y SETTERS AÑADIDOS PARA EL MÓDULO 3 ---
    public int getAmarillasPartido() {
        return amarillasPartido;
    }

    public void setAmarillasPartido(int amarillasPartido) {
        this.amarillasPartido = amarillasPartido;
    }

    public boolean isExpulsado() {
        return expulsado;
    }

    public void setExpulsado(boolean expulsado) {
        this.expulsado = expulsado;
    }

    public int getPartidosSuspension() {
        return partidosSuspension;
    }

    public void setPartidosSuspension(int partidosSuspension) {
        this.partidosSuspension = partidosSuspension;
    }

    @Override
    public String toString() {
        String estado = partidosSuspension > 0 ? " [SUSPENDIDO]" : " [Habilitado]";
        return super.toString() + (esCapitan ? " [CAPITÁN / DELEGADO]" : "") + estado;
    }
}