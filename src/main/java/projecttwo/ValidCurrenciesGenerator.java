package projecttwo;

import java.util.ArrayList;

/**
 * Utility class for printing a list of countries with valid currency conversions. Output has been
 *  used in reworking of 'CurrencyConverter', so has no more use. Kept only to show process.
 *
 * @see CurrencyConverter
 */
public class ValidCurrenciesGenerator {

  /**
   * Prints a nearly copy/paste-able list of country codes corresponding to convertible currencies
   *  found in 'CurrencyConverter.countryCodes'.
   *
   * @param args Not used.
   */
  public static void main(String[] args) {
    ArrayList<String> validCodes = new ArrayList<String>();
    System.out.print("{");
    for (int i = 0; i < CurrencyConverter.countryCodes.size(); i++) {
      try {
        String country = CurrencyConverter.countryCodes.get(i);
        double val = CurrencyConverter.currConvert(CurrencyConverter.getCurrency(country) + "", "USD", 1.0);
        val += 2.2;
        validCodes.add(country);
      } catch (Exception e) {
        continue;
      }
    }
    for (String code : validCodes) {
      System.out.print("\"" + code + "\", ");
    }
    System.out.print("}");
  }
}
