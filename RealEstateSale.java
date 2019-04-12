import java.util.Date; import java.util.Calendar;
/**
 * Simple class holding the details of a real estate transaction.
 *
 * @author Sanford Wilson
 * @version 0.5
 */
public class RealEstateSale {
  private final String country;
  private final double price;
  private final Date date;

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
   * @param country The three-letter country code where the sale occurred.
   * @param price The fully amount of the transaction.
   * @param year The year of the transaction.
   * @param month The month of the transaction.
   * @param day Day of the transaction.
   *
   * @return A new instance of RealEstateSale. If provided an invalid date or country code,
   * all fields will be null/zero.
   */
  public static RealEstateSale make(String country, double price, int year, int month, int day) {
    if (!CurrencyConverter.countryCodes.contains(country)) {
      country = null;
      price = 0.0;
    }
    Calendar cal = Calendar.getInstance();
    cal.set(year, month, day);
    cal.setLenient(false);
    
    Date date = null;

    try {
      date = cal.getTime();
    } catch (Exception e) {
      country = null;
      price = 0.0;
    }

    return new RealEstateSale(country, price, date);
  }
}
