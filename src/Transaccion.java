package pr2_cuentas;

import java.time.LocalDate;

public class Transaccion {
    public static final int TIPO_DEPOSITO = 1;
    public static final int TIPO_RETIRO = 2;
    public static final int TIPO_TRANSFERENCIA = 3;

    private int id;
    private int tipo;
    private Cuenta origenCuenta;   // puede ser null para depósito
    private Cuenta destinoCuenta;  // puede ser null para retiro
    private float valor;
    private String notas;
    private LocalDate transaccionDate;
   //when transaccion proceeds between cuentas with different currencies,
    //the conversion between currenncies using current rate should be proceeded.
    public Transaccion(int id, int tipo, Cuenta origenCuenta,
                       Cuenta destinoCuenta, float valor, String notas) 
    {
        setId(id);
        setTipo(tipo);
        setOrigenCuenta(origenCuenta);
        setDestinoCuenta(destinoCuenta);
        setValor(valor);
        setNotas(notas);
        setTransaccionDate(LocalDate.now());
    }

    public int getId() 
    { 
    	return id; 
    }
    
    public void setId(int id) 
    { 
    	this.id = id; 
    }

    public int getTipo() 
    { 
    	return tipo; 
    }
    
    public void setTipo(int tipo) 
    { 
    	this.tipo = tipo; 
    }

    public Cuenta getOrigenCuenta() 
    { 
    	return origenCuenta; 
    }
    
    public void setOrigenCuenta(Cuenta origenCuenta) 
    { 
    	this.origenCuenta = origenCuenta; 
    }

    public Cuenta getDestinoCuenta() 
    { 
    	return destinoCuenta; 
    }
    
    public void setDestinoCuenta(Cuenta destinoCuenta) 
    { 
    	this.destinoCuenta = destinoCuenta; 
    }

    public float getValor() 
    { 
    	return valor; 
    }
    
    public void setValor(float valor) 
    { 
    	this.valor = valor; 
    }

    public String getNotas() 
    { 
    	return notas; 
    }
    
    public void setNotas(String notas) 
    { 
    	this.notas = notas; 
    }

    public LocalDate getTransaccionDate() 
    { 
    	return transaccionDate; 
    }
    
    public void setTransaccionDate(LocalDate transaccionDate) 
    { 
    	this.transaccionDate = transaccionDate; 
    }

    @Override
    public String toString() 
    {
        String tipoStr = switch (tipo) {
            case TIPO_DEPOSITO -> "Depósito";
            case TIPO_RETIRO -> "Retiro";
            case TIPO_TRANSFERENCIA -> "Transferencia";
            default -> "Desconocido";
        };
        return "Tx #" + id + " [" + tipoStr + "] " + valor +
               " | Fecha: " + transaccionDate +
               (origenCuenta != null ? " | Origen: #" + origenCuenta.getId() : "") +
               (destinoCuenta != null ? " | Destino: #" + destinoCuenta.getId() : "") +
               (notas != null && !notas.isBlank() ? " | " + notas : "");
    }
}