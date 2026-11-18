//Ricardo Quelal
import java.util.ArrayList;
import java.util.List;

public class Equipo {

    private String nombreEquipo;
    private String categoria;
    private Jugador capitan;
    private List<Jugador> jugadores = new ArrayList<>();

    public Equipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
        this.categoria = "Sin asignar";
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Jugador getCapitan() {
        return capitan;
    }

    public void setCapitan(Jugador capitan) {
        this.capitan = capitan;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public String toString() {
        String info = "\nEQUIPO: " + nombreEquipo + " | Categoría: " + categoria + " \n";
        if (capitan != null) {
            info += "\n Capitán/Delegado: " + capitan.getNombre() + " " + capitan.getApellido() + " (Tel: " + capitan.getTelefono() + ")";
        } else {
            info += "Capitán/Delegado: Sin asignar";
        }
        info += "Plantilla (" + jugadores.size() + " jugadores):";
        if (jugadores.isEmpty()) {
            info += "  (Aún no tiene jugadores inscritos)";
        } else {
            for (Jugador j : jugadores) {
                info += "  - " + j.toString() + "\n";
            }
        }
        return info;
    }
}