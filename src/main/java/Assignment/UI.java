package Assignment;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class UI extends JFrame implements ActionListener, ListSelectionListener {
    JPanel rootPanel, contentPanel, leftPanel, rightPanel, totalPanel, rightContentPanel, searchPanel, buttonPanel;
    JList list;
    JTable table;
    JScrollPane scrollPaneLeft, scrollPaneRight;
    //AddBtn addBtn;
    //DiscardBtn discardBtn;
    JButton allConfirmedTotalBtn, highestAndLowestCountryBtn, searchBtn;
    JLabel titleLabel, totalLabel, totalValueLabel;
    JTextField searchTF;
    TableStatus status = TableStatus.INACTIVE;

    DefaultListModel listModel = new DefaultListModel();
    //DefaultTableModel model = new DefaultTableModel();
    String[] columnNames = {"ID", "Inventory ID", "Name", "Quantity", "Price"};

    public UI (){
        setSize(1280, 720);
        setTitle("Visualize Global COVID-19 Data - January 2020 to August 2022");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        addWindowListener(new WindowAdapter(){
//            @Override
//            public void windowClosing(WindowEvent e) {
//                new MenuPage();
//            }
//        });

        titleLabel = new JLabel("Visualize Global COVID-19 Data");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        list = new JList();
        list.setFont(new Font("Arial", Font.PLAIN, 16));
        //list.setCellRenderer(new MyListCellRenderer());
        COVIDReport.confirmedMap.keySet().stream().forEach(listModel::addElement);
        list.setModel(listModel);
        list.addListSelectionListener(this);
        scrollPaneLeft = new JScrollPane();
        scrollPaneLeft.setViewportView(list);

//        addBtn = new AddBtn();
//        addBtn.addActionListener(this);

        leftPanel = new JPanel(new BorderLayout(5, 10)){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension();
            }
        };
        leftPanel.add(scrollPaneLeft);
        //leftPanel.add(addBtn, BorderLayout.SOUTH);

        //model.setColumnIdentifiers(columnNames);

        table = new JTable(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(31);
        //table.setModel(model);
        //table.getTableHeader().setPreferredSize(
        //        new Dimension(table.getTableHeader().getPreferredSize().width, table.getTableHeader().getPreferredSize().height + 15));
        //table.setDefaultRenderer(Object.class, new MyTableCellRenderer());
        table.setFillsViewportHeight(true);

        scrollPaneRight = new JScrollPane(table);

        allConfirmedTotalBtn = new JButton("Display total confirmed by country");
        allConfirmedTotalBtn.addActionListener(this);
        highestAndLowestCountryBtn = new JButton("Display highest/lowest death and recovered cases by country");
        highestAndLowestCountryBtn.addActionListener(this);

        searchBtn = new JButton("Search Confirmed, Deaths, Recovered By Country");
        searchBtn.addActionListener(this);
        searchTF = new JTextField();
        searchTF.setFont(new Font("Arial", Font.PLAIN, 16));
        searchTF.setBorder(new CompoundBorder(new LineBorder(Color.black, 2), new EmptyBorder(2,2,2,2)));

        searchPanel = new JPanel(new GridLayout(1,2, 10, 5));
        searchPanel.add(searchTF);
        searchPanel.add(searchBtn);
//        totalLabel = new JLabel("Total (RM):");
//        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//        totalValueLabel = new JLabel("RM0.00");
//        totalValueLabel.setFont(DesignUI.defaultFontBold);
//        totalValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);


        totalPanel = new JPanel(new GridLayout(1,2, 10, 5));
        totalPanel.add(allConfirmedTotalBtn);
        totalPanel.add(highestAndLowestCountryBtn);

        rightContentPanel = new JPanel(new BorderLayout(5, 10));
        rightContentPanel.add(searchPanel, BorderLayout.NORTH);
        rightContentPanel.add(scrollPaneRight, BorderLayout.CENTER);
        rightContentPanel.add(totalPanel, BorderLayout.SOUTH);


//        discardBtn = new DiscardBtn("Back");
//        discardBtn.addActionListener(this);


//        buttonPanel = new JPanel(new GridLayout(1, 1, 20, 5));
//        buttonPanel.add(discardBtn);

        rightPanel = new JPanel(new BorderLayout(5, 20)){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension();
            }
        };
        rightPanel.add(rightContentPanel, BorderLayout.CENTER);
        //rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rootC = new GridBagConstraints();
        rootC.weightx = 0.4;
        rootC.weighty = 1;
        rootC.gridx = 0;
        rootC.insets = new Insets(0, 20, 0, 20);
        rootC.fill = GridBagConstraints.BOTH;
        contentPanel.add(leftPanel, rootC);
        rootC.gridx = 1;
        rootC.weightx = 0.6;
        contentPanel.add(rightPanel, rootC);

        rootPanel = new JPanel(new BorderLayout(5, 10));
        rootPanel.setBorder(new EmptyBorder(20,20,20,20));
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        rootPanel.add(contentPanel, BorderLayout.CENTER);
        add(rootPanel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == allConfirmedTotalBtn) {
            status = TableStatus.CONFIRMED_BY_COUNTRY;
            table.setModel(COVIDReport.addConfirmedTotalToTable());
        } else if(e.getSource() == highestAndLowestCountryBtn) {
            status = TableStatus.HIGHEST_LOWEST_DEATH_RECOVERED_BY_COUNTRY;
            table.setModel(COVIDReport.addHighestLowestToTable());
        } else if(e.getSource() == searchBtn) {
            status = TableStatus.CONFIRMED_DEATHS_RECOVERED_CASES_BY_COUNTRY;
            if(COVIDReport.confirmedMap.containsKey(searchTF.getText())) {
                table.setModel(COVIDReport.addCfmDeathsRecvrToTable(searchTF.getText()));
            } else {
                JOptionPane.showMessageDialog(this, "Not found. Data of all countries will be shown");
                table.setModel(COVIDReport.addCfmDeathsRecvrToTable());
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            if(status == TableStatus.CONFIRMED_BY_COUNTRY) {
                table.setModel(COVIDReport.addConfirmedTotalToTable(list.getSelectedValue().toString()));
            } else if(status == TableStatus.HIGHEST_LOWEST_DEATH_RECOVERED_BY_COUNTRY) {
                table.setModel(COVIDReport.addHighestLowestToTable(list.getSelectedValue().toString()));
            } else if(status == TableStatus.CONFIRMED_DEATHS_RECOVERED_CASES_BY_COUNTRY) {
                table.setModel(COVIDReport.addCfmDeathsRecvrToTable(list.getSelectedValue().toString()));
            }
        }
    }
}
