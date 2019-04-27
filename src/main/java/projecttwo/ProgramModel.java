package projecttwo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ProgramModel {
  private ArrayList<RealEstateSale> sales;
  private Locale userLocale;
  
  private Date beginDate;
  private Date endDate;
  private final Date now = new Date();

  private double total;

  public ProgramModel() {
    setupDates();
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

  private void setupDates() {
    Calendar cal = Calendar.getInstance();
    cal.set(1980, 0, 1);
    beginDate = cal.getTime();
    endDate = now;
    userLocale = Locale.getDefault();
    
    sales = new ArrayList<RealEstateSale>();
    sales.addAll(makeTestData());
    updateTotal();
  }

  private void updateTotal() {
    double newTotal = 0.0;
    for (RealEstateSale sale : sales) {
      if (sale.getDate().compareTo(beginDate) >= 0 && sale.getDate().compareTo(endDate) <= 1) {
        newTotal += CurrencyConverter.currConvert(
            CurrencyConverter.getCurrency(sale.getCountry()).toString(), "USD", sale.getPrice());
      }
    }
    if (newTotal != total) {
      total = newTotal;
    }
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
