import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ModuloActas {
    private List<ActaPartido> actas = new ArrayList<>();

    private Equipo buscarEquipoPorNombre(List<Equipo> equipos, String nombre) {
        for (Equipo eq : equipos) {
            if (eq.getNombreEquipo().equalsIgnoreCase(nombre)) return eq;
        }
        return null;
    }

    private Jugador buscarJugadorPorCedula(Equipo eq, String cedula) {
        for (Jugador j : eq.getJugadores()) {
            if (j.getCedula().equals(cedula)) return j;
        }
        return null;
    }

    public void iniciarActaDesdeCalendario(Scanner scanner, List<Equipo> equipos, ModuloCalendario calendario) {
        List<Partido> partidos = calendario.getPartidos();
        if (partidos.isEmpty()) {
            System.out.println("Error: Primero debe generar el calendario en la opción 2.");
            return;
        }

        calendario.consultarCalendario();
        System.out.print("Seleccione el número de partido para abrir el Acta: ");
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        if (idx < 0 || idx >= partidos.size()) {
            System.out.println("Número de partido inválido.");
            return;
        }

        Partido p = partidos.get(idx);
        Equipo local = buscarEquipoPorNombre(equipos, p.getLocal());
        Equipo visitante = buscarEquipoPorNombre(equipos, p.getVisitante());

        if (local == null || visitante == null) {
            System.out.println("Error: No se encontraron los equipos inscritos.");
            return;
        }

        System.out.print("Ingrese el número mínimo de jugadores requeridos por equipo: ");
        int minJug = Integer.parseInt(scanner.nextLine());

        ActaPartido acta = new ActaPartido(p, local, visitante, minJug);
        if (acta.iniciarPartido()) {
            System.out.println("Acta abierta y partido iniciado correctamente.");
        } else {
            System.out.println("El partido fue SUSPENDIDO por no cumplir el mínimo de jugadores habilitados.");
        }
        actas.add(acta);
    }

    public void registrarGolEnActa(Scanner scanner) {
        ActaPartido acta = seleccionarActa(scanner);
        if (acta == null) return;

        System.out.println("1. " + acta.getEquipoLocal().getNombreEquipo());
        System.out.println("2. " + acta.getEquipoVisitante().getNombreEquipo());
        System.out.print("Seleccione el equipo que anotó: ");
        int op = Integer.parseInt(scanner.nextLine());
        Equipo eq = (op == 1) ? acta.getEquipoLocal() : acta.getEquipoVisitante();

        mostrarJugadoresEquipo(eq);
        System.out.print("Ingrese la cédula del jugador que anotó: ");
        String cedula = scanner.nextLine();
        Jugador j = buscarJugadorPorCedula(eq, cedula);

        if (j == null) {
            System.out.println("Jugador no encontrado en ese equipo.");
            return;
        }

        System.out.print("Ingrese el minuto del gol: ");
        int minuto = Integer.parseInt(scanner.nextLine());
        acta.registrarGol(eq, j, minuto);
    }

    public void registrarSancionEnActa(Scanner scanner) {
        ActaPartido acta = seleccionarActa(scanner);
        if (acta == null) return;

        System.out.println("1. " + acta.getEquipoLocal().getNombreEquipo());
        System.out.println("2. " + acta.getEquipoVisitante().getNombreEquipo());
        System.out.print("Seleccione el equipo del jugador sancionado: ");
        int op = Integer.parseInt(scanner.nextLine());
        Equipo eq = (op == 1) ? acta.getEquipoLocal() : acta.getEquipoVisitante();

        mostrarJugadoresEquipo(eq);
        System.out.print("Ingrese la cédula del jugador sancionado: ");
        String cedula = scanner.nextLine();
        Jugador j = buscarJugadorPorCedula(eq, cedula);

        if (j == null) {
            System.out.println("Jugador no encontrado en ese equipo.");
            return;
        }

        System.out.print("Tipo de tarjeta (1. Amarilla / 2. Roja): ");
        int tipo = Integer.parseInt(scanner.nextLine());
        String tarjeta = (tipo == 1) ? "Amarilla" : "Roja";

        System.out.print("Minuto en que se cometió la falta: ");
        int minuto = Integer.parseInt(scanner.nextLine());

        System.out.print("Motivo de la falta: ");
        String motivo = scanner.nextLine();

        acta.registrarSancion(eq, j, tarjeta, minuto, motivo);
    }

    public void finalizarActa(Scanner scanner) {
        ActaPartido acta = seleccionarActa(scanner);
        if (acta == null) return;
        acta.finalizarPartido();
        System.out.println(acta.generarTextoActa());
    }

    public void consultarActas() {
        if (actas.isEmpty()) {
            System.out.println("No hay actas registradas por el momento.");
            return;
        }
        for (ActaPartido acta : actas) {
            System.out.println(acta.generarTextoActa());
        }
    }

    public void habilitarSiguienteFecha(List<Equipo> equipos) {
        for (Equipo eq : equipos) {
            for (Jugador j : eq.getJugadores()) {
                j.cumplirFechaSuspension();
            }
        }
        System.out.println("Se ha descontado 1 partido de suspensión a los jugadores sancionados.");
    }

    private ActaPartido seleccionarActa(Scanner scanner) {
        if (actas.isEmpty()) {
            System.out.println("No hay actas iniciadas. Inicie una primero.");
            return null;
        }
        for (int i = 0; i < actas.size(); i++) {
            Partido p = actas.get(i).getPartido();
            System.out.println((i + 1) + ". " + p.getLocal() + " vs " + p.getVisitante() + " [" + p.getEstado() + "]");
        }
        System.out.print("Seleccione el número de acta: ");
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        if (idx < 0 || idx >= actas.size()) {
            System.out.println("Selección inválida.");
            return null;
        }
        return actas.get(idx);
    }

    private void mostrarJugadoresEquipo(Equipo eq) {
        System.out.println("Jugadores de " + eq.getNombreEquipo() + ":");
        for (Jugador j : eq.getJugadores()) {
            System.out.println(" - Cédula: " + j.getCedula() + " | " + j.getNombre() + " " + j.getApellido() +
                    (j.puedeJugar() ? " [Habilitado]" : " [NO HABILITADO / SUSPENDIDO]"));
        }
    }
}