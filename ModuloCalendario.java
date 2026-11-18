//Carlos Raul Morales Escudero
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ModuloCalendario {
    private List<Partido> partidos;

    public ModuloCalendario() {
        this.partidos = new ArrayList<>();
    }

    // Calendario
    public void generarCalendario(List<String> equipos, String cancha, int horaInicio) {
        partidos.clear();
        if (equipos.size() < 2) {
            System.out.println("Error: Se necesitan al menos 2 equipos registrados para generar el calendario");
            return;
        }
        int horaActual = horaInicio;
        System.out.println("\n   Programando Partido   \n");
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                String local = equipos.get(i);
                String visitante = equipos.get(j);
                String fechaDefault = "22-11-2026"; // solo un ejemplo
                Partido nuevoPartido = new Partido(local, visitante, cancha, fechaDefault, horaActual);
                partidos.add(nuevoPartido);
                System.out.println(" 1 VS 1 " + nuevoPartido);
                horaActual++; // Incrementa la hora para evitar cruces en la misma cancha
            }
        }
        System.out.println("\n El calendario de los partidos fue generado con éxito sin cruces\n");
    }

    // Listar partidos actuales
    public void consultarCalendario() {
        System.out.println("\n   Próximos Encuentros   ");
        if (partidos.isEmpty()) {
            System.out.println("  Por el momento no hay partidos registrados  ");
            return;
        }
        for (int i = 0; i < partidos.size(); i++) {
            System.out.println((i + 1) + ". " + partidos.get(i));
        }
        System.out.println("\n    Fin de la Lista  \n");
    }

    // Reprogramar
    public void reprogramarPartido(Scanner scanner) {
        consultarCalendario();
        if (partidos.isEmpty()) return;
        System.out.print("Seleccione el número de partido a reprogramar: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= partidos.size()) {
            System.out.println("Número de partido inválido.");
            return;
        }
        Partido p = partidos.get(index);
        System.out.print("Ingrese la nueva fecha (Día-Mes-Año): ");
        String nuevaFecha = scanner.nextLine();
        System.out.print("Ingrese la nueva hora (ej. 15 o 17 sin :00): ");
        int nuevaHora = Integer.parseInt(scanner.nextLine());
        boolean hayCruce = false;
        for (Partido otro : partidos) {
            if (otro != p && otro.getCancha().equalsIgnoreCase(p.getCancha()) &&
                    otro.getFecha().equals(nuevaFecha) && otro.getHora() == nuevaHora) {
                hayCruce = true;
                break;
            }
        }
        if (hayCruce) {
            System.out.println("Error: La cancha ya está ocupada en esa fecha y hora, Se recomienda asignar un horario valido para reprogramar el partido");
        } else {
            p.setFecha(nuevaFecha);
            p.setHora(nuevaHora);
            p.setEstado("Partido Reprogramado");
            System.out.println("El partido fue reprogramado con éxito y se muestra en el calendario");
        }
    }

    public void gestionarIncomparecencia(Scanner scanner) {
        consultarCalendario();
        if (partidos.isEmpty()) return;
        System.out.print("Seleccione el número de partido para registrar asistencia: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= partidos.size()) {
            System.out.println("Partido inválido.");
            return;
        }
        Partido p = partidos.get(index);
        System.out.println("Partido seleccionado: " + p.getLocal() + " -vs- " + p.getVisitante());
        System.out.println("1. Ambos equipos se presentaron completos");
        System.out.println("2. El equipo Local no completó el mínimo (Pierde por falta de jugadores).");
        System.out.println("3. El equipo Visita no completó el mínimo (Pierde por falta de jugadores).");
        System.out.println("4. Ninguno se presentó (Ambos pierden).");
        System.out.print("Elija una opción: ");
        int op = Integer.parseInt(scanner.nextLine());
        switch (op) {
            case 1:
                p.setEstado("Jugador con Normalidad");
                System.out.println("Asistencia de ambos equipos... el partido procede con normalidad");
                break;
            case 2:
                p.setEstado("Perdido: " + p.getLocal() + " (No se presentó el equipo)");
                System.out.println("El equipo " + p.getLocal() + " pierde por no presentarse, Gana el equipo Contrario");
                break;
            case 3:
                p.setEstado("Perdido " + p.getVisitante() + " (No se presentó el equipo)");
                System.out.println("El equipo " + p.getVisitante() + " pierde por no presentarse, Gana el equipo Contrario");
                break;
            case 4:
                p.setEstado("Ningun Equipo se presento");
                System.out.println("Ningún equipo se presentó. (Empate Ningun equipo gano)");
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    // Getter agregado para conectar con el Módulo 3 (Actas)
    public List<Partido> getPartidos() {
        return partidos;
    }
}