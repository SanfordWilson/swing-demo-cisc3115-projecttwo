package projecttwo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Main program and GUI for real estate transaction summary program.
 *
 * @author Sanford Wilson
 * @version 0.5 4/26/19
 * @since 0.2
 *
 * @see ProgramModel
 * @see RealEstateSale
 */
public final class ProjectTwo extends JFrame implements java.util.Observer {

  private ProgramModel model;
  private JComboBox localeSelector;
  private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

  private JList<RealEstateSale> salesList;
  private DefaultListModel<RealEstateSale> listModel;
  private JLabel totalLabel;
  private JComboBox countryFilter;
  
  private JComboBox sortByBox;
  private JCheckBox currencyMatchingCheck;
  private JCheckBox historicalCheck;
  private JSpinner beginDateSelector;
  private JSpinner endDateSelector;

  private JSpinner creationDatePicker;
  private JTextField creationPriceField;
  private JComboBox creationCountrySelector;
  private JButton submit;


  /**
   * Entry point for program execution.
   *
   * @param args Not used.
   */
  public static void main(String[] args) {
    ProjectTwo gui = new ProjectTwo();
  }

  /**
   * Creates instance of main program and sets up views.
   */
  private ProjectTwo() {
    super("Sales Records");
    model = new ProgramModel();
    model.addObserver(this);

    createComponents();
    setupNorthView();
    setupCenterView();
    setupEastView();
    setupSouthView();
    attachListeners();

    updateTotalLabel();
    setDateSpinnerFormats(dateFormat);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(900, 600);
    setVisible(true);
  }

  /**
   * Responds to changes in the model. Updates the displayed total and, if necessary, the
   *  'DefaultListModel' for the displayed list of sales.
   *
   *  @param o The sending object, should be the model.
   *  @param arg Optional object sent by 'o'. If arg is a collection, 'listModel' will be updated
   *      to reflect the contents of 'arg'.
   */
  public void update(Observable o, Object arg) {
    if (arg instanceof RealEstateSale) {
      listModel.addElement((RealEstateSale) arg);
      sortListBy((Comparator<RealEstateSale>)sortByBox.getSelectedItem());
    }
    updateTotalLabel();
  }

  /**
   * Coordinates instantiation of interactive GUI components.
   */
  private void createComponents() {
    createDateFilterComponents();
    createNewEntryComponents();
    totalLabel = new JLabel("PLACEHOLDER TEXT");
    setupList();
    createViewingOptionComponents();
  }

  /**
   * Handles instantiation of the interactive components involved in new entry creation.
   */
  private void createNewEntryComponents() {
    creationDatePicker = new JSpinner(new javax.swing.SpinnerDateModel(
          model.getEndDate(), null, model.getEndDate(), Calendar.DAY_OF_MONTH));
    DefaultComboBoxModel<String> localeSelectorModel = new DefaultComboBoxModel();
    localeSelectorModel.addAll(CurrencyConverter.countryCodes);
    creationCountrySelector = new JComboBox(localeSelectorModel);
    creationPriceField = new JTextField(20);
    submit = new JButton("Create New");
  }

  /**
   * Handles instantiation of interactive components involved in filtering dates for total.
   */
  private void createDateFilterComponents() {
    Calendar cal = Calendar.getInstance();
    cal.set(1980, 0, 1);
    Date earliestDate = cal.getTime();
    Date endDate = model.getEndDate();
    beginDateSelector = new JSpinner(
        new javax.swing.SpinnerDateModel(earliestDate, null, endDate, Calendar.DAY_OF_MONTH));
    endDateSelector = new JSpinner(
        new javax.swing.SpinnerDateModel(endDate, earliestDate, endDate, Calendar.DAY_OF_MONTH));
  }

  /**
   * Handles instantiation of components involved in displaying list of RealEstateSales.
   */
  private void setupList() {
    listModel = new DefaultListModel<RealEstateSale>();
    listModel.addAll(model.getSales());
    sortListBy(new DateComparator());
    salesList = new JList<RealEstateSale>(listModel);
    salesList.setCellRenderer(new RealEstateSaleListCellRenderer());
  }

