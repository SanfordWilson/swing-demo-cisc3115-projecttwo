package projecttwo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Model for a collection of 'RealEstateSale' instances and their totals converted to a common 
 *  currency and filterd by beginning and end dates.
 *
 *  @author Sanford Wilson
 *  @version 0.5 4/26/19
 *  @since 0.5
 *
 *  @see RealEstateSale
 *  @see java.util.Observable
 */
public class ProgramModel extends java.util.Observable {
  private ArrayList<RealEstateSale> sales;
  private HashMap<RealEstateSale, Double> convertedPrices;
  private Locale userLocale;
  
  private Date beginDate;
  private Date endDate;
  private final Date now = new Date();

  private double total;

  /**
   * Constructor. In current form provides a generated data set for demonstration purposes.
   */
  public ProgramModel() {
    setup();
  }

  /**
   * Accesses the final date in the range used to calculate 'total'.
   *
   * @return the 'Date' in question.
   * 
   * @see Date
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * Accessor for current total value of sales in the range determined by 'beginDate' and 'endDate'.
   *
   * @return the amount in the currency of 'userLocale'.
   *
   * @see Locale
   * @see java.util.Currency
   */
  public double getTotal() {
    return total;
  }

  /**
   * Accessor for all sales in the model.
   *
   * @return An 'ArrayList' containing all of the RealEstateSale objects under consideration.
   *
   * @see ArrayList
   */
  public ArrayList<RealEstateSale> getSales() {
    return sales;
  }

  /**
   * Accessor for the current Locale used for determining RealEstateSale values and total.
   *
   * @return the current Locale
   *
   * @see Locale
   */
  public Locale getUserLocale() {
    return userLocale;
  }

  /**
   * Provides the cached value of an individual sale in the data set in the currency
   *   of the currently selected 'Locale'.
   *
   * @param sale the RealEstateSale under consideration
   *
   * @return The price of 'sale' in the current Locale's currency, or 0.0 if 'sale' is not
   *     an element of the data set
   *
   * @see CurrencyConverter
   */
  public double getConvertedPrice(RealEstateSale sale) {
    if (convertedPrices.containsKey(sale)) {
      return convertedPrices.get(sale);
    } else {
      return 0.0;
    }
  }

  /**
   * Assigns a new 'Locale' for the instance. Converts the prices of all sales into the currency
   *   of the new 'Locale' and caches the values, then updates the total. Future improvements
   *   would include longer-term caching to improve performance.
   *
   * @param locale The new 'Locale' to be set.
   *
   * @see Locale
   * @see CurrencyConverter
   */
  public void setUserLocale(Locale locale) {
    if (userLocale != locale) {
      userLocale = locale;
      String currencyCode = CurrencyConverter.getCurrency(locale.getCountry()).toString();
      convertedPrices = new HashMap<RealEstateSale, Double>();
      for (RealEstateSale sale : sales) {
        convertedPrices.put(sale, CurrencyConverter.currConvert(
              CurrencyConverter.getCurrency(
                  sale.getCountry()).toString(), currencyCode, sale.getPrice()
              )
        );
      }
      updateTotal();
      setChanged();
      notifyObservers(locale);
    }
  }

  /**
   * Sets a new lower date threshold for calculating totals. Updates the total
   *   and notifies observers.
   *
   * @param date The new lowest Date for total calculation.
   *
   * @see Date
   */
  public void setBeginDate(Date date) {
    beginDate = date;
    updateTotal();
    setChanged();
    notifyObservers();
  }

  /**
  * Sets a new upper date threshold for calculating totals. Updates the total
  *   and notifies observers.
  *
  * @param date The new highest Date for total calculation.
  *
  * @see Date
  */
  public void setEndDate(Date date) {
    endDate = date;
    updateTotal();
    setChanged();
    notifyObservers();
  }

  /**
   * Enters the provided RealEstateSale into the data set. Calculates and caches the sale's
   *   price in the currency of the current userLocale. Updates total and notifies observers.
   *
   * @param sale The sale to be added
   */
  public void addSale(RealEstateSale sale) {
    sales.add(sale);
    String currencyCode = CurrencyConverter.getCurrency(userLocale.getCountry()).toString();
    convertedPrices.put(sale, CurrencyConverter.currConvert(
        CurrencyConverter.getCurrency(
            sale.getCountry()).toString(), currencyCode, sale.getPrice()
        )
    );
    updateTotal();
    setChanged();
    notifyObservers(sale);
  }

  /**
   * Sets initial values for demonstration of program.
   */
  private void setup() {
    Calendar cal = Calendar.getInstance();
    cal.set(1980, 0, 1);
    beginDate = cal.getTime();
    endDate = now;
    
    sales = new ArrayList<RealEstateSale>();
    sales.addAll(makeTestData());
    setUserLocale(Locale.getDefault());
    updateTotal();
  }

  /**
   * Recalculates the total value of all RealEstateSale objects in the data set from cached values.
   */
  private void updateTotal() {
    double newTotal = 0.0;
    for (RealEstateSale sale : sales) {
      if (sale.getDate().compareTo(beginDate) >= 0 && sale.getDate().compareTo(endDate) <= 0) {
        newTotal += convertedPrices.get(sale);
      }
    }
    total = newTotal;
  }
  
  /**
   * Generates a random data set for testing purposes.
   *
   * @return ArrayList of 20 random valid 'RealEstateSale's
   */
  private ArrayList<RealEstateSale> makeTestData() {
    ArrayList<RealEstateSale> data = new ArrayList<RealEstateSale>();
    for (int i = 0; i < 20; i++) {
      data.add(getRandomSale());
    }
    return data;
  }

  /**
   * Generates a random, valid RealEstateSale. For testing and demonstration.
   *
   * @return an instance of RealEstateSale with valid fields
   */
  private RealEstateSale getRandomSale() {
    ArrayList<String> codes = CurrencyConverter.countryCodes;
    RealEstateSale sale = null;
    while (sale == null) {
      sale = RealEstateSale.make(
                  codes.get((int) (Math.random() * codes.size())), 
                  Math.random() * 5000000 + 75000,
                  (int) (Math.random() * 20) + 1998,
                  (int) (Math.random() * 12),
                  (int) (Math.random() * 31) + 1
              );
    }
    return sale;
  }
}
