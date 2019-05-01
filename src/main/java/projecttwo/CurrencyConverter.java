package projecttwo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
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
    String[] locales = {"AD", "AS", "AT", "AX", "BE", "BL", "BQ", "BR", "BV", "CA", "CC", "CH", 
        "CK", "CN", "CX", "CY", "CZ", "DE", "DK", "EC", "EE", "ES", "FI", "FM", "FO", "FR", "GB", 
        "GF", "GG", "GL", "GP", "GR", "GS", "GU", "HK", "HM", "HR", "HU", "ID", "IE", "IL", "IM", 
        "IN", "IO", "IS", "IT", "JE", "JP", "KI", "KR", "LI", "LT", "LU", "LV", "MC", "ME", "MF", 
        "MH", "MP", "MQ", "MT", "MX", "MY", "NF", "NL", "NO", "NR", "NU", "NZ", "PH", "PL", "PM", 
        "PN", "PR", "PS", "PT", "PW", "RE", "RO", "SE", "SG", "SI", "SJ", "SK", "SM", "TC", "TF", 
        "TH", "TL", "TR", "TV", "UM", "US", "VA", "VG", "VI", "YT", "ZA"};
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
    Locale l = new Locale("", countryCode);
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
    if (from.equals(to)) {
      return amount;    //avoids possible 400 error for same currencies
    } else {
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

  /**
   * Highly brittle funciton that uses an online currency conversion API.
   *
   * <p>See https://exchangeratesapi.io/ for more info.
   *
   * @param from ISO currency code representing currency of amount.
   * @param to ISO currency code representing target currency
   * @param amount currency amount to be converted
   * @param date the date the exchange rate should referenced to.
   * @return value of amount, expressed in "to" currency using the exchange rate at the time
   *     of date.
   */
  public static Double currConvert(String from, String to, double amount, Date date) {
    if (from.equals(to)) {
      return amount;    //avoids possible 400 error for same currencies
    } else {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      String endDateString = stringify(cal);
      cal.add(Calendar.DATE, -1);
      String beginDateString = stringify(cal);
      try {
        URL url = new URL(
            "https://api.exchangeratesapi.io/history?start_at=" + beginDateString + "&end_at=" 
            + endDateString + "&base=" + from + "&symbols=" + to);
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

  private static String stringify(Calendar cal) {
    int month = cal.get(Calendar.MONTH);
    int date = cal.get(Calendar.DATE);
    month++;
    String monthString;
    String dateString;
    if (month < 10) {
      monthString = "0" + month;
    } else {
      monthString = "" + month;
    }
    if (date < 10) {
      dateString = "0" + date;
    } else {
      dateString = "" + date;
    }
    return cal.get(Calendar.YEAR) + "-" + monthString + "-" + dateString;
  }
}
