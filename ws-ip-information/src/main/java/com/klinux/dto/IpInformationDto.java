package com.klinux.dto;

import java.io.Serializable;

public class IpInformationDto implements Serializable {

	private static final long serialVersionUID = -3602790210316764323L;

	private String estado;
	private String message;
	private String countryName;
	private String isoName;
	private String currencyName;
	private String currencyValue;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getIsoName() {
		return isoName;
	}

	public void setIsoName(String isoName) {
		this.isoName = isoName;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCurrencyValue() {
		return currencyValue;
	}

	public void setCurrencyValue(String currencyValue) {
		this.currencyValue = currencyValue;
	}

	@Override
	public String toString() {
		return "IpInformationDto [estado=" + estado + ", message=" + message + ", countryName=" + countryName
				+ ", isoName=" + isoName + ", currencyName=" + currencyName + ", currencyValue=" + currencyValue + "]";
	}

}