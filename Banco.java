package pr2_cuentas;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class Banco {
    private Map<Integer, Usuario> usuarios = new HashMap<>();
    private Map<Integer, Cuenta> cuentas = new HashMap<>();
    private Map<Integer, Currency> monedas = new HashMap<>();
    private List<Transaccion> transacciones = new ArrayList<>();

    private int nextCuentaId = 1;
    private int nextTransaccionId = 1;


    // USUARIOS

    public Usuario crearUsuario(int dni, int tipo, String nombre, String apellido, String password, String notas) {
        if (usuarios.containsKey(dni))
            throw new IllegalArgumentException("Ya existe un usuario con ese DNI.");
        Usuario currentUsuario = new Usuario(dni, tipo, nombre, apellido, password, notas);
        usuarios.put(dni, currentUsuario);
        return currentUsuario;
    }

    public boolean eliminarUsuario(int dni) {
        if (!usuarios.containsKey(dni)) return false;
        boolean tieneCuentas = cuentas.values().stream().anyMatch(c -> c.getUsuario().getDni() == dni);
        if (tieneCuentas)
            throw new IllegalStateException("No se puede eliminar: el usuario tiene cuentas.");
        usuarios.remove(dni);
        return true;
    }

    public Usuario getUsuario(int dni) { return usuarios.get(dni); }
    public Collection<Usuario> getUsuarios() { return usuarios.values(); }

    public boolean modificarUsuario(int dni, Integer nuevoTipo, String nuevoNombre, String nuevoApellido,
                                    String nuevoPassword, String nuevasNotas) {
        Usuario currentUsuario = usuarios.get(dni);
        if (currentUsuario == null) return false;
        if (nuevoTipo != null) currentUsuario.setTipo(nuevoTipo);
        if (nuevoNombre != null && !nuevoNombre.isBlank()) currentUsuario.setNombre(nuevoNombre);
        if (nuevoApellido != null && !nuevoApellido.isBlank()) currentUsuario.setApellido(nuevoApellido);
        if (nuevoPassword != null && !nuevoPassword.isBlank()) currentUsuario.setPassword(nuevoPassword);
        if (nuevasNotas != null) currentUsuario.setNotas(nuevasNotas);
        return true;
    }

    //Login / autenticación
    public Usuario autenticar(int dni, String password) {
        Usuario u = usuarios.get(dni);
        if (u != null && Objects.equals(u.getPassword(), password)) return u;
        return null;
    }


    // MONEDAS
    
    public Currency crearCurrency(int currencyId, float currentRate, String nombre, String notas) {
        if (monedas.containsKey(currencyId))
            throw new IllegalArgumentException("Ya existe una moneda con ese id.");
        Currency currentMoneda = new Currency(currencyId, currentRate, nombre, notas, LocalDateTime.now());
        monedas.put(currencyId, currentMoneda);
        return currentMoneda;
    }

    public boolean eliminarCurrency(int currencyId) {
        Currency moneda = monedas.get(currencyId);
        if (moneda == null) return false;

        boolean usada = cuentas.values().stream()
                .anyMatch(c -> c.getCurrency() != null &&
                               c.getCurrency().getCurrencyId() == currencyId);
        if (usada)
            throw new IllegalStateException("La moneda está siendo usada por alguna cuenta.");

        monedas.remove(currencyId);
        return true;
    }
    
    public boolean modificarCurrency(int currencyId, Float nuevaTasa, String nuevasNotas) {
        Currency moneda = monedas.get(currencyId);
        if (moneda == null) return false;

        if (nuevaTasa != null) {
            moneda.setCurrentRate(nuevaTasa);
            moneda.setRateDateTime(LocalDateTime.now());
        }
        if (nuevasNotas != null) {
            moneda.setNotas(nuevasNotas);
        }
        return true;
    }

    public Currency getCurrency(int currencyId) { return monedas.get(currencyId); }
    public Collection<Currency> getMonedas() { return monedas.values(); }


    //CUENTAS

    public Cuenta crearCuenta(int dni, int tipo, float saldoInicial, String notas, int currencyId) {
        Usuario currentUsuario = usuarios.get(dni);
        if (currentUsuario == null)
            throw new IllegalArgumentException("No existe usuario con ese DNI.");
        Currency moneda = monedas.get(currencyId);
        if (moneda == null)
            throw new IllegalArgumentException("No existe moneda con ese id.");

        int id = nextCuentaId++;
        Cuenta currentCuenta = new Cuenta(id, currentUsuario, tipo, saldoInicial, notas, moneda);
        cuentas.put(id, currentCuenta);
        return currentCuenta;
    }

    public boolean eliminarCuenta(int cuentaId) {
        Cuenta currentCuenta = cuentas.get(cuentaId);
        if (currentCuenta == null) return false;
        if (currentCuenta.getSaldo() != 0f)
            throw new IllegalStateException("La cuenta debe tener saldo 0 para eliminarse.");
        cuentas.remove(cuentaId);
        return true;
    }

    public Cuenta getCuenta(int cuentaId) { return cuentas.get(cuentaId); }

    public List<Cuenta> getCuentasPorUsuario(int dni) {
        return cuentas.values().stream()
                .filter(currentCuenta -> currentCuenta.getUsuario().getDni() == dni)
                .sorted(Comparator.comparingInt(Cuenta::getId))
                .collect(Collectors.toList());
    }

    public Collection<Cuenta> getTodasLasCuentas() { return cuentas.values(); }

    public boolean modificarCuenta(int cuentaId, Integer nuevoTipo, String nuevasNotas) {
        Cuenta currentCuenta = cuentas.get(cuentaId);
        if (currentCuenta == null) return false;
        if (nuevoTipo != null) currentCuenta.setTipo(nuevoTipo);
        if (nuevasNotas != null) currentCuenta.setNotas(nuevasNotas);
        return true;
    }

  
    //PRESTAMOS
 
    public Prestamo crearPrestamo(int dni, int tipoPrestamo, float monto,
                                  int dias, float tasaAnual, String notas, int currencyId) {
        Usuario currentUsuario = usuarios.get(dni);
        if (currentUsuario == null)
            throw new IllegalArgumentException("No existe usuario con ese DNI.");

        Currency moneda = monedas.get(currencyId);
        if (moneda == null)
            throw new IllegalArgumentException("No existe moneda con ese id.");

        int id = nextCuentaId++;
        Prestamo prestamo = new Prestamo(id, currentUsuario, tipoPrestamo, monto, dias, tasaAnual, notas, moneda);

        
        cuentas.put(id, prestamo);
        return prestamo;
    }


    // SERVICIOS
 
    public Servicio crearServicio(int dniTitular, int tipoServicio,
                                  String nombreServicio, String notas, int currencyId) {
        Usuario titular = usuarios.get(dniTitular);
        if (titular == null)
            throw new IllegalArgumentException("No existe usuario con ese DNI.");

        Currency moneda = monedas.get(currencyId);
        if (moneda == null)
            throw new IllegalArgumentException("No existe moneda con ese id.");

        int id = nextCuentaId++;
        Servicio servicio = new Servicio(id, titular, tipoServicio, nombreServicio, notas, moneda);
        cuentas.put(id, servicio);
        return servicio;
    }
    
 // INVERSIONES

    public Inversion crearInversion(int dni, int tipoInversion, float monto,
                                    int dias, float tasaAnual, String notas, int currencyId) {
        Usuario currentUsuario = usuarios.get(dni);
        if (currentUsuario == null)
            throw new IllegalArgumentException("No existe usuario con ese DNI.");

        Currency moneda = monedas.get(currencyId);
        if (moneda == null)
            throw new IllegalArgumentException("No existe moneda con ese id.");

        int id = nextCuentaId++;
        Inversion inversion = new Inversion(id, currentUsuario, tipoInversion, monto, dias, tasaAnual, notas, moneda);

        cuentas.put(id, inversion);
        return inversion;
    }



    //OPERACIONES / TRANSACCIONES
 

    private float convertirMonto(float monto, Currency origen, Currency destino) {
        if (origen == null || destino == null) return monto;
        if (origen.getCurrencyId() == destino.getCurrencyId()) return monto;

        // Suposición: currentRate = valor de 1 unidad de esta moneda en ARS
        float enBase = monto * origen.getCurrentRate();      // origen → ARS
        return enBase / destino.getCurrentRate();            // ARS → destino
    }

    public void depositar(int cuentaId, float monto, String notas) {
        Cuenta currentCuenta = getCuenta(cuentaId);
        if (currentCuenta == null) {
            JOptionPane.showMessageDialog(null, "Cuenta no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (monto <= 0) {
            JOptionPane.showMessageDialog(null, "El monto a depositar debe ser mayor que 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        currentCuenta.depositar(monto);
        registrarTransaccion(Transaccion.TIPO_DEPOSITO, null, currentCuenta, monto, notas);
        JOptionPane.showMessageDialog(null, "Depósito exitoso. Monto depositado: " + monto, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void retirar(int cuentaId, float monto, String notas) {
        Cuenta currentCuenta = getCuenta(cuentaId);
        if (currentCuenta == null) {
            JOptionPane.showMessageDialog(null, "Cuenta no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean retiroExitoso = currentCuenta.retirar(monto);
        if (retiroExitoso) {
            registrarTransaccion(Transaccion.TIPO_RETIRO, currentCuenta, null, monto, notas);
            JOptionPane.showMessageDialog(null, "Retiro exitoso. Monto retirado: " + monto, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Saldo insuficiente para realizar el retiro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void transferir(int origenId, int destinoId, float monto, String notas) {
        if (origenId == destinoId) {
            JOptionPane.showMessageDialog(null, "Cuentas origen y destino deben ser distintas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Cuenta origen = getCuenta(origenId);
        Cuenta destino = getCuenta(destinoId);
        if (origen == null || destino == null) {
            JOptionPane.showMessageDialog(null, "Cuenta origen o destino inexistente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (monto <= 0) {
            JOptionPane.showMessageDialog(null, "El monto a transferir debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float montoDestino = monto;
        if (origen.getCurrency() != null && destino.getCurrency() != null) {
            montoDestino = convertirMonto(monto, origen.getCurrency(), destino.getCurrency());
        }

        boolean retiroExitoso = origen.retirar(monto);
        if (!retiroExitoso) {
            JOptionPane.showMessageDialog(null, "Saldo insuficiente en la cuenta de origen.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        destino.depositar(montoDestino);
        registrarTransaccion(Transaccion.TIPO_TRANSFERENCIA, origen, destino, monto, notas);

        JOptionPane.showMessageDialog(null,
                "Transferencia realizada.\nOrigen #" + origen.getId() +
                " (" + (origen.getCurrency() != null ? origen.getCurrency().getNombre() : "-") + ")" +
                " → Destino #" + destino.getId() +
                " (" + (destino.getCurrency() != null ? destino.getCurrency().getNombre() : "-") + ")" +
                "\nMonto origen: " + monto +
                "\nMonto destino: " + String.format("%.2f", montoDestino),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    //Pago de servicio (simplemente una transferencia hacia una cuenta de tipo Servicio).
    public void pagarServicio(int origenId, int servicioId, float monto, String notas) {
        Cuenta origen = getCuenta(origenId);
        Cuenta servicio = getCuenta(servicioId);

        if (origen == null || servicio == null) {
            JOptionPane.showMessageDialog(null, "Cuenta origen o servicio inexistente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!(servicio instanceof Servicio)) {
            JOptionPane.showMessageDialog(null, "La cuenta destino no es un servicio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (monto <= 0) {
            JOptionPane.showMessageDialog(null, "El monto debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float montoDestino = monto;
        if (origen.getCurrency() != null && servicio.getCurrency() != null) {
            montoDestino = convertirMonto(monto, origen.getCurrency(), servicio.getCurrency());
        }

        boolean retiroExitoso = origen.retirar(monto);
        if (!retiroExitoso) {
            JOptionPane.showMessageDialog(null, "Saldo insuficiente en la cuenta de origen.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        servicio.depositar(montoDestino);
        String notasTx = "Pago servicio: " + ((Servicio) servicio).getServicio_nombre();
        if (notas != null && !notas.isBlank()) {
            notasTx += " - " + notas;
        }
        registrarTransaccion(Transaccion.TIPO_TRANSFERENCIA, origen, servicio, monto, notasTx);

        JOptionPane.showMessageDialog(null, "Pago de servicio realizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarTransaccion(int tipo, Cuenta origen, Cuenta destino, float monto, String notas) {
        int id = nextTransaccionId++;
        transacciones.add(new Transaccion(id, tipo, origen, destino, monto, notas));
    }

    public List<Transaccion> getTransacciones() {
        return Collections.unmodifiableList(transacciones);
    }
    
    

}
