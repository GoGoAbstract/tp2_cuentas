package pr2_cuentas;

public class Usuario {

    public static final int TIPO_ADMINISTRADOR = 1;
    public static final int TIPO_EMPLEADO = 3;
    public static final int TIPO_CLIENTE_PARTICULAR = 2;

    private int dni;
    private int tipo;
    private String nombre;
    private String apellido;
    private String password;
    private String email;
    private String notas;

    public Usuario(int dni, int tipo, String nombre, String apellido, String password, String notas) {
        setDni(dni);
        setTipo(tipo);
        setNombre(nombre);
        setApellido(apellido);
        setPassword(password);
        setNotas(notas);
    }

    public int getDni() { return dni; }
    public void setDni(int dni) { this.dni = dni; }

    public int getTipo() { return tipo; }
    public void setTipo(int tipo) { this.tipo = tipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
    
    @Override
    public String toString() {
        return "DNI: " + dni + " | " + nombre + " " + apellido;
    }

    public String details() {
        return "DNI: " + dni + " | " + nombre + " " + apellido +
               (notas != null && !notas.isBlank() ? " | " + notas : "");
    }


}
