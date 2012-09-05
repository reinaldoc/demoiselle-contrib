package br.gov.frameworkdemoiselle.util.contrib;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Math {

	public static double round(double value, RoundingMode roundmode) {
		return round(value, 2, roundmode);
	}

	public static double round(double value, int digits, RoundingMode roundmode) {
		return (new BigDecimal(value)).divide(new BigDecimal(1), digits, roundmode).doubleValue();
	}

}
