package pr2_cuentas;

import javax.swing.JOptionPane;

public class Cuenta {
    public static final int TIPO_CAJA_AHORRO = 1;
    public static final int TIPO_CUENTA_CORRIENTE = 2;

    private int id;
    private Usuario usuario;
    private int tipo;     // incluye caja, cta cte, préstamos, servicios, etc.
    private float saldo;
    private String notas;
    private Currency currency;

    public Cuenta(int id, Usuario usuario, int tipo, float saldoInicial, String notas, Currency currency) {
        setId(id);
        setUsuario(usuario);
        setTipo(tipo);
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo.");
        }
        this.saldo = saldoInicial;
        setNotas(notas);
        setCurrency(currency);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Usuario requerido.");
        this.usuario = usuario;
    }

    public void setTipo(int tipo) {
        // Aceptamos también tipos de préstamo, servicio e inversión
        if (tipo != TIPO_CAJA_AHORRO &&
            tipo != TIPO_CUENTA_CORRIENTE &&
            tipo != Prestamo.TIPO_PRESTAMO_PARTICULAR &&
            tipo != Prestamo.TIPO_PRESTAMO_ESPECIAL &&
            tipo != Servicio.TIPO_SERVICIO_PARTICULAR &&
            tipo != Servicio.TIPO_SERVICIO_ONE_TIME &&
            tipo != Servicio.TIPO_SERVICIO_RERCORRIDO &&
            tipo != Inversion.TIPO_INVERSION_PARTICULAR &&
            tipo != Inversion.TIPO_INVERSION_ESPECIAL) {

            throw new IllegalArgumentException("Tipo de cuenta inválido.");
        }
        this.tipo = tipo;
    }

    public float getSaldo() { return saldo; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    // Depositar dinero (monto > 0)
    public boolean depositar(float monto) {
        if (monto <= 0) {
            JOptionPane.showMessageDialog(null, "El monto a depositar debe ser > 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        this.saldo += monto;
        return true;
    }

    // Retirar dinero (monto > 0 y <= saldo)
    public boolean retirar(float monto) {
        if (monto <= 0) {
            JOptionPane.showMessageDialog(null, "El monto a retirar debe ser mayor que 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (monto > this.saldo) {
            JOptionPane.showMessageDialog(null, "Saldo insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        this.saldo -= monto;
        return true;
    }

    // Transferir
    public boolean transferir(Cuenta destino, float monto) {
        if (destino == null) {
            JOptionPane.showMessageDialog(null, "Cuenta destino requerida.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (destino == this) {
            JOptionPane.showMessageDialog(null, "No se puede transferir a la misma cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (monto <= 0) {
            JOptionPane.showMessageDialog(null, "El monto a transferir debe ser mayor que 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean retiroExitoso = this.retirar(monto);
        if (!retiroExitoso) {
            JOptionPane.showMessageDialog(null, "Saldo insuficiente para realizar la transferencia.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        destino.depositar(monto);
        JOptionPane.showMessageDialog(null, "Transferencia exitosa. Monto transferido: " + monto, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    @Override
    public String toString() {
        String tipoStr;
        switch (tipo) {
            case TIPO_CAJA_AHORRO -> tipoStr = "Caja de ahorro";
            case TIPO_CUENTA_CORRIENTE -> tipoStr = "Cuenta corriente";
            case Prestamo.TIPO_PRESTAMO_PARTICULAR -> tipoStr = "Préstamo particular";
            case Prestamo.TIPO_PRESTAMO_ESPECIAL -> tipoStr = "Préstamo especial";
            case Servicio.TIPO_SERVICIO_PARTICULAR -> tipoStr = "Servicio particular";
            case Servicio.TIPO_SERVICIO_ONE_TIME -> tipoStr = "Servicio único";
            case Servicio.TIPO_SERVICIO_RERCORRIDO -> tipoStr = "Servicio recurrente";
            case Inversion.TIPO_INVERSION_PARTICULAR -> tipoStr = "Inversion particular";
            case Inversion.TIPO_INVERSION_ESPECIAL -> tipoStr = "Inversion especial";
            default -> tipoStr = "Desconocido";
        }

        String monedaStr = (currency != null ? currency.getNombre() : "-");

        return "Cuenta #" + id + " [" + tipoStr + "]" +
               " | Moneda: " + monedaStr +
               " | DNI: " + usuario.getDni() +
               " | Titular: " + usuario.getNombre() + " " + usuario.getApellido() +
               " | Saldo: " + String.format("%.2f", saldo) +
               (notas != null && !notas.isBlank() ? " | " + notas : "");
    }
}
