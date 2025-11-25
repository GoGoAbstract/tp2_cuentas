package pr2_cuentas;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final Banco banco = new Banco();

    public static void main(String[] args) {
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Aceptar");

  
        
      //Admin por defecto
        if (banco.getUsuario(0) == null) {
            banco.crearUsuario(
                    1,
                    Usuario.TIPO_ADMINISTRADOR,
                    "Default Admin",
                    "Default Admin",
                    "12345678",
                    ""
            );
        }

        try {
            PopulateInitialData.cargar(banco);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error cargando datos iniciales: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        };

        
        
        

     // Login loop
        while (true) {
            Usuario logueado = login();
            if (logueado == null) break;

            if (logueado.getTipo() == Usuario.TIPO_ADMINISTRADOR) {
                menuAdministrador();
            } else if (logueado.getTipo() == Usuario.TIPO_EMPLEADO) {
                menuEmpleado();
            } else { // cliente particular
                menuCliente(logueado.getDni());
            }
        }
    }

    //Login
    private static Usuario login() {
        Integer dni = null;
        String password = null;
        
        do {
        
        // DNI con Validaciones
        try {
            dni = Validaciones.IngresarDNI("Ingrese DNI:");
        if (dni == -1)
        {
        	continue;
        } 
  
        } catch (Exception e) {
            return null;
        }
        }
        while (dni < 0);
        
        // password
        JPasswordField pf = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(null, pf, "Ingrese Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        password = new String(pf.getPassword());

        Usuario u = banco.autenticar(dni, password);
        if (u == null) {
            JOptionPane.showMessageDialog(null, "Credenciales inválidas.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        
        return u;
    }

    //menu administrador
    private static void menuAdministrador() {
        String[] administradorMenuPrincipal = {"Usuarios", "Cuentas", "Transacciones", "Monedas", "Productos", "Volver"};
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                    null, "Seleccione una opción", "Menú administrador",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, administradorMenuPrincipal, administradorMenuPrincipal[0]
            );
            if (opcion == 0) menuUsuarios();
            else if (opcion == 1) menuCuentasAdmin();
            else if (opcion == 2) menuTransaccionesAdmin();
            else if (opcion == 3) menuMonedasAdmin(); // SOLO admin
            else if (opcion == 4) menuProductos();    // préstamos, inversiones y servicios
            else break;
        }
    }
    
    
    //menu empleado
    private static void menuEmpleado() {
        String[] empleadoMenuPrincipal = {"Usuarios (ver)", "Cuentas", "Transacciones", "Productos", "Volver"};
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                    null, "Seleccione una opción", "Menú empleado",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, empleadoMenuPrincipal, empleadoMenuPrincipal[0]
            );
            if (opcion == 0) menuUsuariosEmpleado(); // solo lectura
            else if (opcion == 1) menuCuentasAdmin();        // mismas operaciones de cuentas que admin
            else if (opcion == 2) menuTransaccionesAdmin();  // depósitos, retiros, transferencias
            else if (opcion == 3) menuProductos();           // préstamos, inversiones, servicios
            else break;
        }
    }


    //Menú Cliente (solo ver sus cuentas + transferir)
    private static void menuCliente(int dni) {
        String[] userMenu = {"Ver mis cuentas", "Depósito", "Pagar servicio", "Transferencia", "Ver mi historial", "Volver"};
        while (true) {
            int op = JOptionPane.showOptionDialog(
                    null, "Seleccione una opción", "Menú cliente",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, userMenu, userMenu[0]
            );
            if (op == 0) { // Ver cuentas
                List<Cuenta> lista = banco.getCuentasPorUsuario(dni);
                String texto = lista.isEmpty() ? "No tenés cuentas."
                        : lista.stream().map(Cuenta::toString).collect(java.util.stream.Collectors.joining("\n"));
                info(texto);

            } else if (op == 1) { // Depósito
                Integer idCuenta = pedirEntero("ID de la cuenta destino (debe ser tuya):");
                if (idCuenta == null) continue;
                Cuenta c = banco.getCuenta(idCuenta);
                if (c == null || c.getUsuario().getDni() != dni) {
                    error("La cuenta seleccionada no te pertenece.");
                    continue;
                }
                Float monto = pedirFloatPositivo("Monto a depositar (> 0):");
                if (monto == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                banco.depositar(idCuenta, monto, notas);

            } else if (op == 2) { // Pagar servicio
                Integer idOrigen = pedirEntero("ID de la cuenta ORIGEN (debe ser tuya):");
                if (idOrigen == null) continue;
                Cuenta origen = banco.getCuenta(idOrigen);
                if (origen == null || origen.getUsuario().getDni() != dni) {
                    error("La cuenta de origen no te pertenece.");
                    continue;
                }
                Integer idServicio = pedirEntero("ID de la cuenta SERVICIO:");
                if (idServicio == null) continue;
                Float monto = pedirFloatPositivo("Monto a pagar (> 0):");
                if (monto == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                banco.pagarServicio(idOrigen, idServicio, monto, notas);

            } else if (op == 3) { // Transferencia
                Integer idOrigen = pedirEntero("ID de la cuenta ORIGEN (debe ser tuya):");
                if (idOrigen == null) continue;
                Cuenta origen = banco.getCuenta(idOrigen);
                if (origen == null || origen.getUsuario().getDni() != dni) {
                    error("La cuenta de origen no te pertenece.");
                    continue;
                }
                Integer idDestino = pedirEntero("ID de la cuenta DESTINO:");
                if (idDestino == null) continue;
                Float monto = pedirFloatPositivo("Monto a transferir (> 0):");
                if (monto == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                banco.transferir(idOrigen, idDestino, monto, notas);

            } else if (op == 4) { // Ver historial
                String texto = banco.getTransacciones().stream()
                        .filter(tx ->
                                (tx.getOrigenCuenta() != null && tx.getOrigenCuenta().getUsuario().getDni() == dni) ||
                                (tx.getDestinoCuenta() != null && tx.getDestinoCuenta().getUsuario().getDni() == dni))
                        .map(Transaccion::toString)
                        .collect(java.util.stream.Collectors.joining("\n"));
                info(texto.isBlank() ? "Sin movimientos." : texto);

            } else break;
        }
    }


    //Usuarios (Admin)
    private static void menuUsuarios() {
        String[] opciones = {"Añadir", "Eliminar", "Modificar", "Ver todos", "Volver"};
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                    null, "Operaciones de Usuarios", "Usuarios",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, opciones, opciones[0]
            );
            if (opcion == 0) { // Añadir
                Integer dni = Validaciones.IngresarInt("DNI del usuario:");
                String nombre = Validaciones.IngresarString("Nombre:");
                String apellido = Validaciones.IngresarString("Apellido:");
                int tipo = Validaciones.IngresarInt("Tipo de usuario:\n1 = Administrador\n2 = Cliente\n3 = Empleado");
                while (tipo != 1 && tipo != 2 && tipo != 3){
                    tipo = Validaciones.IngresarInt("Tipo inválido. Ingrese 1 (Admin) o 2 (Cliente) o 3 (Empleado):");
                }
                String password = Validaciones.IngresarString("Password:");
                String notas = JOptionPane.showInputDialog("Notas (opcional):");
                try {
                    banco.crearUsuario(dni, tipo, nombre, apellido, password, notas);
                    info("Usuario creado.");
                } catch (Exception e) { error(e.getMessage()); }
            } else if (opcion == 1) { // Eliminar
                Integer dni = pedirEntero("DNI del usuario a eliminar:");
                if (dni == null) continue;
                try {
                    boolean ok = banco.eliminarUsuario(dni);
                    info(ok ? "Usuario eliminado." : "No existe usuario con ese DNI.");
                } catch (Exception e) { error(e.getMessage()); }
            } else if (opcion == 2) { // Modificar
                Integer dni = pedirEntero("DNI del usuario a modificar:");
                if (dni == null) continue;
                String strTipo = pedirTextoOpcional("Nuevo tipo (1=Admin, 2=Cliente, 3=Empleado) o vacío:");
                Integer nuevoTipo = null;
                if (strTipo != null && !strTipo.isBlank()) {
                    try { nuevoTipo = Integer.parseInt(strTipo.trim()); }
                    catch (Exception e) { error("Tipo inválido."); continue; }
                }
                String nuevoNombre = pedirTextoOpcional("Nuevo nombre (vacío = no cambiar):");
                String nuevoApellido = pedirTextoOpcional("Nuevo apellido (vacío = no cambiar):");
                String nuevoPassword = pedirTextoOpcional("Nuevo password (vacío = no cambiar):");
                String nuevasNotas = pedirTextoOpcional("Nuevas notas (opcional):");
                boolean ok = banco.modificarUsuario(dni, nuevoTipo,
                        vacioANull(nuevoNombre), vacioANull(nuevoApellido),
                        vacioANull(nuevoPassword), nuevasNotas);
                info(ok ? "Usuario modificado." : "Usuario no encontrado.");
            } else if (opcion == 3) { // Ver todos
                String texto = banco.getUsuarios().isEmpty()
                        ? "No hay usuarios."
                        : banco.getUsuarios().stream()
                        .map(u -> u.toString() + " | Tipo: " +
                                (u.getTipo() == Usuario.TIPO_ADMINISTRADOR ? "Admin" : (u.getTipo() == Usuario.TIPO_EMPLEADO ? "Empleado" : "Cliente")))
                        .collect(Collectors.joining("\n"));
                info(texto);
            } else break;
        }
    }
    
   
  //Monedas (Admin)
    private static void menuMonedasAdmin() {
        String[] opciones = {"Añadir", "Eliminar", "Modificar", "Ver todos", "Volver"};
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                    null, "Operaciones de Monedas", "Monedas",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, opciones, opciones[0]
            );
            if (opcion == 0) { // Añadir
                Integer id = pedirEntero("ID de moneda:");
                if (id == null) continue;

                String nombre = Validaciones.IngresarString("Nombre de moneda:");
                String notas = pedirTextoOpcional("Notas (opcional):");
                Float currentRate = pedirFloatPositivo("Introduzca la tasa de cambio actual (respecto de la moneda base):");
                if (currentRate == null) continue;

                try {
                    banco.crearCurrency(id, currentRate, nombre, notas);
                    info("Moneda creada.");
                } catch (Exception e) {
                    error(e.getMessage());
                }

            } else if (opcion == 1) { // Eliminar
                Integer id = pedirEntero("ID de moneda para eliminar:");
                if (id == null) continue;
                try {
                    boolean ok = banco.eliminarCurrency(id);
                    info(ok ? "Moneda eliminada." : "No existe moneda con ese id.");
                } catch (Exception e) {
                    error(e.getMessage());
                }

            } else if (opcion == 2) { // Modificar
                Integer id = pedirEntero("ID de la moneda a modificar:");
                if (id == null) continue;

                String strTasa = pedirTextoOpcional("Nueva tasa (vacío = no cambiar):");
                Float nuevaTasa = null;
                if (strTasa != null && !strTasa.isBlank()) {
                    try {
                        nuevaTasa = Float.parseFloat(strTasa.trim());
                    } catch (Exception e) {
                        error("Tasa inválida.");
                        continue;
                    }
                }
                String nuevasNotas = pedirTextoOpcional("Nuevas notas (opcional):");

                boolean ok = banco.modificarCurrency(id, nuevaTasa, nuevasNotas);
                info(ok ? "Moneda modificada." : "Moneda no encontrada.");

            } else if (opcion == 3) { // Ver todos
                String texto = banco.getMonedas().isEmpty()
                        ? "No hay monedas."
                        : banco.getMonedas().stream()
                            .map(Currency::toString)
                            .collect(java.util.stream.Collectors.joining("\n"));
                info(texto);
            } else {
                break;
            }
        }
    }

    
    
    
    

  //Usuarios (Empleado)
    private static void menuUsuariosEmpleado() {
        String[] opciones = { "Ver todos", "Volver"};
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                    null, "Operaciones de Usuarios", "Usuarios",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, opciones, opciones[0]
            );
            if (opcion == 0) { // Ver todos
                String texto = banco.getUsuarios().isEmpty()
                        ? "No hay usuarios."
                        : banco.getUsuarios().stream()
                        .map(u -> u.toString() + " | Tipo: " +
                                (u.getTipo() == Usuario.TIPO_ADMINISTRADOR ? "Admin" :
                                 (u.getTipo() == Usuario.TIPO_EMPLEADO ? "Empleado" : "Cliente")))
                        .collect(java.util.stream.Collectors.joining("\n"));
                info(texto);
            } else {
                break;
            }
        }
    }
    
    private static void menuProductos() {
        String[] opciones = {"Crear préstamo", "Crear inversión", "Crear servicio", "Volver"};
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                    null, "Productos bancarios", "Productos",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, opciones, opciones[0]
            );
            if (opcion == 0) { // préstamo
                Integer dni = pedirEntero("DNI del titular del préstamo:");
                if (dni == null) continue;
                Integer tipo = pedirEntero("Tipo de préstamo:\n20 = Particular\n21 = Especial");
                if (tipo == null) continue;
                Float monto = pedirFloatPositivo("Monto del préstamo:");
                if (monto == null) continue;
                Integer dias = pedirEntero("Plazo en días:");
                if (dias == null) continue;
                Float tasa = pedirFloatPositivo("Tasa anual (ej: 0.50 = 50%):");
                if (tasa == null) continue;
                Integer currencyId = pedirEntero("ID de moneda:");
                if (currencyId == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                try {
                    Prestamo p = banco.crearPrestamo(dni, tipo, monto, dias, tasa, notas, currencyId);
                    info("Préstamo creado: " + p);
                } catch (Exception e) { error(e.getMessage()); }

            } else if (opcion == 1) { // inversión
                Integer dni = pedirEntero("DNI del titular de la inversión:");
                if (dni == null) continue;
                Integer tipo = pedirEntero("Tipo de inversión:\n30 = Particular\n31 = Especial");
                if (tipo == null) continue;
                Float monto = pedirFloatPositivo("Monto a invertir:");
                if (monto == null) continue;
                Integer dias = pedirEntero("Plazo en días:");
                if (dias == null) continue;
                Float tasa = pedirFloatPositivo("Tasa anual (ej: 0.50 = 50%):");
                if (tasa == null) continue;
                Integer currencyId = pedirEntero("ID de moneda:");
                if (currencyId == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                try {
                    Inversion inv = banco.crearInversion(dni, tipo, monto, dias, tasa, notas, currencyId);
                    info("Inversión creada: " + inv);
                } catch (Exception e) { error(e.getMessage()); }

            } else if (opcion == 2) { // servicio
                Integer dni = pedirEntero("DNI del titular de la cuenta que pagará el servicio:");
                if (dni == null) continue;
                Integer tipo = pedirEntero("Tipo servicio:\n10 = Particular\n11 = One-time\n12 = Recurrente");
                if (tipo == null) continue;
                String nombreServ = pedirTexto("Nombre del servicio:");
                Integer currencyId = pedirEntero("ID de moneda:");
                if (currencyId == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                try {
                    Servicio s = banco.crearServicio(dni, tipo, nombreServ, notas, currencyId);
                    info("Servicio creado: " + s);
                } catch (Exception e) { error(e.getMessage()); }

            } else {
                break;
            }
        }
    }


    
    
    
    //Cuentas (Admin)
    private static void menuCuentasAdmin() {
        String[] opciones = {"Añadir", "Eliminar", "Modificar", "Ver por usuario", "Ver todas", "Volver"};
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                    null, "Operaciones de Cuentas", "Cuentas",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, opciones, opciones[0]
            );
            if (opcion == 0) {
                Integer dni = pedirEntero("DNI del titular:");
                if (dni == null) continue;

                Integer tipo = pedirEntero("Tipo de cuenta:\n1 = Caja de ahorro\n2 = Cuenta corriente");
                if (tipo == null) continue;

                Float saldoInicial = pedirFloatNoNeg("Saldo inicial (>= 0):");
                if (saldoInicial == null) continue;

                // para simplificar, mostramos los IDs que usamos en PopulateInitialData
                Integer currencyId = pedirEntero("ID de moneda:\n1 = ARS\n2 = USD\n3 = EUR\n4 = BRL");
                if (currencyId == null) continue;

                String notas = pedirTextoOpcional("Notas (opcional):");
                try {
                    Cuenta c = banco.crearCuenta(dni, tipo, saldoInicial, notas, currencyId);
                    info("Cuenta creada: " + c);
                } catch (Exception e) {
                    error(e.getMessage());
                }
            

            } else if (opcion == 1) {
                Integer id = pedirEntero("ID de la cuenta a eliminar (saldo debe ser 0):");
                if (id == null) continue;
                try {
                    boolean ok = banco.eliminarCuenta(id);
                    info(ok ? "Cuenta eliminada." : "No existe cuenta con ese ID.");
                } catch (Exception e) { error(e.getMessage()); }
            } else if (opcion == 2) {
                Integer id = pedirEntero("ID de la cuenta a modificar:");
                if (id == null) continue;
                String strTipo = pedirTextoOpcional("Nuevo tipo (1 o 2) o vacío:");
                Integer nuevoTipo = null;
                if (strTipo != null && !strTipo.isBlank()) {
                    try { nuevoTipo = Integer.parseInt(strTipo.trim()); }
                    catch (Exception e) { error("Tipo inválido."); continue; }
                }
                String nuevasNotas = pedirTextoOpcional("Nuevas notas (opcional):");
                boolean ok = banco.modificarCuenta(id, nuevoTipo, nuevasNotas);
                info(ok ? "Cuenta modificada." : "Cuenta no encontrada.");
            } else if (opcion == 3) {
                Integer dni = pedirEntero("DNI del titular:");
                if (dni == null) continue;
                List<Cuenta> lista = banco.getCuentasPorUsuario(dni);
                String texto = lista.isEmpty() ? "Sin cuentas para ese DNI."
                        : lista.stream().map(Cuenta::toString).collect(Collectors.joining("\n"));
                info(texto);
            } else if (opcion == 4) {
                String texto = banco.getTodasLasCuentas().isEmpty()
                        ? "No hay cuentas."
                        : banco.getTodasLasCuentas().stream().map(Cuenta::toString).collect(Collectors.joining("\n"));
                info(texto);
            } else break;
        }
    }

    //Transacciones (Admin)
    private static void menuTransaccionesAdmin() {
        String[] opciones = {"Depósito", "Retiro", "Transferencia", "Ver historial", "Volver"};
        while (true) {
            int op = JOptionPane.showOptionDialog(
                    null, "Operaciones de Transacciones", "Transacciones",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, opciones, opciones[0]
            );
            if (op == 0) {
                Integer id = pedirEntero("ID de la cuenta destino:");
                if (id == null) continue;
                Float monto = pedirFloatPositivo("Monto a depositar (> 0):");
                if (monto == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                banco.depositar(id, monto, notas);
            } else if (op == 1) {
                Integer id = pedirEntero("ID de la cuenta origen:");
                if (id == null) continue;
                Float monto = pedirFloatPositivo("Monto a retirar (> 0):");
                if (monto == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                banco.retirar(id, monto, notas);
            } else if (op == 2) {
                Integer idOrigen = pedirEntero("ID de la cuenta ORIGEN:");
                if (idOrigen == null) continue;
                Integer idDestino = pedirEntero("ID de la cuenta DESTINO:");
                if (idDestino == null) continue;
                Float monto = pedirFloatPositivo("Monto a transferir (> 0):");
                if (monto == null) continue;
                String notas = pedirTextoOpcional("Notas (opcional):");
                banco.transferir(idOrigen, idDestino, monto, notas);
            } else if (op == 3) {
                String texto = banco.getTransacciones().isEmpty()
                        ? "No hay transacciones."
                        : banco.getTransacciones().stream().map(Transaccion::toString).collect(Collectors.joining("\n"));
                info(texto);
            } else break;
        }
    }

    //Utilidades 
    private static void info(String msg) { JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
    private static void error(String msg) { JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE); }

    private static String pedirTexto(String prompt) {
        String s = JOptionPane.showInputDialog(null, prompt);
        if (s == null || s.isBlank()) return null;
        return s.trim();
    }

    private static String pedirTextoOpcional(String prompt) {
        String s = JOptionPane.showInputDialog(null, prompt);
        if (s == null) return null;
        return s.trim();
    }

    private static Integer pedirEntero(String prompt) {
        String s = JOptionPane.showInputDialog(null, prompt);
        if (s == null) return null;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { error("Debe ingresar un número entero."); return null; }
    }

    private static Float pedirFloatPositivo(String prompt) {
        String s = JOptionPane.showInputDialog(null, prompt);
        if (s == null) return null;
        try {
            float v = Float.parseFloat(s.trim());
            if (v <= 0) { error("El valor debe ser > 0."); return null; }
            return v;
        } catch (NumberFormatException e) { error("Debe ingresar un número válido."); return null; }
    }

    private static Float pedirFloatNoNeg(String prompt) {
        String s = JOptionPane.showInputDialog(null, prompt);
        if (s == null) return null;
        try {
            float v = Float.parseFloat(s.trim());
            if (v < 0) { error("El valor debe ser >= 0."); return null; }
            return v;
        } catch (NumberFormatException e) { error("Debe ingresar un número válido."); return null; }
    }

    private static String vacioANull(String s) { return (s == null || s.isBlank()) ? null : s; }
}
