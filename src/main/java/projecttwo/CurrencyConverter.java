package projecttwo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for CISC 3115. Uses a no-cost online currency conversion tool to convert values
 * from one currency to another.
 */
public class CurrencyConverter {
  public static ArrayList<String> countryCodes;

  /* intialize countryCodes */

  static {
    String[] locales = {"AD", "AS", "AT", "AX", "BE", "BL", 
        "BQ", "CA", "CY", "DE", "EC", "EE", "ES", "FI", "FM", 
        "FR", "GF", "GP", "GR", "GU", "IE", "IO", "IS", "IT", 
        "LT", "LU", "LV", "MC", "ME", "MF", "MH", "MP", "MQ", 
        "MT", "NL", "PM", "PR", "PT", "PW", "RE", "SI", "SK", 
        "SM", "TC", "TF", "TL", "UM", "US", "VA", "VG", "VI", "YT"};
    countryCodes = new ArrayList<String>();
    for (String s : locales) {
      countryCodes.add(s);
      //      Locale l = new Locale("en", s);
      //      countryCodes.add(l.getISO3Country());
    }
  }

  /**
   * Highly brittle method to return Currency of given country code.
   *
   * <p>May crash on a few country codes, but mostly works...!
   *
   * @param countryCode Two-character country code.
   * @return Currency code corresponding to countryCode.
   */
  public static Currency getCurrency(String countryCode) {
    Locale l = new Locale("*", countryCode);
    return Currency.getInstance(l);
  }

  /**
   * Highly brittle function that uses an online currency conversion API.
   *
   * <p>See https://exchangeratesapi.io/ for more info.
   *
   * @param from ISO currency code representing currency of amount.
   * @param to ISO currency code representing target currency
   * @param amount currency amount to be converted
   * @return value of amount, expressed in "to" currency
   */
  public static Double currConvert(String from, String to, double amount) {

    try {
      URL url = new URL("https://api.exchangeratesapi.io/latest?base=" + from + "&symbols=" + to);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String jsonString = reader.readLine();
      if (jsonString.length() > 0) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d*");
        Matcher matcher = pattern.matcher(jsonString);
        if (matcher.find()) {
          return Double.parseDouble(matcher.group()) * amount;
        } else {
          return null;
        }
      }
      reader.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return null;
  }
}
