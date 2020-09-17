package com.klinux.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klinux.clients.BanIpClientRest;
import com.klinux.clients.ConversionClientRest;
import com.klinux.clients.CountryClientRest;
import com.klinux.clients.CurrencyClientRest;
import com.klinux.constants.Constantes;
import com.klinux.dto.CountryDto;
import com.klinux.dto.IpInformationDto;

import feign.FeignException.NotFound;

@Service
public class IpInformationServiceImpl implements IpInformationService {

	private static Logger log = LoggerFactory.getLogger(IpInformationServiceImpl.class);

	@Autowired
	private BanIpClientRest banIpClient;

	@Autowired
	private CountryClientRest countryClient;

	@Autowired
	private CurrencyClientRest currencyClient;

	@Autowired
	private ConversionClientRest conversionClient;

	private IpInformationDto response;

	@Async
	public CompletableFuture<IpInformationDto> getIpInformation(String ip) {
		log.info("Name: " + Thread.currentThread().getName());
		try {
			response = new IpInformationDto();
			requestIpTypeFromBanIpClient(ip);
		} catch (Exception e) {
			log.error(printError(e));
			response.setEstado(Constantes.Error);
			response.setMessage(printError(e));
		}
		return CompletableFuture.completedFuture(response);
	}

	private void requestIpTypeFromBanIpClient(String ip) throws JsonMappingException, JsonProcessingException {
		String typeIp = banIpClient.getIpStatus(ip);
		if (typeIp != null) {
			evaluateTypeIp(typeIp, ip);
		}
	}

	private void evaluateTypeIp(String typeIp, String ip) throws JsonMappingException, JsonProcessingException {
		if (typeIp.equals(Constantes.ENABLED)) {
			requestFromCountryDetailClient(ip);
		}
		if (typeIp.equals(Constantes.BANNED)) {
			response.setEstado(Constantes.STATUS_SUCCESS);
			response.setMessage(Constantes.IP_IS_BAN);
		}
	}

	private void requestFromCountryDetailClient(String ip) throws JsonMappingException, JsonProcessingException {
		CountryDto country = countryClient.getCountryDetail(ip);
		if (country != null) {
			response.setCountryName(country.getCountryName());
			response.setIsoName(country.getCountryCode3());
			requestCurrencyByCountryName(country.getCountryName());
		}
	}

	private void requestCurrencyByCountryName(String countryName) throws JsonMappingException, JsonProcessingException {
		String jsonCurrency = currencyClient.getCurrencyByCountryName(countryName);
		if (jsonCurrency != null) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(jsonCurrency);
			String currencyCode = jsonNode.get(0).get("currencies").get(0).get("code").asText();
			response.setCurrencyName(currencyCode);
			requestCurrencyDetail(currencyCode);
		}
	}

	private void requestCurrencyDetail(String currencyCode) throws JsonMappingException, JsonProcessingException {
		String jsonConversion = conversionClient.getCurrencyDetail(currencyCode);
		if (jsonConversion != null) {
			ObjectMapper mapperConversion = new ObjectMapper();
			JsonNode jsonNodeConversion = mapperConversion.readTree(jsonConversion);
			String rate = jsonNodeConversion.get("rates").get(currencyCode).asText();
			response.setCurrencyValue(rate);
			response.setEstado(Constantes.STATUS_SUCCESS);
			response.setMessage(null);
		}
	}

	public String printError(Exception e) {
		return new Throwable().getStackTrace()[0].getMethodName() + " - " + e.getMessage();
	}

	public IpInformationDto getResponse() {
		return response;
	}

	public void setResponse(IpInformationDto response) {
		this.response = response;
	}

}