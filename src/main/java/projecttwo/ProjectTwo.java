package projecttwo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    //TODO Layout views better
    setupNorthView();
    setupCenterView();
    setupEastView();
    setupSouthView();
    updateTotalLabel();
    attachListeners();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 500);
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
    if (arg instanceof java.util.Collection) {
      java.util.Collection<RealEstateSale> updatedSales;
      try {
        updatedSales = (java.util.Collection<RealEstateSale>) arg;
        listModel.clear();
        listModel.addAll(updatedSales);
      } catch (ClassCastException e) {
        // Then the listModel should not be updated
      }
    }
    updateTotalLabel();
  }

  /**
   * Creates and adds instances of appropriate listeners to GUI elements.
   */
  private void attachListeners() {
    beginDateSelector.addChangeListener(new BeginDateListener());
    endDateSelector.addChangeListener(new EndDateListener());
    submit.addActionListener(new EntryCreationListener());
  }

  /**
   * Creates and shapes south views.
   */
  private void setupSouthView() {
    creationDatePicker = new JSpinner(new javax.swing.SpinnerDateModel(
          model.getEndDate(), null, model.getEndDate(), Calendar.DAY_OF_MONTH));

    DefaultComboBoxModel<String> localeSelectorModel = new DefaultComboBoxModel();
    localeSelectorModel.addAll(CurrencyConverter.countryCodes);
    creationCountrySelector = new JComboBox(localeSelectorModel);
    creationPriceField = new JTextField(20);
    JPanel southPanel = new JPanel();
    southPanel.add(creationDatePicker);
    southPanel.add(creationPriceField);
    southPanel.add(creationCountrySelector);
    submit = new JButton("Create New");
    southPanel.add(submit);

    add(southPanel, BorderLayout.SOUTH);
  }
    

  /**
   * Creates and shapes east views.
   */
  private void setupEastView() {
    totalLabel = new JLabel("PLACEHOLDER TEXT");

    Calendar cal = Calendar.getInstance();
    cal.set(1980, 0, 1);
    Date earliestDate = cal.getTime();
    Date endDate = model.getEndDate();
    beginDateSelector = new JSpinner(
        new javax.swing.SpinnerDateModel(earliestDate, null, endDate, Calendar.DAY_OF_MONTH));
    endDateSelector = new JSpinner(
        new javax.swing.SpinnerDateModel(endDate, earliestDate, endDate, Calendar.DAY_OF_MONTH));
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
   * Creates and shapes center views.
   */
  private void setupCenterView() {
    listModel = new DefaultListModel<RealEstateSale>();
    listModel.addAll(model.getSales());
    salesList = new JList<RealEstateSale>(listModel);
    salesList.setCellRenderer(new RealEstateSaleListCellRenderer());
    
    JPanel optionsPanel = new JPanel();
    currencyMatchingCheck = new JCheckBox("Convert from local currency");
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
   * Creates and shapes north views.
   */
  private void setupNorthView() {
    DefaultComboBoxModel<String> localeSelectorModel = new DefaultComboBoxModel();
    localeSelectorModel.addAll(CurrencyConverter.countryCodes);
    localeSelector = new JComboBox(localeSelectorModel);

    JPanel northPanel = new JPanel();
    northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
    northPanel.add(localeSelector);
    add(northPanel, BorderLayout.NORTH);
  }

  
  /**
   * Sets the text of 'totalLabel' to reflict the current total value in the model.
   */
  private void updateTotalLabel() {
    totalLabel.setText(String.format("$%,.2f", model.getTotal()));
  }

  /**
   * Custom ListCellRenderer for proper display of RealEstateSale values.
   */
  class RealEstateSaleListCellRenderer extends javax.swing.DefaultListCellRenderer {

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

        setText(String.format("%3s | %18s | %,13.2f", sale.getCountry(), date, sale.getPrice()));
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
     * Sets th emodel's ending date to the currently displayed date of 'endDateSelector'.
     */
    public void stateChanged(ChangeEvent event) {
      model.setEndDate((Date) endDateSelector.getValue());
    }
  }

  private class EntryCreationListener implements ActionListener {

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
  class DateComparator implements java.util.Comparator<RealEstateSale> {

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
  }

  /**
   * Allows comparison of 'RealEstateSale' instances by country.
   */
  class CountryComparator implements java.util.Comparator<RealEstateSale> {

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
  }

  /**
   * Allows comparison of 'RealEstateSale' instances by price.
   */
  class PriceComparator implements java.util.Comparator<RealEstateSale> {

    /**
     * Compares 'RealEstateSale' instances by the value of their respective 'getPrice()' methods.
     *   ***Does not currently account for differently valued currencies***
     *
     * @param one The first sale to compare
     * @param two The second sale to compare
     *
     * @return The result of the comparison of the returns of the 'getPrice' method of each
     *     RealEstateSale according to the natural sorting order of those values.
     */
    public int compare(RealEstateSale one, RealEstateSale two) {
      return (int) (one.getPrice() - two.getPrice());
    }
  }
}
