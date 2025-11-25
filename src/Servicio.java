package pr2_cuentas;

public class Servicio extends Cuenta {
    // Cuenta de un tercero (empresa de servicios)

    public static final int TIPO_SERVICIO_PARTICULAR = 10;
    public static final int TIPO_SERVICIO_ONE_TIME = 11;
    public static final int TIPO_SERVICIO_RERCORRIDO = 12;

    private String servicio_nombre;

    public Servicio(int id,
                    Usuario usuario,
                    int tipo,
                    String servicio_nombre,
                    String notas,
                    Currency currency) {

        // saldo 0, porque no nos interesa saldo inicial de la empresa
        super(id, usuario, tipo, 0f, notas, currency);
        this.servicio_nombre = servicio_nombre;
    }

    public String getServicio_nombre() {
        return servicio_nombre;
    }

    public void setServicio_nombre(String servicio_nombre) {
        this.servicio_nombre = servicio_nombre;
    }

    @Override
    public String toString() {
        return super.toString() + " | Servicio: " + servicio_nombre;
    }
}
