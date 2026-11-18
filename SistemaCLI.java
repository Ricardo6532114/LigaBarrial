//Ricardo Quelal
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SistemaCLI {

    List<Persona> personasRegistradas = new ArrayList<>();
    List<Equipo> equipos = new ArrayList<>();
    Scanner sc;
    BufferedReader br;

    public SistemaCLI() {
        sc = new Scanner(System.in);
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    //Validar

    public boolean validarSoloLetras(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            boolean esMayuscula = (c >= 'A' && c <= 'Z') || c == 'Ñ';
            boolean esMinuscula = (c >= 'a' && c <= 'z') || c == 'ñ';
            boolean esEspacio = (c == ' ');
            if (!esMayuscula && !esMinuscula && !esEspacio) {
                return false;
            }
        }
        return true;
    }

    public boolean validarDiezDigitos(String dato) {
        if (dato == null || dato.length() != 10) {
            return false;
        }
        for (int i = 0; i < dato.length(); i++) {
            char c = dato.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    //Funcion

    public void crearPersona() {
        String nombre = "";
        String apellido = "";
        int edad = -1;
        String cedula = "";
        String telefono = "";

        try {
            System.out.println("\n Registrar persona \n");
            boolean nombreValido;
            do {
                nombreValido = true;
                System.out.print("Nombre (Solo Nombre no se admite números): ");
                nombre = br.readLine();
                if (!validarSoloLetras(nombre)) {
                    System.out.println("Error: El nombre solo debe contener letras.");
                    nombreValido = false;
                }
            } while (!nombreValido);

            do {
                System.out.print("Apellido (Solo Apellido no se admite números): ");
                apellido = br.readLine();
                if (!validarSoloLetras(apellido)) {
                    System.out.println("Error: El apellido solo debe contener letras.");
                }
            } while (!validarSoloLetras(apellido));

            for (Persona p : personasRegistradas) {
                if (p.getNombre().equalsIgnoreCase(nombre.trim()) && p.getApellido().equalsIgnoreCase(apellido.trim())) {
                    System.out.println("Error: Ya existe una persona registrada con este mismo nombre y apellido.");
                    return;
                }
            }

            do {
                System.out.print("Edad (14 a 60 años): ");
                edad = sc.nextInt();
                if (edad < 14 || edad > 60) {
                    System.out.println("Error: La edad debe estar entre 14 y 60 años.");
                }
            } while (edad < 14 || edad > 60);

            boolean cedulaValida;
            do {
                cedulaValida = true;
                System.out.print("Cédula (10 dígitos): ");
                cedula = sc.next();
                if (!validarDiezDigitos(cedula)) {
                    System.out.println("Error: La cédula debe tener exactamente 10 números positivos.");
                    cedulaValida = false;
                } else {
                    for (Persona existente : personasRegistradas) {
                        if (existente.getCedula().equals(cedula)) {
                            System.out.println("Error: Ya existe una persona registrada con esa cédula.");
                            cedulaValida = false;
                            break;
                        }
                    }
                }
            } while (!cedulaValida);

            boolean telefonoValido;
            do {
                telefonoValido = true;
                System.out.print("Teléfono (10 dígitos): ");
                telefono = sc.next();
                if (!validarDiezDigitos(telefono)) {
                    System.out.println("Error: El teléfono debe tener exactamente 10 números positivos.");
                    telefonoValido = false;
                } else {
                    for (Persona existente : personasRegistradas) {
                        if (existente.getTelefono().equals(telefono)) {
                            System.out.println("Error: Este número de teléfono ya está registrado.");
                            telefonoValido = false;
                            break;
                        }
                    }
                }
            } while (!telefonoValido);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Persona p = new Persona(nombre, apellido, edad, cedula, telefono);
        personasRegistradas.add(p);
        System.out.println("Persona registrada correctamente! Volviendo al menú....");
    }

    public void crearEquipo() {
        try {
            System.out.println("\n Crear Nuevo Equipo \n");
            System.out.print("Ingrese el nombre del equipo: ");
            String nombreEquipo = br.readLine();

            for (Equipo eq : equipos) {
                if (eq.getNombreEquipo().equalsIgnoreCase(nombreEquipo)) {
                    System.out.println("Error: Ya existe un equipo con ese nombre.");
                    return;
                }
            }

            Equipo nuevo = new Equipo(nombreEquipo);
            equipos.add(nuevo);
            System.out.println("El equipo '" + nombreEquipo + "' esta creado. Volviendo al menú....");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean jugadorYaInscritoEnCategoria(String cedula, String categoria) {
        for (Equipo eq : equipos) {
            if (eq.getCategoria().equalsIgnoreCase(categoria)) {
                for (Jugador j : eq.getJugadores()) {
                    if (j.getCedula().equals(cedula)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Validar si la edad del jugador con su categoria
    public boolean validarEdadParaCategoria(int edad, String categoria) {
        if (categoria.equals("Juvenil (Sub-18)")) {
            return edad >= 14 && edad < 18;
        } else if (categoria.equals("Abierta / Senior") || categoria.equals("Senior")) {
            return edad >= 18 && edad <= 39;
        } else if (categoria.equals("Máster (+40)")) {
            return edad >= 40;
        }
        return false;
    }

    public void inscribirJugadoresEnEquipo() {
        if (equipos.isEmpty()) {
            System.out.println("Primero debe crear al menos un equipo.");
            return;
        }

        System.out.println("\n Inscripcion de jugadores para el Equipo \n");

        // 1. Seleccionar el equipo
        System.out.println("Seleccione el equipo a inscribir:");
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println((i + 1) + ". " + equipos.get(i).getNombreEquipo()
                    + " (Jugadores actuales: " + equipos.get(i).getJugadores().size() + ")");
        }
        System.out.println("0. Volver al menú principal");

        System.out.print("Opción: ");
        int opEquipo = sc.nextInt();
        if (opEquipo <= 0 || opEquipo > equipos.size()) {
            System.out.println("Volviendo al menú principal...");
            return;
        }

        Equipo equipoSeleccionado = equipos.get(opEquipo - 1);

        if (!equipoSeleccionado.getJugadores().isEmpty()) {
            System.out.println("Este equipo ya tiene una plantilla inscrita.");
            return;
        }

        // Para la categoria
        System.out.println("\n Seleccione la categoria para el equipo '" + equipoSeleccionado.getNombreEquipo() + "': \n");
        System.out.println("1. Juvenil (Sub-18) [Solo menores de 18 años]");
        System.out.println("2. Senior [De 18 a 39 años]");
        System.out.println("3. Máster (+40) [40 años en adelante]");
        System.out.print("Seleccione una opción: ");
        int opCat = sc.nextInt();

        String categoriaEquipo = "";
        if (opCat == 1) {
            categoriaEquipo = "Juvenil (Sub-18)";
        } else if (opCat == 2) {
            categoriaEquipo = "Senior";
        } else if (opCat == 3) {
            categoriaEquipo = "Máster (+40)";
        } else {
            System.out.println("Categoría no válida. Volviendo al menú...");
            return;
        }

        // Cantidad
        System.out.print("¿Cuántos jugadores desea inscribir para este equipo?: ");
        int cantJugadores = sc.nextInt();
        if (cantJugadores <= 0) {
            System.out.println("Cantidad no válida. Volviendo al menú...");
            return;
        }

        List<Jugador> listaTemporal = new ArrayList<>();


        for (int i = 0; i < cantJugadores; i++) {
            System.out.println("Jugador #" + (i + 1) + " de " + cantJugadores + " (Categoría requerida: " + categoriaEquipo + ") ");
            System.out.println("1. Buscar persona ya registrada por cédula");
            System.out.println("2. Registrar nueva persona");
            System.out.println("3. Cancelar y volver al menú principal");
            System.out.print("Seleccione una opción: ");
            int accion = sc.nextInt();

            if (accion == 3) {
                System.out.println("Inscripción cancelada. Volviendo al menú.");
                return;
            }

            Persona p = null;
            if (accion == 1) {
                if (personasRegistradas.isEmpty()) {
                    System.out.println("No hay personas registradas aún. Seleccione opción 2.");
                    i--;
                    continue;
                }
                System.out.print("Ingrese la cédula: ");
                String ced = sc.next();
                for (Persona per : personasRegistradas) {
                    if (per.getCedula().equals(ced)) {
                        p = per;
                        break;
                    }
                }
                if (p == null) {
                    System.out.println("No se encontró ninguna persona con esa cédula.");
                    i--;
                    continue;
                }
            } else if (accion == 2) {
                String nombre = "";
                String apellido = "";
                int edad = -1;
                String cedula = "";
                String telefono = "";
                try {
                    System.out.print("Nombre (solo letras): ");
                    nombre = br.readLine();
                    System.out.print("Apellido (solo letras): ");
                    apellido = br.readLine();
                    System.out.print("Edad (0-60): ");
                    edad = sc.nextInt();
                    System.out.print("Cédula (10 dígitos): ");
                    cedula = sc.next();
                    System.out.print("Teléfono (10 dígitos): ");
                    telefono = sc.next();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                p = new Persona(nombre, apellido, edad, cedula, telefono);
                personasRegistradas.add(p);
            } else {
                System.out.println("Opción no válida.");
                i--;
                continue;
            }

            // Categoria
            if (!validarEdadParaCategoria(p.getEdad(), categoriaEquipo)) {
                System.out.println("Error: La edad del jugador (" + p.getEdad() + " años) NO corresponde a la categoría '" + categoriaEquipo + "'. Ingrese un jugador válido.");
                i--;
                continue;
            }


            if (jugadorYaInscritoEnCategoria(p.getCedula(), categoriaEquipo)) {
                System.out.println("Error: Este jugador ya está inscrito en otro equipo de la categoría " + categoriaEquipo + ".");
                i--;
                continue;
            }


            boolean repetidoLocal = false;
            for (Jugador j : listaTemporal) {
                if (j.getCedula().equals(p.getCedula())) {
                    repetidoLocal = true;
                    break;
                }
            }
            if (repetidoLocal) {
                System.out.println("Error: Este jugador ya fue agregado a este equipo.");
                i--;
                continue;
            }

            Jugador jugador = new Jugador(p.getNombre(), p.getApellido(), p.getEdad(), p.getCedula(), p.getTelefono(), false);
            listaTemporal.add(jugador);
            System.out.println("\n Jugador agregado correctamente \n");
        }


        System.out.println("\n Seleccione el número del jugador que será el CAPITÁN del Equipo \n");
        for (int i = 0; i < listaTemporal.size(); i++) {
            System.out.println((i + 1) + ". " + listaTemporal.get(i).getNombre() + " " + listaTemporal.get(i).getApellido());
        }

        int idxCapitan = -1;
        do {
            System.out.print("Opción: ");
            idxCapitan = sc.nextInt() - 1;
        } while (idxCapitan < 0 || idxCapitan >= listaTemporal.size());

        Jugador capitan = listaTemporal.get(idxCapitan);
        capitan.setEsCapitan(true);

        equipoSeleccionado.setCategoria(categoriaEquipo);
        equipoSeleccionado.setCapitan(capitan);
        equipoSeleccionado.setJugadores(listaTemporal);

        System.out.println("\n El jugador fue inscrito con éxito en '" + equipoSeleccionado.getNombreEquipo()
                + "' en la categoría " + categoriaEquipo + " Volviendo al menú...\n");
    }

    public void mostrarPersonas() {
        if (personasRegistradas.isEmpty()) {
            System.out.println("No hay personas registradas.");
            return;
        }
        System.out.println("PERSONAS REGISTRADAS ");
        for (Persona p : personasRegistradas) {
            System.out.println(p.toString());
        }
        System.out.println();
    }

    public void mostrarEquipos() {
        if (equipos.isEmpty()) {
            System.out.println("No hay equipos inscritos.");
            return;
        }
        for (Equipo e : equipos) {
            System.out.println(e.toString());
        }
        System.out.println();
    }

    public void verJugadoresDeEquipoEspecifico() {
        if (equipos.isEmpty()) {
            System.out.println("No hay equipos registrados.");
            return;
        }

        System.out.println("\n Seleccionar Equipo para ver los Jugadores \n");
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println((i + 1) + ". " + equipos.get(i).getNombreEquipo()
                    + " (Categoría: " + equipos.get(i).getCategoria() + ")");
        }
        System.out.println("0. Volver al menú principal");
        System.out.print("Seleccione el número del equipo: ");
        int opcion = sc.nextInt();

        if (opcion <= 0 || opcion > equipos.size()) {
            System.out.println("Volviendo al menú principal.");
            return;
        }

        Equipo seleccionado = equipos.get(opcion - 1);
        System.out.println(seleccionado.toString());
    }

    // Menu

    public void mostrarMenu() {
        int opc;
        do {
            System.out.println("\n Gestion de equipos y Jugadores \n");
            System.out.println("1. Registrar Persona");
            System.out.println("2. Crear Equipo");
            System.out.println("3. Inscribir Jugadores en un Equipo");
            System.out.println("4. Ver Personas Registradas");
            System.out.println("5. Ver Todos los Equipos e Inscripciones");
            System.out.println("6. Ver Jugadores de un Equipo Específico");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");
            opc = sc.nextInt();

            switch (opc) {
                case 1:
                    crearPersona();
                    break;
                case 2:
                    crearEquipo();
                    break;
                case 3:
                    inscribirJugadoresEnEquipo();
                    break;
                case 4:
                    mostrarPersonas();
                    break;
                case 5:
                    mostrarEquipos();
                    break;
                case 6:
                    verJugadoresDeEquipoEspecifico();
                    break;
                case 7:
                    System.out.println("Saliendo del sistema.");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opc != 7);
    }
}