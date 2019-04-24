package projecttwo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DateFormat;
import java.util.ArrayList;
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

/**
 * Main program and GUI for real estate transaction summary program.
 *
 * @author Sanford Wilson
 * @version 0.1 4/21/19
 */
public class ProjectTwo extends JFrame {
  private DefaultListModel<RealEstateSale> listModel;
  private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

  private JComboBox localeSelector;

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

  private ArrayList<RealEstateSale> data;

  /**
   * Entry point for program execution.
   */
  public static void main(String[] args) {
    ProjectTwo gui = new ProjectTwo();
  }

  /**
   * Creates instance of main program and sets up views.
   */
  private ProjectTwo() {
    super("Sales Records");

    setupData();
    setupNorthView();
    setupCenterView();
    setupEastView();
    setupSouthView();
    updateTotal();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 300);
    setVisible(true);
  }
  
  /**
   * Creates and shapes south views.
   */
  private void setupSouthView() {
    creationDatePicker = new JSpinner(new javax.swing.SpinnerDateModel());
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
    
  private void updateTotal() {
    double total = 0.0;
    for (RealEstateSale sale : data) {
      total += CurrencyConverter.currConvert(
          CurrencyConverter.getCurrency(sale.getCountry()).toString(), "USD", sale.getPrice());
    }
    totalLabel.setText(String.format("$%,.2f", total));
  }

  /**
   * Creates and shapes east views.
   */
  private void setupEastView() {
    totalLabel = new JLabel("PLACEHOLDER TEXT");

    beginDateSelector = new JSpinner(new javax.swing.SpinnerDateModel());
    endDateSelector = new JSpinner(new javax.swing.SpinnerDateModel());
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
    JList<RealEstateSale> salesList = new JList<RealEstateSale>(listModel);
    listModel.addAll(data);
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
   * Instantializes 'data' to an array of 20 random RealEstateSales.
   */
  private void setupData() {
    data = new ArrayList<RealEstateSale>();
    for (int i = 0; i < 20; i++) {
      data.add(getRandomSale());
    }
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

        setText(String.format("%3s | %18s | %,.2f", sale.getCountry(), date, sale.getPrice()));
      }
      setFont(new Font("Courier New", Font.PLAIN, 14));
      return this;
    }
  }

  class DateComparator implements java.util.Comparator<RealEstateSale> {

    public int compare(RealEstateSale one, RealEstateSale two) {
      return one.getDate().compareTo(two.getDate());
    }
  }

  class CountryComparator implements java.util.Comparator<RealEstateSale> {

    public int compare(RealEstateSale one, RealEstateSale two) {
      return one.getCountry().compareTo(two.getCountry());
    }
  }

  class PriceComparator implements java.util.Comparator<RealEstateSale> {

    public int compare(RealEstateSale one, RealEstateSale two) {
      return (int) (one.getPrice() - two.getPrice());
    }
  }
}