  /**
   * Handles instantiation of components involved in setting viewing options for list.
   */
  private void createViewingOptionComponents() {
    currencyMatchingCheck = new JCheckBox("Convert from local currency:");
    DefaultComboBoxModel<String> localeSelectorModel = new DefaultComboBoxModel();
    localeSelectorModel.addAll(CurrencyConverter.countryCodes);
    localeSelector = new JComboBox(localeSelectorModel);
    localeSelector.setSelectedItem(model.getUserLocale().getCountry());
    localeSelector.setEditable(false);

    historicalCheck = new JCheckBox("Use historical exchange rates when available:");

    DefaultComboBoxModel<Comparator> sortByModel = new DefaultComboBoxModel(
        new Comparator[] {new DateComparator(), new PriceComparator(), new CountryComparator()});
    sortByBox = new JComboBox(sortByModel);
    sortByBox.setEditable(false);
    sortByBox.setSelectedIndex(0);
  }

  /**
   * Creates and adds instances of appropriate listeners to GUI elements.
   */
  private void attachListeners() {
    beginDateSelector.addChangeListener(new BeginDateListener());
    endDateSelector.addChangeListener(new EndDateListener());
    submit.addActionListener(new EntryCreationListener());
    currencyMatchingCheck.addItemListener(new ConvertAllPricesListener());
    sortByBox.addItemListener(new SortingListener());
    localeSelector.addItemListener(new LocaleSelectionListener());
    historicalCheck.addItemListener(new HistoricalListener());
  }

  /**
   * Lays out the views in the 'southern' portion of the frame.
   */
  private void setupSouthView() {
    JPanel southPanel = new JPanel();
    southPanel.add(creationDatePicker);
    southPanel.add(creationPriceField);
    southPanel.add(creationCountrySelector);
    southPanel.add(submit);

    add(southPanel, BorderLayout.SOUTH);
  }
    

  /**
   * Lays out the views in the 'eastern' portion of the frame.
   */
  private void setupEastView() {
    JPanel spinnerPanel = new JPanel();
    spinnerPanel.add(beginDateSelector);
    spinnerPanel.add(endDateSelector);

    JPanel statsPanel = new JPanel();
    statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
    statsPanel.add(spinnerPanel);
    statsPanel.add(new JLabel("Total:"));
    statsPanel.add(totalLabel);
    add(statsPanel, BorderLayout.EAST);
  }

  /**
   * Lays out the views in the 'center' portion of the frame.
   */
  private void setupCenterView() {
    
    JPanel optionsPanel = new JPanel();
    optionsPanel.add(sortByBox);
    optionsPanel.add(currencyMatchingCheck);

    JScrollPane scrollPane = new JScrollPane();
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    centerPanel.add(optionsPanel);
    centerPanel.add(scrollPane);
    scrollPane.setViewportView(salesList);
    add(centerPanel, BorderLayout.CENTER);
  }

  /**
   * Lays out the views in the 'northern' portion of the frame.
   */
  private void setupNorthView() {

    JPanel northPanel = new JPanel();
    northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
    northPanel.add(localeSelector);
    northPanel.add(historicalCheck);
    add(northPanel, BorderLayout.NORTH);
  }

  
  /**
   * Sets the text of 'totalLabel' to reflict the current total value in the model.
   */
  private void updateTotalLabel() {
    totalLabel.setText(formatForLocale(model.getTotal(), model.getUserLocale()));
  }

  /**
   * Provides a formatted string to properly display amount in local style for the currency of
   *    specified country.
   *
   * <p>Includes appropriate currency symbol. 
   *
   * @param amount the amount of currency
   * @param countryCode 2-letter code for the country.
   *
   * @return formatted String for locale-correct display of amount in the appropriate currency
   */
  private String formatForLocale(double amount, String countryCode) {
    Locale locale = new Locale("en", countryCode);
    return formatForLocale(amount, locale);
  }

  /**
   * Provides a formatted String to properly display in local style amount
   *    in the currency of locale.
   *
   * <p>Includes appropriate currency symbol. 
   *
   * @param amount the number to be formatted
   * @param locale the Locale of the desired currency
   *
   * @return formatted String for locale-correct display of amount in the appropriate currency
   *
   * @see Locale
   */
  private String formatForLocale(double amount, Locale locale) {
    NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
    return formatter.format(amount);
  }

  /**
   * Sorts entries in main display list by the provided method.
   *
   * @param sortMethod the Comparator used to determine sort order
   *
   * @see Comparator
   */
  private void sortListBy(Comparator<RealEstateSale> sortMethod) {
    java.util.ArrayList<RealEstateSale> tempList = Collections.list(listModel.elements());
    Collections.sort(tempList, sortMethod);
    listModel.clear();
    listModel.addAll(tempList);
  }

