package pr2_cuentas;

public class Inversion extends Cuenta {
    public static final int TIPO_INVERSION_PARTICULAR = 30;
    public static final int TIPO_INVERSION_ESPECIAL   = 31;

    private int inversionDays;
    private float inversionMonto;   // capital original
    private float inversionTasa;    // tasa anual (0.50 = 50%)

    public Inversion(int id,
                     Usuario usuario,
                     int tipo,
                     float monto,
                     int dias,
                     float tasaAnual,
                     String notas,
                     Currency currency) {

        super(id, usuario, tipo, monto, notas, currency);
        setInversionMonto(monto);
        setInversionDays(dias);
        setInversionTasa(tasaAnual);
    }

    public int getInversionDays() {
        return inversionDays;
    }

    public void setInversionDays(int inversionDays) {
        this.inversionDays = inversionDays;
    }

    public float getInversionMonto() {
        return inversionMonto;
    }

    public void setInversionMonto(float inversionMonto) {
        this.inversionMonto = inversionMonto;
    }

    public float getInversionTasa() {
        return inversionTasa;
    }

    public void setInversionTasa(float inversionTasa) {
        this.inversionTasa = inversionTasa;
    }

    //Interés total aproximado: capital * tasa * días / 365
    public float calcularInteresTotal() {
        return inversionMonto * inversionTasa * inversionDays / 365f;
    }

    @Override
    public String toString() {
        return super.toString() +
               " | Monto inversión: " + String.format("%.2f", inversionMonto) +
               " | Días: " + inversionDays +
               " | Tasa anual: " + String.format("%.2f%%", inversionTasa * 100);
    }
}
