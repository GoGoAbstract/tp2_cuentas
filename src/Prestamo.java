package pr2_cuentas;

public class Prestamo extends Cuenta {

    public static final int TIPO_PRESTAMO_PARTICULAR = 20;
    public static final int TIPO_PRESTAMO_ESPECIAL = 21;

    private int prestamoDays;
    private float prestamoMonto;     // capital original
    private float prestamoTasa;      // tasa anual (0.50 = 50%)

    public Prestamo(int id,
                    Usuario usuario,
                    int tipo,
                    float monto,
                    int dias,
                    float tasaAnual,
                    String notas,
                    Currency currency) {

        // el saldo de la cuenta préstamo representa capital pendiente
        super(id, usuario, tipo, monto, notas, currency);
        setPrestamoMonto(monto);
        setPrestamoDays(dias);
        setPrestamoTasa(tasaAnual);
    }

    public int getPrestamoDays() { return prestamoDays; }
    public void setPrestamoDays(int prestamoDays) { this.prestamoDays = prestamoDays; }

    public float getPrestamoMonto() { return prestamoMonto; }
    public void setPrestamoMonto(float prestamoMonto) { this.prestamoMonto = prestamoMonto; }

    public float getPrestamoTasa() { return prestamoTasa; }
    public void setPrestamoTasa(float prestamoTasa) { this.prestamoTasa = prestamoTasa; }

    // Interés total aproximado simple: capital * tasa * días / 365 
    public float calcularInteresTotal() {
        return prestamoMonto * prestamoTasa * prestamoDays / 365f;
    }

    @Override
    public String toString() {
        return super.toString() +
               " | Monto préstamo: " + String.format("%.2f", prestamoMonto) +
               " | Días: " + prestamoDays +
               " | Tasa anual: " + String.format("%.2f%%", prestamoTasa * 100);
    }
}
