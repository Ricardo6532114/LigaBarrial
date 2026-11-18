import java.util.ArrayList;
import java.util.List;

public class ActaPartido {
    private Partido partido;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private int minimoJugadores;
    private boolean partidoIniciado;
    private boolean partidoFinalizado;
    private boolean partidoSuspendido;
    private String motivoSuspension;

    private List<Gol> goles = new ArrayList<>();
    private List<Sancion> sanciones = new ArrayList<>();

    public ActaPartido(Partido partido, Equipo equipoLocal, Equipo equipoVisitante, int minimoJugadores) {
        this.partido = partido;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.minimoJugadores = minimoJugadores; // Aquí se recibe el mínimo (ej. 6)
        this.partidoIniciado = false;
        this.partidoFinalizado = false;
        this.partidoSuspendido = false;
        this.motivoSuspension = "";
    }

    public int contarHabilitados(Equipo eq) {
        int cont = 0;
        for (Jugador j : eq.getJugadores()) {
            if (j.puedeJugar()) {
                cont++;
            }
        }
        return cont;
    }

    public boolean iniciarPartido() {
        int habLocal = contarHabilitados(equipoLocal);
        int habVisitante = contarHabilitados(equipoVisitante);

        // Validación estricta del mínimo de jugadores en cancha (6)
        if (habLocal < minimoJugadores || habVisitante < minimoJugadores) {
            this.partidoSuspendido = true;
            this.motivoSuspension = "No cumple el mínimo de " + minimoJugadores + " jugadores habilitados en cancha.";
            partido.setEstado("Suspendido por falta de jugadores");
            return false;
        }
        this.partidoIniciado = true;
        partido.setEstado("En Juego");
        return true;
    }

    public void registrarGol(Equipo equipo, Jugador jugador, int minuto) {
        if (!partidoIniciado || partidoFinalizado || partidoSuspendido) {
            System.out.println("Error: El partido no está en juego.");
            return;
        }
        if (!jugador.puedeJugar()) {
            System.out.println("Error: El jugador está suspendido o expulsado y no puede anotar.");
            return;
        }
        goles.add(new Gol(jugador, equipo.getNombreEquipo(), minuto));
        System.out.println("Gol registrado en el minuto " + minuto + "'.");
    }

    public void registrarSancion(Equipo equipo, Jugador jugador, String tipoTarjeta, int minuto, String motivo) {
        if (!partidoIniciado || partidoFinalizado || partidoSuspendido) {
            System.out.println("Error: El partido no está en juego.");
            return;
        }
        if (jugador.isExpulsado() || jugador.getPartidosSuspension() > 0) {
            System.out.println("Error: El jugador ya está expulsado o suspendido.");
            return;
        }

        if (tipoTarjeta.equalsIgnoreCase("Amarilla")) {
            int amarillas = jugador.getAmarillasPartido() + 1;
            jugador.setAmarillasPartido(amarillas);
            sanciones.add(new Sancion(jugador, equipo.getNombreEquipo(), "Amarilla", minuto, motivo));

            // RF7: Dos amarillas = Roja automática, expulsión y 1 partido de suspensión
            if (amarillas == 2) {
                jugador.setExpulsado(true);
                jugador.setPartidosSuspension(1);
                sanciones.add(new Sancion(jugador, equipo.getNombreEquipo(), "Roja (Doble Amarilla)", minuto, "Acumulación de 2 amarillas"));
                System.out.println("ALERTA RF7: " + jugador.getNombre() + " acumuló 2 amarillas -> ROJA y 1 partido de suspensión.");
            }
        } else if (tipoTarjeta.equalsIgnoreCase("Roja")) {
            jugador.setExpulsado(true);
            jugador.setPartidosSuspension(1);
            sanciones.add(new Sancion(jugador, equipo.getNombreEquipo(), "Roja", minuto, motivo));
            System.out.println("ALERTA RF7: " + jugador.getNombre() + " recibió ROJA directa -> 1 partido de suspensión.");
        }

        // Si por expulsiones el equipo queda por debajo del mínimo reglamentario (6)
        if (contarHabilitados(equipo) < minimoJugadores) {
            this.partidoSuspendido = true;
            this.motivoSuspension = "El equipo " + equipo.getNombreEquipo() + " se quedó con menos de " + minimoJugadores + " jugadores en cancha.";
            partido.setEstado("Suspendido por inferioridad numérica");
        }
    }

    public void finalizarPartido() {
        if (partidoIniciado && !partidoSuspendido) {
            this.partidoFinalizado = true;
            int gLocal = calcularGolesEquipo(equipoLocal.getNombreEquipo());
            int gVis = calcularGolesEquipo(equipoVisitante.getNombreEquipo());
            partido.setEstado("Finalizado (" + gLocal + " - " + gVis + ")");
        }
    }

    public int calcularGolesEquipo(String nombreEquipo) {
        int total = 0;
        for (Gol g : goles) {
            if (g.getNombreEquipo().equalsIgnoreCase(nombreEquipo)) total++;
        }
        return total;
    }

    public Partido getPartido() { return partido; }
    public Equipo getEquipoLocal() { return equipoLocal; }
    public Equipo getEquipoVisitante() { return equipoVisitante; }

    public String generarTextoActa() {
        StringBuilder sb = new StringBuilder();
        sb.append("================ ACTA OFICIAL DEL PARTIDO ================\n");
        sb.append("Encuentro: ").append(equipoLocal.getNombreEquipo()).append(" vs ").append(equipoVisitante.getNombreEquipo()).append("\n");
        sb.append("Mínimo requerido en cancha: ").append(minimoJugadores).append(" jugadores\n");
        if (partidoFinalizado) {
            int gLocal = calcularGolesEquipo(equipoLocal.getNombreEquipo());
            int gVis = calcularGolesEquipo(equipoVisitante.getNombreEquipo());
            sb.append("RESULTADO FINAL: ").append(equipoLocal.getNombreEquipo()).append(" [").append(gLocal).append("] - [").append(gVis).append("] ").append(equipoVisitante.getNombreEquipo()).append("\n");
        } else {
            sb.append("ESTADO: ").append(partido.getEstado()).append("\n");
        }
        sb.append("\n--- GOLES ---\n");
        for (Gol g : goles) sb.append(g).append("\n");
        sb.append("\n--- SANCIONES ---\n");
        for (Sancion s : sanciones) sb.append(s).append("\n");
        sb.append("==========================================================\n");
        return sb.toString();
    }
}