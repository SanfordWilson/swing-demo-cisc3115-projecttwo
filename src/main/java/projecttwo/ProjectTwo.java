package projecttwo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DateFormat;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Main program and GUI for real estate transaction summary program.
 *
 * @author Sanford Wilson
 * @version 0.1 4/21/19
 */
public class ProjectTwo extends JFrame {
  private JList<RealEstateSale> salesList;
  private JScrollPane scrollPane;
  private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
  private JLabel totalLabel;
  private JPanel statsPane;

  private RealEstateSale[] data;

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
    scrollPane = new JScrollPane();
    salesList = new JList<RealEstateSale>(data);
    statsPane = new JPanel();
    totalLabel = new JLabel("PLACEHOLDER TEXT");

    salesList.setCellRenderer(new RealEstateSaleListCellRenderer());

    add(scrollPane, BorderLayout.CENTER);
    statsPane.setLayout(new BoxLayout(statsPane, BoxLayout.Y_AXIS));
    statsPane.add(new JLabel("Total:"));
    statsPane.add(totalLabel);
    add(statsPane, BorderLayout.EAST);
    scrollPane.setViewportView(salesList);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 300);
    setVisible(true);
  }

  /**
   * Instantializes 'data' to an array of 20 random RealEstateSales.
   */
  private void setupData() {
    data = new RealEstateSale[20];
    for (int i = 0; i < 20; i++) {
      data[i] = getRandomSale();
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
    while (sale == null || sale.getCountry() == null) {
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

        setText(String.format("%3s | %18s | $%.2f", sale.getCountry(), date, sale.getPrice()));
      }
      setFont(new Font("Courier New", Font.PLAIN, 14));
      return this;
    }
  }
}