  /**
   * Provides a best-guess locale for a given country code.
   *
   * <p>Uses jdk-provided constants when available.
   *
   * @param country The 2-letter country code of the desired country
   *
   * @return a best-guess instance of Locale
   *
   * @see Locale
   */
  private Locale bestLocaleFor(String country) {
    Locale bestGuess;
    switch (country) {
      case "CA":
        bestGuess = Locale.CANADA;
        break;
      case "CN":
        bestGuess = Locale.CHINA;
        break;
      case "FR":
        bestGuess = Locale.FRANCE;
        break;
      case "DE":
        bestGuess = Locale.GERMANY;
        break;
      case "IT":
        bestGuess = Locale.ITALY;
        break;
      case "JP":
        bestGuess = Locale.JAPAN;
        break;
      case "KR":
        bestGuess = Locale.KOREA;
        break;
      case "TW":
        bestGuess = Locale.TAIWAN;
        break;
      case "US":
        bestGuess = Locale.US;
        break;
      case "UK":
        bestGuess = Locale.UK;
        break;
      default:
        bestGuess = new Locale("en", country);
    }
    return bestGuess;
  }

  /**
   * Updates the formats of all JSpinners displaying dates.
   *
   * @param dateFormat the new format to use
   *
   * @see DateFormat
   */
  private void setDateSpinnerFormats(DateFormat dateFormat) {
    try {
      String format = ((java.text.SimpleDateFormat) dateFormat).toLocalizedPattern();
      beginDateSelector.setEditor(new JSpinner.DateEditor(beginDateSelector, format));
      endDateSelector.setEditor(new JSpinner.DateEditor(endDateSelector, format));
      creationDatePicker.setEditor(new JSpinner.DateEditor(creationDatePicker, format));
    } catch (ClassCastException e) {
      //keep the current format
    }
  }

  /**
   * Custom ListCellRenderer for proper display of RealEstateSale values.
   */
  class RealEstateSaleListCellRenderer extends DefaultListCellRenderer {

