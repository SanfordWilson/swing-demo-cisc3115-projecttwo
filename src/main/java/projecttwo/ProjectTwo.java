package projecttwo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
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
public final class ProjectTwo extends JFrame {

  private ProgramModel model;
  private JComboBox localeSelector;
  private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

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

    setupNorthView();
    setupCenterView();
    setupEastView();
    setupSouthView();
    updateTotalLabel();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 300);
    setVisible(true);
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
    DefaultListModel listModel = new DefaultListModel<RealEstateSale>();
    listModel.addAll(model.getSales());
    JList<RealEstateSale> salesList = new JList<RealEstateSale>(listModel);
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
