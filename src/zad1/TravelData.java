package zad1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TravelData {
    File dir;
    List<String> offersDescriptionsList;

    public TravelData(File dir){
        this.dir = dir;
    }
    public List<String> getOffersDescriptionsList(String locale, String dateformat){
        offersDescriptionsList = new ArrayList<>();
        try{
            Files.walkFileTree(Paths.get(dir.toURI()), new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().toLowerCase().endsWith(".txt")) {
                        List<String> lines = Files.readAllLines(file);

                        ResourceBundle resourceBundle = ResourceBundle.getBundle("offersResource", new Locale(locale));
                            for (String line : lines) {

                                int counter = 0;
                                String[] splitted = line.split("\t");


                                String inputLocale = splitted[0];

                                String[] splittedOriginalLocale = inputLocale.split("_");
                                String splittedLocale[] = locale.split("_");

                                Locale realOriginalLocale = null;

                                if (splittedOriginalLocale.length == 1) {
                                    realOriginalLocale = new Locale(splittedOriginalLocale[0], splittedOriginalLocale[0].toUpperCase());
                                } else {
                                    realOriginalLocale = new Locale(splittedOriginalLocale[0], splittedOriginalLocale[1]);
                                }

                                if(inputLocale.equals(locale) || inputLocale.equals(splittedLocale[0])) {
                                   line = extractLocale(line);
                                   line = line.replace("\t", " ");
                                    offersDescriptionsList.add(line);

                                    continue;
                                }

                                StringBuilder str = new StringBuilder();
                                String country = "";
                                boolean ifCountry = true;
                                for(String word : splitted){

                                    if(counter == 0){
                                        counter++;
                                        continue;
                                    }
                                    if(counter == splitted.length - 1){
                                        str.append(word);
                                        continue;
                                    }
                                    if(counter == splitted.length - 2){

                                        str.append(translateCurrency(word, realOriginalLocale, new Locale(splittedLocale[0], splittedLocale[1])) + " ");
                                        counter++;

                                        continue;
                                    }

                                    if(Character.isDigit(word.charAt(0))) {
                                        if (ifCountry) {

                                            String toTranslate = country.substring(0, country.length() - 1);
                                            Locale resultLocale = new Locale(splittedLocale[0], splittedLocale[1]);
                                            String translatedCountry = translateCountry(toTranslate, realOriginalLocale, resultLocale);
                                            str.append(translatedCountry + " ");

                                            counter++;
                                            
                                        }
                                        str.append(word + " ");
                                        counter++;
                                        ifCountry = false;
                                    }
                                    else{
                                        if(ifCountry){
                                            country += word + " ";
                                            continue;
                                        }
                                        str.append(resourceBundle.getString(word) + " ");
                                        counter++;
                                    }
                                }
                                offersDescriptionsList.add(str.toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return offersDescriptionsList;
    }


    private static String extractLocale(String input) {
        int tabIndex = input.indexOf('\t');

        if (tabIndex != -1) {
            return input.substring(tabIndex + 1, input.length());
        }

        return "";
    }
    public static String translateCountry(String toTranslate, Locale originalLocale, Locale resultLocale){
        for (Locale availableLocale : Locale.getAvailableLocales()) {
            if (availableLocale.getDisplayCountry(originalLocale).equals(toTranslate)) {
                return availableLocale.getDisplayCountry(resultLocale);
            }
        }
        return "";
    }
    public static String translateCurrency(String word, Locale originalLocale, Locale resultLocale){
        NumberFormat oldFormat = NumberFormat.getNumberInstance(originalLocale);

        try{
            Number number = oldFormat.parse(word);
            NumberFormat newFormat = NumberFormat.getNumberInstance(resultLocale);

            String newNumber = newFormat.format(number);
            return newNumber;
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return " ";
    }
}
