package zad1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class GUI {
    Database database;
    private DefaultTableModel tableModel;
    private JComboBox<String> languageComboBox;
    private List<Object[]> offersData;
    String currentLanguage;
    public GUI(Database database){
        this.database = database;
        createAndShowGUI();

    }
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Travel Offers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String[] languages = {"English", "Polish", "Germany"};
        currentLanguage = "English";
        languageComboBox = new JComboBox<>(languages);

        String[] columnNamesEn = {"ID", "Country", "Date From", "Date To", "Place", "Price", "Currency"};
        String[] columnNamesPl = {"ID", "Kraj", "Data od", "Data do", "Miejsce", "Cena", "Waluta"};
        String[] columnNamesDe = {"ID", "Land", "Datum Von", "Datum Bis", "Ort", "Preis", "Währung"};

        languageComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedLanguage = (String) languageComboBox.getSelectedItem();
                updateTableData(selectedLanguage);
                if(currentLanguage.equals("English")){
                    tableModel.setColumnIdentifiers(columnNamesEn);
                }
                else if(currentLanguage.equals("Polish")){
                    tableModel.setColumnIdentifiers(columnNamesPl);
                }
                else if(currentLanguage.equals("Germany")){
                    tableModel.setColumnIdentifiers(columnNamesDe);
                }
            }
        });

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(columnNamesEn);

        offersData = database.getOffersData();
        for (Object[] rowData : offersData) {
            tableModel.addRow(rowData);
        }
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel topPanel = new JPanel();
        topPanel.add((new JLabel("switch language")));
        topPanel.add(languageComboBox);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(800, 400);
        frame.setVisible(true);

    }
    public void updateTableData(String selectedLanguage){
        Locale resultLocale = null;
        Locale originalLocale = new Locale("en", "GB");
        String countryName = null, place = null;
        List<Object[]> offersDataCopyToTranslate = new ArrayList<>();
        for (Object[] rowData : offersData) {
            offersDataCopyToTranslate.add(rowData.clone());
        }
        if(selectedLanguage.equals("English")){
            if(!currentLanguage.equals("English")) {
                tableModel.setRowCount(0);
                resultLocale = new Locale("en", "GB");
                for (Object[] rowData : offersDataCopyToTranslate) {
                    tableModel.addRow(rowData);
                }
                currentLanguage = "English";
            }
        }
        else if(selectedLanguage.equals("Polish")){
            if(!currentLanguage.equals("Polish")) {
                tableModel.setRowCount(0);
                resultLocale = new Locale("pl", "PL");
                translateOffer(resultLocale, originalLocale, offersDataCopyToTranslate);
                currentLanguage = "Polish";
            }
        }
        else if(selectedLanguage.equals("Germany")){
            if(!currentLanguage.equals("Germany")){
                tableModel.setRowCount(0);
                resultLocale = new Locale("de", "DE");
                translateOffer(resultLocale, originalLocale, offersDataCopyToTranslate);
                currentLanguage = "Germany";
            }
        }

    }

    private void translateOffer(Locale resultLocale, Locale originalLocale, List<Object[]> offersDataCopyToTranslate) {
        String countryName;
        for (Object[] rowData : offersDataCopyToTranslate) {
            countryName = TravelData.translateCountry((String) rowData[1], originalLocale, resultLocale);
            rowData[1] = countryName;
            ResourceBundle resourceBundle = ResourceBundle.getBundle("offersResource", resultLocale);
            rowData[4] = resourceBundle.getString((String) rowData[4]);
            tableModel.addRow(rowData);
        }
    }
}
