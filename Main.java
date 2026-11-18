public class Main {
    public static void main(String[] args) {
        // Ejecuta la interfaz gráfica de forma segura en el hilo de eventos de Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            InterfazCampeonatoGUI ventana = new InterfazCampeonatoGUI();
            ventana.setVisible(true);
        });
    }
}