    /**
     * Formats a cell for proper display of RealEstateSale objects.
     *
     * @param list The JList consumer of cells.
     * @param value The RealEstateSale associated with the cell.
     * @param index The index of value in list's model.
     * @param isSelected Selection status of cell to be rendered.
     * @param cellHasFocus Focus status of cell to be rendered.
     *
     * @return The properly formatted cell.
     */
    public java.awt.Component getListCellRendererComponent(
              JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof RealEstateSale) {
        RealEstateSale sale = (RealEstateSale) value;
        String date = dateFormat.format(sale.getDate());

        setText(String.format("%3s | %18s | %15s", 
              sale.getCountry(), date, 
              formatForLocale(sale.getPrice(), bestLocaleFor(sale.getCountry()))
            ));
      }
      setFont(new Font("Courier New", Font.PLAIN, 14));
      return this;
    }
  }

  /**
   * Custom ListCellRenderer for proper display of RealEstateSale values with prices converted
   *    to the user's currency.
   */
  class RealEstateSaleListCellRendererConvertedCurrencies extends DefaultListCellRenderer {

    /**
     * Formats a cell for proper display of RealEstateSale objects.
     *
     * @param list The JList consumer of cells.
     * @param value The RealEstateSale associated with the cell.
     * @param index The index of value in list's model.
     * @param isSelected Selection status of cell to be rendered.
     * @param cellHasFocus Focus status of cell to be rendered.
     *
     * @return The properly formatted cell.
     */
    public java.awt.Component getListCellRendererComponent(
              JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof RealEstateSale) {
        RealEstateSale sale = (RealEstateSale) value;
        String date = dateFormat.format(sale.getDate());

        setText(String.format("%3s | %18s | %15s", 
               sale.getCountry(), 
               date, 
               formatForLocale(model.getConvertedPrice(sale), 
               model.getUserLocale())
              ));
      }
      setFont(new Font("Courier New", Font.PLAIN, 14));
      return this;
    }
  }


  /**
   * Listener for manipulation of the model's beginning date.
   */
  private class BeginDateListener implements ChangeListener {

    /**
     * Sets the model's beginning date to the currently displayed value of 'beginDateSelector'.
     *
     * @param event unused
     */
    public void stateChanged(ChangeEvent event) {
      model.setBeginDate((Date) beginDateSelector.getValue());
    }
  }

  /**
   * Listener for manipulation of the model's ending date.
   */
  private class EndDateListener implements ChangeListener {

    /**
     * Sets the model's ending date to the currently displayed date of 'endDateSelector'.
     * @param event unused
     */
    public void stateChanged(ChangeEvent event) {
      model.setEndDate((Date) endDateSelector.getValue());
    }
  }

  /**
   * Listener for new entry creation. Adds a new entry to the model and restes fields.
   */
  private class EntryCreationListener implements ActionListener {

    /**
     * Creates and adds a new RealEstateSale to the model and resets entry fields
     *    if fields contain valid information.
     *
     * @param event unused
     */
    public void actionPerformed(ActionEvent event) {
      RealEstateSale sale = null;
      try {
        sale = RealEstateSale.make(
            (String) creationCountrySelector.getSelectedItem(),
            Double.parseDouble(creationPriceField.getText()), 
            (Date) creationDatePicker.getValue()
        );
        if (sale != null) {
          model.addSale(sale);
          creationDatePicker.setValue(new Date());
          creationPriceField.setText("");
        }
      } catch (NumberFormatException e) {
        //do nothing
      } catch (NullPointerException e) {
        //do nothing
      } catch (ClassCastException e) {
        //also do nothing
      }
    }
  }

  /**
   * Allows comparison of 'RealEstateSale' instances by their 'Date'.
   */
  class DateComparator implements Comparator<RealEstateSale> {

    /**
     * Compares 'RealEstateSale' instances by the values of their respective 'getDate()' methods.
     *
     * @param one The first sale to compare
     * @param two The second sale for comparison
     *
     * @return An int less than 0 if 'one' has a lesser 'Date' than 'two', 0 if both dates 
     *     are equal, or an int greater than 0 if 'one' has a greater 'Date' than 'two'.
     */
    public int compare(RealEstateSale one, RealEstateSale two) {
      return one.getDate().compareTo(two.getDate());
    }

    public String toString() {
      return "Date";
    }
  }

  /**
   * Allows comparison of 'RealEstateSale' instances by country.
   */
  class CountryComparator implements Comparator<RealEstateSale> {

    /**
     * Compares 'RealEstateSale' instances by the value of their respective 'getCountry()' methods.
     *
     * @param one The first sale to compare
     * @param two The second sale to compare
     *
     * @return The result of the comparison of the returns of the 'getCountry' method of each
     *     RealEstateSale according to the natural sorting order of those values.
     */
    public int compare(RealEstateSale one, RealEstateSale two) {
      return one.getCountry().compareTo(two.getCountry());
    }

    public String toString() {
      return "Country";
    }
  }

  /**
   * Allows comparison of 'RealEstateSale' instances by price.
   */
  class PriceComparator implements Comparator<RealEstateSale> {

    /**
     * Compares 'RealEstateSale' instances by the value of their respective prices under the 
     *    current currency conversion model.
     *
     * @param one The first sale to compare
     * @param two The second sale to compare
     *
     * @return The result of the comparison of the RealEstateSales converted prices.
     */
    public int compare(RealEstateSale one, RealEstateSale two) {
      return (int) (model.getConvertedPrice(one) - model.getConvertedPrice(two));
    }

    public String toString() {
      return "Price";
    }
  }

  /**
   * Listener for toggling display of converted prices in list.
   */
  class ConvertAllPricesListener implements ItemListener {

    /**
     * Sets the conversion display mode.
     *
     * @param event reflects the state of the item pressed
     */
    public void itemStateChanged(ItemEvent event) {
      DefaultListCellRenderer renderer;
      if (event.getStateChange() == ItemEvent.SELECTED) {
        renderer = new RealEstateSaleListCellRendererConvertedCurrencies();
      } else {
        renderer = new RealEstateSaleListCellRenderer();
      }
      salesList.setCellRenderer(renderer);
    }
  }

  /**
   * Listener for selection of sorting order.
   */
  class SortingListener implements ItemListener {
    public void itemStateChanged(ItemEvent event) {
      sortListBy((Comparator<RealEstateSale>)sortByBox.getSelectedItem());
    }
  }

  /**
   * Listener for selection of user's preferred locale.
   */
  class LocaleSelectionListener implements ItemListener {
    public void itemStateChanged(ItemEvent event) {
      String country = (String) localeSelector.getSelectedItem();
      Locale toSet = bestLocaleFor(country);
      model.setUserLocale(toSet);
      dateFormat = DateFormat.getDateInstance(DateFormat.LONG, toSet);
      setDateSpinnerFormats(dateFormat);
      salesList.repaint();
    }
  }

  /**
   * Listener for toggling historic or current exchange rates.
   */
  class HistoricalListener implements ItemListener {
    public void itemStateChanged(ItemEvent event) {
      if (event.getStateChange() == ItemEvent.SELECTED) {
        model.setHistorical(true);
      } else {
        model.setHistorical(false);
      }
    }
  }
}
