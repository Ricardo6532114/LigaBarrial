import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InterfazCampeonatoGUI extends JFrame {

    private final SistemaCLI moduloInscripciones;
    private final ModuloCalendario moduloCalendario;
    private final List<ActaPartido> actasRegistradas = new ArrayList<>();

    private DefaultTableModel modeloPersonas;
    private DefaultTableModel modeloEquipos;
    private DefaultTableModel modeloPartidos;

    private JComboBox<String> comboEquiposInscripcion;
    private JComboBox<String> comboPersonasExistentes;
    private JComboBox<String> comboEquiposDetalle;
    private JComboBox<String> comboPartidosReprogramar;
    private JComboBox<String> comboPartidosAsistencia;
    
    // Controles específicos para el Módulo 3
    private JComboBox<String> comboPartidosParaActa;
    private JComboBox<String> comboActasActivas;
    private JComboBox<String> comboEquipoEventoGol;
    private JComboBox<String> comboEquipoEventoTarjeta;
    private JComboBox<String> comboTipoTarjeta;

    private JTextArea areaDetalleEquipo;
    private JTextArea areaDetalleActa;

    private final List<Jugador> plantillaTemporal = new ArrayList<>();
    private DefaultListModel<String> modeloListaTemporal;
    private JComboBox<String> comboCapitanTemporal;

    public InterfazCampeonatoGUI() {
        moduloInscripciones = new SistemaCLI();
        moduloCalendario = new ModuloCalendario();

        setTitle("Sistema de Gestión Integral - Campeonato Barrial");
        setSize(1100, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font("SansSerif", Font.BOLD, 13));

        pestanas.addTab("1. Personas", crearPanelPersonas());
        pestanas.addTab("2. Equipos", crearPanelEquipos());
        pestanas.addTab("3. Inscribir Plantilla", crearPanelInscripcion());
        pestanas.addTab("4. Consultas", crearPanelConsultas());
        pestanas.addTab("5. Calendario y Partidos", crearPanelCalendario());
        pestanas.addTab("6. Actas, Goles y Sanciones (M3)", crearPanelActas());

        add(pestanas);
    }

    // =========================================================================
    // PESTAÑA 1: REGISTRAR PERSONA
    // =========================================================================
    private JPanel crearPanelPersonas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(new TitledBorder("Registrar Nueva Persona"));

        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtEdad = new JTextField();
        JTextField txtCedula = new JTextField();
        JTextField txtTelefono = new JTextField();
        JButton btnGuardar = new JButton("Registrar Persona");

        form.add(new JLabel("Nombre (solo letras):"));
        form.add(txtNombre);
        form.add(new JLabel("Apellido (solo letras):"));
        form.add(txtApellido);
        form.add(new JLabel("Edad (14 a 60 años):"));
        form.add(txtEdad);
        form.add(new JLabel("Cédula (10 dígitos):"));
        form.add(txtCedula);
        form.add(new JLabel("Teléfono (10 dígitos):"));
        form.add(txtTelefono);
        form.add(new JLabel(""));
        form.add(btnGuardar);

        modeloPersonas = new DefaultTableModel(new String[]{"Nombre", "Apellido", "Edad", "Cédula", "Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablaPersonas = new JTable(modeloPersonas);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String edadStr = txtEdad.getText().trim();
            String cedula = txtCedula.getText().trim();
            String telefono = txtTelefono.getText().trim();

            if (!moduloInscripciones.validarSoloLetras(nombre)) {
                mostrarError("El nombre solo debe contener letras.");
                return;
            }
            if (!moduloInscripciones.validarSoloLetras(apellido)) {
                mostrarError("El apellido solo debe contener letras.");
                return;
            }
            for (Persona p : moduloInscripciones.personasRegistradas) {
                if (p.getNombre().equalsIgnoreCase(nombre) && p.getApellido().equalsIgnoreCase(apellido)) {
                    mostrarError("Ya existe una persona registrada con este mismo nombre y apellido.");
                    return;
                }
            }

            int edad;
            try {
                edad = Integer.parseInt(edadStr);
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese una edad numérica válida.");
                return;
            }
            if (edad < 14 || edad > 60) {
                mostrarError("La edad debe estar entre 14 y 60 años.");
                return;
            }

            if (!moduloInscripciones.validarDiezDigitos(cedula)) {
                mostrarError("La cédula debe tener exactamente 10 números positivos.");
                return;
            }
            for (Persona existente : moduloInscripciones.personasRegistradas) {
                if (existente.getCedula().equals(cedula)) {
                    mostrarError("Ya existe una persona registrada con esa cédula.");
                    return;
                }
            }

            if (!moduloInscripciones.validarDiezDigitos(telefono)) {
                mostrarError("El teléfono debe tener exactamente 10 números positivos.");
                return;
            }
            for (Persona existente : moduloInscripciones.personasRegistradas) {
                if (existente.getTelefono().equals(telefono)) {
                    mostrarError("Este número de teléfono ya está registrado.");
                    return;
                }
            }

            Persona nueva = new Persona(nombre, apellido, edad, cedula, telefono);
            moduloInscripciones.personasRegistradas.add(nueva);

            txtNombre.setText("");
            txtApellido.setText("");
            txtEdad.setText("");
            txtCedula.setText("");
            txtTelefono.setText("");

            actualizarVistasInscripciones();
            JOptionPane.showMessageDialog(this, "Persona registrada correctamente.");
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPersonas), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // PESTAÑA 2: CREAR EQUIPO
    // =========================================================================
    private JPanel crearPanelEquipos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.setBorder(new TitledBorder("Crear Nuevo Equipo"));

        JTextField txtNombreEquipo = new JTextField(25);
        JButton btnCrearEquipo = new JButton("Crear Equipo");

        form.add(new JLabel("Nombre del Equipo:"));
        form.add(txtNombreEquipo);
        form.add(btnCrearEquipo);

        modeloEquipos = new DefaultTableModel(new String[]{"Equipo", "Categoría", "Capitán", "Jugadores Inscritos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablaEquipos = new JTable(modeloEquipos);

        btnCrearEquipo.addActionListener(e -> {
            String nombreEquipo = txtNombreEquipo.getText().trim();
            if (nombreEquipo.isEmpty()) {
                mostrarError("Ingrese un nombre de equipo.");
                return;
            }
            for (Equipo eq : moduloInscripciones.equipos) {
                if (eq.getNombreEquipo().equalsIgnoreCase(nombreEquipo)) {
                    mostrarError("Ya existe un equipo con ese nombre.");
                    return;
                }
            }
            moduloInscripciones.equipos.add(new Equipo(nombreEquipo));
            txtNombreEquipo.setText("");
            actualizarVistasInscripciones();
            JOptionPane.showMessageDialog(this, "El equipo '" + nombreEquipo + "' fue creado.");
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaEquipos), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // PESTAÑA 3: INSCRIBIR JUGADORES EN EQUIPO
    // =========================================================================
    private JPanel crearPanelInscripcion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(new TitledBorder("1. Configuración del Equipo"));

        comboEquiposInscripcion = new JComboBox<>();
        JComboBox<String> comboCategoria = new JComboBox<>(new String[]{
                "Juvenil (Sub-18)",
                "Senior",
                "Máster (+40)"
        });

        topPanel.add(new JLabel("Seleccione el Equipo (sin plantilla):"));
        topPanel.add(comboEquiposInscripcion);
        topPanel.add(new JLabel("Categoría del Equipo:"));
        topPanel.add(comboCategoria);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel panelBuscar = new JPanel(new BorderLayout(8, 8));
        panelBuscar.setBorder(new TitledBorder("2A. Agregar Persona Ya Registrada"));
        comboPersonasExistentes = new JComboBox<>();
        JButton btnAgregarExistente = new JButton("Agregar Jugador Seleccionado");

        JPanel subBuscar = new JPanel(new GridLayout(3, 1, 5, 5));
        subBuscar.add(new JLabel("Seleccione persona por cédula:"));
        subBuscar.add(comboPersonasExistentes);
        subBuscar.add(btnAgregarExistente);
        panelBuscar.add(subBuscar, BorderLayout.NORTH);

        JPanel panelNuevo = new JPanel(new GridLayout(6, 2, 5, 5));
        panelNuevo.setBorder(new TitledBorder("2B. O Registrar Nueva Persona y Agregar"));
        JTextField txtNom = new JTextField();
        JTextField txtApe = new JTextField();
        JTextField txtEd = new JTextField();
        JTextField txtCed = new JTextField();
        JTextField txtTel = new JTextField();
        JButton btnAgregarNuevo = new JButton("Registrar y Agregar");

        panelNuevo.add(new JLabel("Nombre:"));
        panelNuevo.add(txtNom);
        panelNuevo.add(new JLabel("Apellido:"));
        panelNuevo.add(txtApe);
        panelNuevo.add(new JLabel("Edad:"));
        panelNuevo.add(txtEd);
        panelNuevo.add(new JLabel("Cédula (10 díg):"));
        panelNuevo.add(txtCed);
        panelNuevo.add(new JLabel("Teléfono (10 díg):"));
        panelNuevo.add(txtTel);
        panelNuevo.add(new JLabel(""));
        panelNuevo.add(btnAgregarNuevo);

        centerPanel.add(panelBuscar);
        centerPanel.add(panelNuevo);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new TitledBorder("3. Plantilla Temporal y Designación de Capitán"));

        modeloListaTemporal = new DefaultListModel<>();
        JList<String> listaVisualTemporal = new JList<>(modeloListaTemporal);
        JScrollPane scrollTemp = new JScrollPane(listaVisualTemporal);
        scrollTemp.setPreferredSize(new Dimension(400, 130));

        JPanel accionesFinales = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        comboCapitanTemporal = new JComboBox<>();
        JButton btnLimpiarTemp = new JButton("Limpiar Lista");
        JButton btnConfirmarInscripcion = new JButton("Confirmar Inscripción de Plantilla");
        btnConfirmarInscripcion.setFont(new Font("SansSerif", Font.BOLD, 12));

        accionesFinales.add(new JLabel("Capitán del Equipo:"));
        accionesFinales.add(comboCapitanTemporal);
        accionesFinales.add(btnLimpiarTemp);
        accionesFinales.add(btnConfirmarInscripcion);

        bottomPanel.add(scrollTemp, BorderLayout.CENTER);
        bottomPanel.add(accionesFinales, BorderLayout.SOUTH);

        btnAgregarExistente.addActionListener(e -> {
            if (comboEquiposInscripcion.getSelectedIndex() < 0) {
                mostrarError("No hay equipos disponibles sin plantilla.");
                return;
            }
            int idxPersona = comboPersonasExistentes.getSelectedIndex();
            if (idxPersona < 0) {
                mostrarError("No hay personas registradas.");
                return;
            }
            Persona p = moduloInscripciones.personasRegistradas.get(idxPersona);
            String categoria = (String) comboCategoria.getSelectedItem();
            agregarJugadorATemporal(p, categoria);
        });

        btnAgregarNuevo.addActionListener(e -> {
            if (comboEquiposInscripcion.getSelectedIndex() < 0) {
                mostrarError("No hay equipos disponibles sin plantilla.");
                return;
            }
            String nombre = txtNom.getText().trim();
            String apellido = txtApe.getText().trim();
            String cedula = txtCed.getText().trim();
            String telefono = txtTel.getText().trim();
            int edad;

            if (!moduloInscripciones.validarSoloLetras(nombre) || !moduloInscripciones.validarSoloLetras(apellido)) {
                mostrarError("Nombre y apellido deben contener solo letras.");
                return;
            }
            try {
                edad = Integer.parseInt(txtEd.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarError("Edad inválida.");
                return;
            }
            if (!moduloInscripciones.validarDiezDigitos(cedula) || !moduloInscripciones.validarDiezDigitos(telefono)) {
                mostrarError("Cédula y teléfono deben tener 10 dígitos.");
                return;
            }
            for (Persona existente : moduloInscripciones.personasRegistradas) {
                if (existente.getCedula().equals(cedula)) {
                    mostrarError("Ya existe una persona con esa cédula.");
                    return;
                }
            }

            Persona nueva = new Persona(nombre, apellido, edad, cedula, telefono);
            String categoria = (String) comboCategoria.getSelectedItem();

            if (agregarJugadorATemporal(nueva, categoria)) {
                moduloInscripciones.personasRegistradas.add(nueva);
                txtNom.setText("");
                txtApe.setText("");
                txtEd.setText("");
                txtCed.setText("");
                txtTel.setText("");
                actualizarVistasInscripciones();
            }
        });

        btnLimpiarTemp.addActionListener(e -> limpiarPlantillaTemporal());

        btnConfirmarInscripcion.addActionListener(e -> {
            String nombreEqSeleccionado = (String) comboEquiposInscripcion.getSelectedItem();
            if (nombreEqSeleccionado == null) {
                mostrarError("Seleccione un equipo válido.");
                return;
            }
            if (plantillaTemporal.isEmpty()) {
                mostrarError("Debe agregar al menos un jugador a la plantilla.");
                return;
            }
            int idxCap = comboCapitanTemporal.getSelectedIndex();
            if (idxCap < 0) {
                mostrarError("Seleccione un capitán.");
                return;
            }

            Equipo equipoObj = null;
            for (Equipo eq : moduloInscripciones.equipos) {
                if (eq.getNombreEquipo().equals(nombreEqSeleccionado)) {
                    equipoObj = eq;
                    break;
                }
            }
            if (equipoObj == null) return;

            String categoria = (String) comboCategoria.getSelectedItem();
            List<Jugador> listaFinal = new ArrayList<>(plantillaTemporal);
            Jugador capitan = listaFinal.get(idxCap);
            capitan.setEsCapitan(true);

            equipoObj.setCategoria(categoria);
            equipoObj.setCapitan(capitan);
            equipoObj.setJugadores(listaFinal);

            limpiarPlantillaTemporal();
            actualizarVistasInscripciones();
            JOptionPane.showMessageDialog(this, "Plantilla inscrita con éxito en '" + equipoObj.getNombreEquipo() + "'.");
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private boolean agregarJugadorATemporal(Persona p, String categoria) {
        if (!moduloInscripciones.validarEdadParaCategoria(p.getEdad(), categoria)) {
            mostrarError("La edad del jugador (" + p.getEdad() + " años) NO corresponde a la categoría '" + categoria + "'.");
            return false;
        }
        if (moduloInscripciones.jugadorYaInscritoEnCategoria(p.getCedula(), categoria)) {
            mostrarError("Este jugador ya está inscrito en otro equipo de la categoría " + categoria + ".");
            return false;
        }
        for (Jugador j : plantillaTemporal) {
            if (j.getCedula().equals(p.getCedula())) {
                mostrarError("Este jugador ya fue agregado a esta plantilla.");
                return false;
            }
        }

        Jugador jugador = new Jugador(p.getNombre(), p.getApellido(), p.getEdad(), p.getCedula(), p.getTelefono(), false);
        plantillaTemporal.add(jugador);
        modeloListaTemporal.addElement(jugador.toString());
        comboCapitanTemporal.addItem(jugador.getNombre() + " " + jugador.getApellido() + " (" + jugador.getCedula() + ")");
        return true;
    }

    private void limpiarPlantillaTemporal() {
        plantillaTemporal.clear();
        modeloListaTemporal.clear();
        comboCapitanTemporal.removeAllItems();
    }

    // =========================================================================
    // PESTAÑA 4: CONSULTAR EQUIPOS Y JUGADORES
    // =========================================================================
    private JPanel crearPanelConsultas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        comboEquiposDetalle = new JComboBox<>();
        JButton btnVerEquipo = new JButton("Ver Detalle del Equipo");
        JButton btnVerTodos = new JButton("Ver Todos los Equipos");

        top.add(new JLabel("Seleccionar Equipo:"));
        top.add(comboEquiposDetalle);
        top.add(btnVerEquipo);
        top.add(btnVerTodos);

        areaDetalleEquipo = new JTextArea();
        areaDetalleEquipo.setEditable(false);
        areaDetalleEquipo.setFont(new Font("Monospaced", Font.PLAIN, 13));

        btnVerEquipo.addActionListener(e -> {
            int idx = comboEquiposDetalle.getSelectedIndex();
            if (idx < 0 || moduloInscripciones.equipos.isEmpty()) {
                areaDetalleEquipo.setText("No hay equipos registrados.");
                return;
            }
            areaDetalleEquipo.setText(moduloInscripciones.equipos.get(idx).toString());
        });

        btnVerTodos.addActionListener(e -> {
            if (moduloInscripciones.equipos.isEmpty()) {
                areaDetalleEquipo.setText("No hay equipos inscritos.");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Equipo eq : moduloInscripciones.equipos) {
                sb.append(eq.toString()).append("\n--------------------------------------------------\n");
            }
            areaDetalleEquipo.setText(sb.toString());
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaDetalleEquipo), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // PESTAÑA 5: CALENDARIO, REPROGRAMACIÓN E INCOMPARECENCIAS
    // =========================================================================
    private JPanel crearPanelCalendario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBorder(new TitledBorder("Generación de Fixture"));
        JButton btnGenerarCalendario = new JButton("Generar Calendario con Equipos Inscritos");
        btnGenerarCalendario.setFont(new Font("SansSerif", Font.BOLD, 12));
        top.add(btnGenerarCalendario);

        modeloPartidos = new DefaultTableModel(new String[]{"#", "Estado", "Local", "Visitante", "Cancha", "Fecha", "Hora"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablaPartidos = new JTable(modeloPartidos);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel panelReprog = new JPanel(new GridLayout(4, 2, 5, 5));
        panelReprog.setBorder(new TitledBorder("Reprogramar Partido"));
        comboPartidosReprogramar = new JComboBox<>();
        JTextField txtNuevaFecha = new JTextField("23-11-2026");
        JTextField txtNuevaHora = new JTextField("15");
        JButton btnReprogramar = new JButton("Reprogramar");

        panelReprog.add(new JLabel("Partido:"));
        panelReprog.add(comboPartidosReprogramar);
        panelReprog.add(new JLabel("Nueva Fecha (Día-Mes-Año):"));
        panelReprog.add(txtNuevaFecha);
        panelReprog.add(new JLabel("Nueva Hora (ej. 15 sin :00):"));
        panelReprog.add(txtNuevaHora);
        panelReprog.add(new JLabel(""));
        panelReprog.add(btnReprogramar);

        JPanel panelAsist = new JPanel(new GridLayout(3, 2, 5, 5));
        panelAsist.setBorder(new TitledBorder("Gestionar Asistencia / Incomparecencia"));
        comboPartidosAsistencia = new JComboBox<>();
        JComboBox<String> comboEstadoAsistencia = new JComboBox<>(new String[]{
                "1. Ambos equipos se presentaron completos",
                "2. Equipo Local no completó el mínimo",
                "3. Equipo Visita no completó el mínimo",
                "4. Ninguno se presentó (Ambos pierden)"
        });
        JButton btnGuardarAsistencia = new JButton("Registrar Asistencia");

        panelAsist.add(new JLabel("Partido:"));
        panelAsist.add(comboPartidosAsistencia);
        panelAsist.add(new JLabel("Resolución:"));
        panelAsist.add(comboEstadoAsistencia);
        panelAsist.add(new JLabel(""));
        panelAsist.add(btnGuardarAsistencia);

        bottom.add(panelReprog);
        bottom.add(panelAsist);

        btnGenerarCalendario.addActionListener(e -> {
            List<String> nombresEquipos = new ArrayList<>();
            for (Equipo eq : moduloInscripciones.equipos) {
                nombresEquipos.add(eq.getNombreEquipo());
            }
            String salida = ejecutarCapturandoConsola(() ->
                    moduloCalendario.generarCalendario(nombresEquipos, "cancha principal", 14)
            );
            actualizarTablaPartidos();
            JOptionPane.showMessageDialog(this, salida.trim());
        });

        btnReprogramar.addActionListener(e -> {
            int idx = comboPartidosReprogramar.getSelectedIndex();
            if (idx < 0) {
                mostrarError("No hay partidos disponibles.");
                return;
            }
            String fecha = txtNuevaFecha.getText().trim();
            String horaStr = txtNuevaHora.getText().trim();
            try {
                Integer.parseInt(horaStr);
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese una hora entera válida (ej. 15).");
                return;
            }

            String entradaSimulada = (idx + 1) + "\n" + fecha + "\n" + horaStr + "\n";
            Scanner scSimulado = new Scanner(new ByteArrayInputStream(entradaSimulada.getBytes(StandardCharsets.UTF_8)));

            String salida = ejecutarCapturandoConsola(() -> moduloCalendario.reprogramarPartido(scSimulado));
            String[] lineas = salida.trim().split("\n");
            String mensajeFinal = lineas[lineas.length - 1];

            actualizarTablaPartidos();
            JOptionPane.showMessageDialog(this, mensajeFinal);
        });

        btnGuardarAsistencia.addActionListener(e -> {
            int idx = comboPartidosAsistencia.getSelectedIndex();
            if (idx < 0) {
                mostrarError("No hay partidos disponibles.");
                return;
            }
            int opcionAsistencia = comboEstadoAsistencia.getSelectedIndex() + 1;

            String entradaSimulada = (idx + 1) + "\n" + opcionAsistencia + "\n";
            Scanner scSimulado = new Scanner(new ByteArrayInputStream(entradaSimulada.getBytes(StandardCharsets.UTF_8)));

            String salida = ejecutarCapturandoConsola(() -> moduloCalendario.gestionarIncomparecencia(scSimulado));
            String[] lineas = salida.trim().split("\n");
            String mensajeFinal = lineas[lineas.length - 1];

            actualizarTablaPartidos();
            JOptionPane.showMessageDialog(this, mensajeFinal);
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPartidos), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // PESTAÑA 6: MÓDULO 3 (ACTAS, GOLES Y SANCIONES / RF6 Y RF7)
    // =========================================================================
    private JPanel crearPanelActas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelControles = new JPanel(new GridLayout(3, 1, 10, 10));

        // 1. Abrir Acta (Mínimo de jugadores fijo a 6 por defecto)
        JPanel subIniciar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        subIniciar.setBorder(new TitledBorder("1. Abrir Acta de Partido"));
        comboPartidosParaActa = new JComboBox<>();
        JTextField txtMinJugadores = new JTextField("6", 3); // Mínimo 6 jugadores en cancha
        JButton btnIniciarActa = new JButton("Iniciar Acta (Mín. 6 jugadores)");

        subIniciar.add(new JLabel("Seleccionar Partido Programado:"));
        subIniciar.add(comboPartidosParaActa);
        subIniciar.add(new JLabel("Mínimo:"));
        subIniciar.add(txtMinJugadores);
        subIniciar.add(btnIniciarActa);

        // 2. Registrar Eventos (Goles y Tarjetas organizados de forma clara)
        JPanel subEventos = new JPanel(new GridLayout(2, 1, 5, 5));
        subEventos.setBorder(new TitledBorder("2. Registrar Goles y Tarjetas (RF6 y RF7)"));
        comboActasActivas = new JComboBox<>();

        // Subpanel Goles
        JPanel subGol = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JComboBox<String> comboJugadoresGol = new JComboBox<>();
        comboEquipoEventoGol = new JComboBox<>(new String[]{"Local", "Visitante"});
        JTextField txtMinutoGol = new JTextField(3);
        JButton btnGol = new JButton("Registrar Gol");

        subGol.add(new JLabel("Acta:"));
        subGol.add(comboActasActivas);
        subGol.add(new JLabel("Equipo:"));
        subGol.add(comboEquipoEventoGol);
        subGol.add(new JLabel("Cédula Jugador:"));
        JTextField txtCedulaGol = new JTextField(9);
        subGol.add(txtCedulaGol);
        subGol.add(new JLabel("Min':"));
        subGol.add(txtMinutoGol);
        subGol.add(btnGol);

        // Subpanel Tarjetas (Sanciones con opción a elegir Amarilla o Roja y Minuto)
        JPanel subTarjeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JComboBox<String> comboActasActivasTarjeta = new JComboBox<>();
        comboEquipoEventoTarjeta = new JComboBox<>(new String[]{"Local", "Visitante"});
        JTextField txtCedulaTarjeta = new JTextField(9);
        JTextField txtMinutoTarjeta = new JTextField(3);
        comboTipoTarjeta = new JComboBox<>(new String[]{"Amarilla", "Roja"});
        JButton btnTarjeta = new JButton("Registrar Tarjeta");

        subTarjeta.add(new JLabel("Equipo:"));
        subTarjeta.add(comboEquipoEventoTarjeta);
        subTarjeta.add(new JLabel("Cédula Jugador:"));
        subTarjeta.add(txtCedulaTarjeta);
        subTarjeta.add(new JLabel("Min':"));
        subTarjeta.add(txtMinutoTarjeta);
        subTarjeta.add(new JLabel("Tipo:"));
        subTarjeta.add(comboTipoTarjeta);
        subTarjeta.add(btnTarjeta);

        subEventos.add(subGol);
        subEventos.add(subTarjeta);

        // 3. Cierre y Finalización
        JPanel subFinalizar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        subFinalizar.setBorder(new TitledBorder("3. Cierre y Disciplina"));
        JButton btnFinalizarPartido = new JButton("Finalizar Partido / Ver Acta Oficial");
        JButton btnAvanzarFecha = new JButton("Avanzar Fecha (Cumplir Suspensión)");
        btnFinalizarPartido.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAvanzarFecha.setFont(new Font("SansSerif", Font.BOLD, 12));

        subFinalizar.add(btnFinalizarPartido);
        subFinalizar.add(btnAvanzarFecha);

        panelControles.add(subIniciar);
        panelControles.add(subEventos);
        panelControles.add(subFinalizar);

        areaDetalleActa = new JTextArea();
        areaDetalleActa.setEditable(false);
        areaDetalleActa.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // Eventos de la pestaña Actas
        btnIniciarActa.addActionListener(e -> {
            int idx = comboPartidosParaActa.getSelectedIndex();
            List<Partido> partidos = moduloCalendario.getPartidos();
            if (idx < 0 || partidos.isEmpty()) {
                mostrarError("Seleccione un partido válido del calendario.");
                return;
            }
            Partido p = partidos.get(idx);
            Equipo local = buscarEquipoPorNombre(p.getLocal());
            Equipo visitante = buscarEquipoPorNombre(p.getVisitante());

            if (local == null || visitante == null) {
                mostrarError("No se encontraron los equipos inscritos en el sistema.");
                return;
            }

            int minJug = 6; // Obligatorio mínimo 6 jugadores en cancha
            try {
                minJug = Integer.parseInt(txtMinJugadores.getText().trim());
            } catch (Exception ignored) {}

            ActaPartido acta = new ActaPartido(p, local, visitante, minJug);
            boolean iniciado = acta.iniciarPartido();
            actasRegistradas.add(acta);
            actualizarCombosActas();

            if (iniciado) {
                JOptionPane.showMessageDialog(this, "Acta abierta correctamente. Partido en curso (Mín. 6 jugadores).");
            } else {
                JOptionPane.showMessageDialog(this, "¡PARTIDO SUSPENDIDO! No cumple con el mínimo de 6 jugadores habilitados en cancha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            areaDetalleActa.setText(acta.generarTextoActa());
        });

        btnGol.addActionListener(e -> {
            ActaPartido acta = obtenerActaSeleccionadaCombo();
            if (acta == null) return;

            Equipo eq = comboEquipoEventoGol.getSelectedIndex() == 0 ? acta.getEquipoLocal() : acta.getEquipoVisitante();
            String cedula = txtCedulaGol.getText().trim();
            Jugador j = buscarJugadorPorCedula(eq, cedula);

            if (j == null) {
                mostrarError("Jugador no encontrado con esa cédula en el equipo seleccionado (" + eq.getNombreEquipo() + ").");
                return;
            }

            int minuto;
            try {
                minuto = Integer.parseInt(txtMinutoGol.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un minuto válido para el gol.");
                return;
            }

            acta.registrarGol(eq, j, minuto);
            txtMinutoGol.setText("");
            txtCedulaGol.setText("");
            areaDetalleActa.setText(acta.generarTextoActa());
            JOptionPane.showMessageDialog(this, "Gol de " + j.getNombre() + " (" + eq.getNombreEquipo() + ") registrado en el minuto " + minuto + "'.");
        });

        btnTarjeta.addActionListener(e -> {
            ActaPartido acta = obtenerActaSeleccionadaCombo();
            if (acta == null) return;

            Equipo eq = comboEquipoEventoTarjeta.getSelectedIndex() == 0 ? acta.getEquipoLocal() : acta.getEquipoVisitante();
            String cedula = txtCedulaTarjeta.getText().trim();
            Jugador j = buscarJugadorPorCedula(eq, cedula);

            if (j == null) {
                mostrarError("Jugador no encontrado con esa cédula en el equipo seleccionado (" + eq.getNombreEquipo() + ").");
                return;
            }

            int minuto;
            try {
                minuto = Integer.parseInt(txtMinutoTarjeta.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un minuto válido para la tarjeta.");
                return;
            }

            String tipoTarjeta = (String) comboTipoTarjeta.getSelectedItem();
            acta.registrarSancion(eq, j, tipoTarjeta, minuto, "Infracción reglamentaria");
            txtMinutoTarjeta.setText("");
            txtCedulaTarjeta.setText("");
            areaDetalleActa.setText(acta.generarTextoActa());

            if (j.isExpulsado()) {
                JOptionPane.showMessageDialog(this, "¡ALERTA RF7! El jugador " + j.getNombre() + " acumuló tarjeta roja / doble amarilla, fue expulsado y suspendido 1 partido.", "Disciplina", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Tarjeta " + tipoTarjeta + " registrada al jugador " + j.getNombre() + " en el minuto " + minuto + "'.");
            }
        });

        btnFinalizarPartido.addActionListener(e -> {
            ActaPartido acta = obtenerActaSeleccionadaCombo();
            if (acta == null) return;
            acta.finalizarPartido();
            areaDetalleActa.setText(acta.generarTextoActa());
            JOptionPane.showMessageDialog(this, "Partido finalizado y resultado oficial calculado.");
        });

        btnAvanzarFecha.addActionListener(e -> {
            for (Equipo eq : moduloInscripciones.equipos) {
                for (Jugador j : eq.getJugadores()) {
                    j.cumplirFechaSuspension();
                }
            }
            JOptionPane.showMessageDialog(this, "Se ha avanzado de fecha: se ha descontado 1 partido de suspensión a los jugadores sancionados.");
        });

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaDetalleActa), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE SINCRONIZACIÓN Y CAPTURA DE CONSOLA
    // =========================================================================
    private void actualizarVistasInscripciones() {
        modeloPersonas.setRowCount(0);
        comboPersonasExistentes.removeAllItems();
        for (Persona p : moduloInscripciones.personasRegistradas) {
            modeloPersonas.addRow(new Object[]{p.getNombre(), p.getApellido(), p.getEdad(), p.getCedula(), p.getTelefono()});
            comboPersonasExistentes.addItem(p.getCedula() + " - " + p.getNombre() + " " + p.getApellido() + " (" + p.getEdad() + " años)");
        }

        modeloEquipos.setRowCount(0);
        comboEquiposInscripcion.removeAllItems();
        comboEquiposDetalle.removeAllItems();
        for (Equipo eq : moduloInscripciones.equipos) {
            String cap = (eq.getCapitan() != null)
                    ? eq.getCapitan().getNombre() + " " + eq.getCapitan().getApellido()
                    : "Sin asignar";
            modeloEquipos.addRow(new Object[]{eq.getNombreEquipo(), eq.getCategoria(), cap, eq.getJugadores().size()});
            comboEquiposDetalle.addItem(eq.getNombreEquipo());

            if (eq.getJugadores().isEmpty()) {
                comboEquiposInscripcion.addItem(eq.getNombreEquipo());
            }
        }
    }

    private void actualizarTablaPartidos() {
        modeloPartidos.setRowCount(0);
        comboPartidosReprogramar.removeAllItems();
        comboPartidosAsistencia.removeAllItems();
        if (comboPartidosParaActa != null) comboPartidosParaActa.removeAllItems();

        String salida = ejecutarCapturandoConsola(moduloCalendario::consultarCalendario);
        String[] lineas = salida.split("\n");

        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.matches("^\\d+\\.\\s+\\[.*")) {
                int puntoIdx = linea.indexOf('.');
                String num = linea.substring(0, puntoIdx).trim();
                String resto = linea.substring(puntoIdx + 1).trim();

                int cierreCorchete = resto.indexOf(']');
                String estado = resto.substring(1, cierreCorchete);
                String datos = resto.substring(cierreCorchete + 1).trim();

                String[] partes = datos.split("\\|");
                String[] vs = partes[0].trim().split("-vs-");
                String local = vs[0].trim();
                String visitante = vs.length > 1 ? vs[1].trim() : "";
                String cancha = partes[1].replace("Cancha:", "").trim();
                String fecha = partes[2].replace("Fecha:", "").trim();
                String hora = partes[3].replace("Hora:", "").trim();

                modeloPartidos.addRow(new Object[]{num, estado, local, visitante, cancha, fecha, hora});
                String etiqueta = num + ". " + local + " vs " + visitante + " (" + fecha + " " + hora + ")";
                comboPartidosReprogramar.addItem(etiqueta);
                comboPartidosAsistencia.addItem(etiqueta);
                if (comboPartidosParaActa != null) comboPartidosParaActa.addItem(etiqueta);
            }
        }
    }

    private void actualizarCombosActas() {
        if (comboActasActivas != null) {
            comboActasActivas.removeAllItems();
            for (ActaPartido acta : actasRegistradas) {
                comboActasActivas.addItem(acta.getEquipoLocal().getNombreEquipo() + " vs " + acta.getEquipoVisitante().getNombreEquipo());
            }
        }
    }

    private Equipo buscarEquipoPorNombre(String nombre) {
        for (Equipo eq : moduloInscripciones.equipos) {
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

    private ActaPartido obtenerActaSeleccionadaCombo() {
        int idx = comboActasActivas.getSelectedIndex();
        if (idx < 0 || actasRegistradas.isEmpty()) {
            mostrarError("Seleccione un acta activa válida.");
            return null;
        }
        return actasRegistradas.get(idx);
    }

    private String ejecutarCapturandoConsola(Runnable accion) {
        PrintStream salidaOriginal = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            System.setOut(ps);
            accion.run();
        } finally {
            System.setOut(salidaOriginal);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new InterfazCampeonatoGUI().setVisible(true);
        });
    }
}
