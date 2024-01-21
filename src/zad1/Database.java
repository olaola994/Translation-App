package zad1;

import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Database {
    String url;
    TravelData travelData;

    public Database(String url, TravelData travelData){
        this.url = url;
        this.travelData = travelData;
    }
    public void create(){
        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            List<String> offersDescriptions = travelData.getOffersDescriptionsList("en_GB", "yyyy-MM-dd");

            statement.executeUpdate("DROP TABLE OFFERS");
            statement.executeUpdate("CREATE TABLE OFFERS(ID INT PRIMARY KEY ," +
                    " COUNTRY VARCHAR(50)," +
                    " DATE_FROM DATE," +
                    " DATE_TO DATE," +
                    " PLACE VARCHAR(20)," +
                    " PRICE DECIMAL(10,2)," +
                    " CURRENCY VARCHAR(5))");

            int counter = 1;
            for(String str : offersDescriptions){
                String[] splittedDescriptions = str.split(" ");
                int countryLength = 0;
                for(String word : splittedDescriptions){
                    if(Character.isDigit(word.charAt(0))){
                        break;
                    }
                    countryLength++;
                }
                String countryName = "", dateFrom = "", dateTo = "", place = "", price = "", currency = "";
                if(countryLength > 1){
                    for(int i = 0; i < countryLength; i++){
                        countryName += splittedDescriptions[i] + " ";
                    }
                    countryName = countryName.trim();

                }
                else{
                    countryName = splittedDescriptions[0];
                }
                int id = counter;
                dateFrom = splittedDescriptions[countryLength];
                dateTo = splittedDescriptions[countryLength+1];
                place = splittedDescriptions[countryLength+2];
                price = splittedDescriptions[countryLength+3];
                currency = splittedDescriptions[countryLength+4];


                Locale originalLocale = new Locale("en","GB");
                Locale resultLocale = new Locale("pl", "PL");
                String parsedtoPlPrice = TravelData.translateCurrency(price, originalLocale, resultLocale);

                NumberFormat numberFormat = NumberFormat.getNumberInstance(resultLocale);
                Number number = numberFormat.parse(parsedtoPlPrice);
                double parsedDoublePrice = number.doubleValue();




                String insertQuery = "INSERT INTO OFFERS (ID, COUNTRY, DATE_FROM, DATE_TO, PLACE, PRICE, CURRENCY) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, countryName);
                    preparedStatement.setDate(3, Date.valueOf(dateFrom));
                    preparedStatement.setDate(4, Date.valueOf(dateTo));
                    preparedStatement.setString(5, place);
                    preparedStatement.setBigDecimal(6, BigDecimal.valueOf(parsedDoublePrice));
                    preparedStatement.setString(7, currency);


                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                counter++;

            }
            System.out.println();

            connection.close();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }
    public List<Object[]> getOffersData() {
        List<Object[]> offersData = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM OFFERS");

            while (resultSet.next()) {
                Object[] rowData = new Object[7];
                for (int i = 1; i <= 7; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                offersData.add(rowData);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offersData;
    }

    public void showGui(){
        GUI gui = new GUI(this);
    }
}
