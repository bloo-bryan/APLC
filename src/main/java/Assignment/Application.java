package Assignment;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.IsoFields;
import java.util.*;

public class Application {
    public static void main(String[] args) throws IOException, CsvException {
        FileReader confirmedFile = new FileReader("src/main/resources/time_series_covid19_confirmed_global-2.csv");
        FileReader deathsFile = new FileReader("src/main/resources/time_series_covid19_deaths_global-2.csv");
        FileReader recoveredFile = new FileReader("src/main/resources/time_series_covid19_recovered_global-2.csv");
//        try(CSVReader csvReader = new CSVReader(file)) {
//            String[] values = null;
//            while((values = csvReader.readNext()) != null) {
//                List<String> array = Arrays.asList(values);
//                System.out.println(array);
//            }
//        }
        System.out.println(COVIDReport.readConfirmedIntoList());
        System.out.println(COVIDReport.readDeathIntoList());

        System.out.println(COVIDReport.confirmedList.size());
        System.out.println(COVIDReport.getLowestDeathDateByCountry.apply("Argentina"));
        System.out.println(COVIDReport.getRecordByCountry().apply(COVIDReport.deathList).apply("Argentina"));
        COVIDReport.parseCSVIntoHashMap(confirmedFile, COVIDReport.confirmedMap); //this is boolean
        COVIDReport.parseCSVIntoHashMap(deathsFile, COVIDReport.deathsMap); //this is boolean
        COVIDReport.parseCSVIntoHashMap(recoveredFile, COVIDReport.recoveredMap); //this is boolean
        System.out.println(COVIDReport.deathsMap.get("Cambodia"));
        //System.out.println(COVIDReport.add().apply(COVIDReport.deathsMap).apply("China"));
        new UI();
    }
}
