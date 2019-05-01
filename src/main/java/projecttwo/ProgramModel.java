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

  public ProgramModel() {
    setup();
  }

  public Date getEndDate() {
    return endDate;
  }

  public double getTotal() {
    return total;
  }

  public ArrayList<RealEstateSale> getSales() {
    return sales;
  }

  public Locale getUserLocale() {
    return userLocale;
  }

  public double getConvertedPrice(RealEstateSale sale) {
    if (convertedPrices.containsKey(sale)) {
      return convertedPrices.get(sale);
    } else {
      return 0.0;
    }
  }

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
      setChanged();
      notifyObservers();
    }
  }

  public void setBeginDate(Date date) {
    beginDate = date;
    updateTotal();
    setChanged();
    notifyObservers();
  }

  public void setEndDate(Date date) {
    endDate = date;
    updateTotal();
    setChanged();
    notifyObservers();
  }

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
   * Generates a random, valid RealEstateSale.
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
