package pr2_cuentas;

import javax.swing.JOptionPane;

public abstract class Validaciones {

    public static String IngresarString(String mensaje) {
        String dato;
        do {
            dato = JOptionPane.showInputDialog(mensaje);
            if (dato == null) dato = ""; // cancelar -> repetir
            if (dato.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: el dato no puede ser vacío.");
            }
        } while (dato.isEmpty());
        return dato;
    }

    public static int IngresarInt(String mensaje) {
        boolean flag;
        String dato;
        do {
            flag = true;
            dato = JOptionPane.showInputDialog(mensaje);
            if (dato == null || dato.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: el dato no puede ser vacío.");
                flag = false;
            } else {
                for (int i = 0; i < dato.length(); i++) {
                    if (!Character.isDigit(dato.charAt(i)) && (i != 0 || dato.charAt(0) != '-')) {
                        flag = false;
                        JOptionPane.showMessageDialog(null, "El dato debe ser numérico.");
                        break;
                    }
                }
            }
        } while (!flag);
        return Integer.parseInt(dato);
    }
    
    public static Integer IngresarDNI(String mensaje) {
        boolean flag;
        String dato;
        do {
            flag = true;
            dato = JOptionPane.showInputDialog(mensaje);

            // Check if user pressed Cancel (dato is null)
            if (dato == null) {
                return null;  // Return null if Cancel was pressed
            }

            // If the input is empty, show an error message and continue the loop
            if (dato.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: el dato no puede ser vacío.");
                flag = false;
            } else {
                // Validate the input - check if it's numeric and potentially has a minus sign at the start
                for (int i = 0; i < dato.length(); i++) {
                    if (!Character.isDigit(dato.charAt(i)) && (i != 0 || dato.charAt(0) != '-')) {
                        flag = false;
                        JOptionPane.showMessageDialog(null, "El dato debe ser numérico.");
                        break;
                    }
                }
            }
        } while (!flag);
        
        try {
            return Integer.parseInt(dato);  // Return the entered DNI as an integer
        } catch (NumberFormatException e) {
            return -1;  // If something goes wrong with parsing, return -1
        }
    }

}

