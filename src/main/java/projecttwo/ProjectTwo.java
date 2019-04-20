package projecttwo;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import javax.swing.JList;
import java.awt.BorderLayout;
import java.text.DateFormat;

public class ProjectTwo extends JFrame {
  private JList salesList;
  private JScrollPane scrollPane;
  private static DateFormat dateFormat = DateFormat.getDateInstance();

  private RealEstateSale[] data;
  public static void main(String[] args) {
    ProjectTwo gui = new ProjectTwo();
  }

  private ProjectTwo() {
    super("Sales Records");

    setupData();
    scrollPane = new JScrollPane();
    salesList = new JList(data);

    salesList.setCellRenderer(new RealEstateSaleListCellRenderer());
    add(scrollPane, BorderLayout.CENTER);
    scrollPane.setViewportView(salesList);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 300);
    setVisible(true);
  }

  private void setupData() {
    data = new RealEstateSale[20];
    ArrayList<String> codes = CurrencyConverter.countryCodes;
    for (int i = 0; i < 20; i++) {
      data[i] = RealEstateSale.make(
                  codes.get((int) (Math.random() * codes.size())), 
                  Math.random() * 5000000 + 75000,
                  (int) (Math.random() * 20) + 1998,
                  (int) (Math.random() * 12),
                  (int) (Math.random() * 31) + 1
              );
    }
  }
  
  class RealEstateSaleListCellRenderer extends javax.swing.DefaultListCellRenderer {
    public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof RealEstateSale) {
        RealEstateSale sale = (RealEstateSale) value;
        String date = dateFormat.format(sale.getDate());

        setText(String.format("%3s | %s | %f", sale.getCountry(), date, sale.getPrice()));
      }
      return this;
    }
  }
}
