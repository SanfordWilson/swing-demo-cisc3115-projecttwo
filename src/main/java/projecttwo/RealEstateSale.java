package projecttwo;

import java.util.Calendar;
import java.util.Date; 

/**
 * Simple class holding the details of a real estate transaction.
 *
 * @author Sanford Wilson
 * @version 0.0.5
 */
public class RealEstateSale {
  protected final String country;
  protected final double price;
  protected final Date date;

  /**
   * Naive constructor.
   */
  protected RealEstateSale(String country, double price, Date date) {
    this.country = country;
    this.price = price;
    this.date = date;
  }

  /**
   * Data-validating factory for RealEstateSale instances. 
   *
   * @param country The 2-letter country code corresponding to the location of the sale.
   * @param price The amount of the transaction.
   * @param year The year of the transaction.
   * @param month The month of the transaction.
   * @param day Day of the transaction.
   *
   * @return A new instance of RealEstateSale if fields are valid, otherwise returns null.
   */
  public static RealEstateSale make(String country, double price, int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, month, day);
    cal.setLenient(false);
    
    Date date = null;

    try {
      date = cal.getTime();
    } catch (Exception e) {
      return null;
    }
    if (!CurrencyConverter.countryCodes.contains(country)) {
      return null;
    }
    if (price < 0.0) {
      return null;
    }

    return new RealEstateSale(country, price, date);
  }

  /**
   * Accesses country code.
   *
   * @return The 2-letter country code corresponding to the location of the sale.
   */
  public String getCountry() {
    return country;
  }

  /**
   * Accesses sale price.
   *
   * @return The amount of the transaction.
   */
  public double getPrice() {
    return price;
  }

  /**
   * Accesses sale date.
   *
   * @return Date object representing the Month, Day, and Year of the transaction.
   */
  public Date getDate() {
    return date;
  }

}
