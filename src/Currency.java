package pr2_cuentas;

import java.time.LocalDateTime;

public class Currency {
	private int currencyId;
	private float currentRate;
	private LocalDateTime rateDateTime;
	private String nombre;
	private String notas;
	
	public Currency(int currencyId, float currentRate,  String nombre, String notas, LocalDateTime rateDateTime) 
	{
		setCurrencyId(currencyId);
		setCurrentRate(currentRate);
		setNombre(nombre);
		setNotas(notas);
		setRateDateTime(rateDateTime);
	}
	
	public int getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}
	public float getCurrentRate() {
		return currentRate;
	}
	public void setCurrentRate(float currentRate) {
		this.currentRate = currentRate;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getNotas() {
		return notas;
	}
	public void setNotas(String notas) {
		this.notas = notas;
	}

	public LocalDateTime getRateDateTime() {
		return rateDateTime;
	}

	public void setRateDateTime(LocalDateTime rateDateTime) {
		this.rateDateTime = rateDateTime;
	}
	
	
	
	@Override
	public String toString() {
	    return "Moneda #" + currencyId +
	           " | " + nombre +
	           " | Tasa actual: " + currentRate +
	           " | Última actualización: " + rateDateTime +
	           (notas != null && !notas.isBlank() ? " | " + notas : "");
	}


}

