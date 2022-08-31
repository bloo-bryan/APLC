package Assignment;

import com.opencsv.exceptions.CsvException;
import org.jpl7.Query;
import org.jpl7.Term;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Application {
    public static final String _KNOWLEDGEBASE = "src/main/java/Assignment/knowledge_base.pl";
    public static void main(String[] args) throws IOException, CsvException {
        final FileReader confirmedFile = new FileReader("src/main/resources/time_series_covid19_confirmed_global-2.csv");
        final FileReader deathsFile = new FileReader("src/main/resources/time_series_covid19_deaths_global-2.csv");
        final FileReader recoveredFile = new FileReader("src/main/resources/time_series_covid19_recovered_global-2.csv");
        COVIDReport.parseCSVIntoHashMap(confirmedFile, COVIDReport.confirmedMap); //this is boolean
        COVIDReport.parseCSVIntoHashMap(deathsFile, COVIDReport.deathsMap); //this is boolean
        COVIDReport.parseCSVIntoHashMap(recoveredFile, COVIDReport.recoveredMap);

        Query q = new Query("consult('"+_KNOWLEDGEBASE+"').");
        boolean isConnected = q.hasSolution();
        if(isConnected) {
            COVIDReport.WriteToKnowledgeBase();
            String queryString = "quick_sort(" + Arrays.toString(COVIDReport.createMaxConfirmedMap().values().toArray()) + ",S).";
            String queryString2 = "msort(" + Arrays.toString(COVIDReport.createMaxConfirmedMap().values().toArray()) + ",S).";
            q = new Query(queryString);
            Map<String, Term> result = q.oneSolution();
            List<Object> sortedAscValues = COVIDReport.convertStrtoIntList(Arrays.asList(
                    result.get("S").toString().split("\\s*,\\s*")).stream()
                    .map(i -> i.replaceAll("[\\s\\[\\]]", ""))
                    .toList(), Integer::parseInt);
            q = new Query(queryString2);
            List<Object> sortedDscValues = COVIDReport.convertStrtoIntList(Arrays.asList(
                    q.oneSolution().get("S").toString().split("\\s*,\\s*")).stream()
                    .map(i -> i.replaceAll("[\\s\\[\\]]", ""))
                    .toList(), Integer::parseInt);
            COVIDReport.generateSortedList(sortedAscValues, COVIDReport.sortedAscConfirmedList);
            COVIDReport.generateSortedList(sortedDscValues, COVIDReport.sortedDscConfirmedList);
            System.out.println(COVIDReport.sortedAscConfirmedList);
            System.out.println(COVIDReport.sortedDscConfirmedList);
            new UI();
//            COVIDReport.sortedConfirmedList.add(0, sortedValues);
//            COVIDReport.sortedConfirmedList.get(0).stream().forEachOrdered(x -> {
//                Query q2;
//                q2 = new Query("consult('"+_KNOWLEDGEBASE+"').");
//                String qs = "fact(X," + x + ").";
//                q2 = new Query(qs);
//                Map<String, Term> map = q2.oneSolution();
//                System.out.println(map.get("X"));
//            });

        }

//        System.out.println(COVIDReport.readConfirmedIntoList());
//        System.out.println(COVIDReport.readDeathIntoList());
//
//        System.out.println(COVIDReport.confirmedList.size());
//        System.out.println(COVIDReport.getLowestDeathDateByCountry.apply("Argentina"));
//        System.out.println(COVIDReport.getRecordByCountry().apply(COVIDReport.deathList).apply("Argentina"));
//        COVIDReport.parseCSVIntoHashMap(confirmedFile, COVIDReport.confirmedMap); //this is boolean
//        COVIDReport.parseCSVIntoHashMap(deathsFile, COVIDReport.deathsMap); //this is boolean
//        COVIDReport.parseCSVIntoHashMap(recoveredFile, COVIDReport.recoveredMap); //this is boolean
//        System.out.println(COVIDReport.deathsMap.get("Cambodia"));
//        //System.out.println(COVIDReport.add().apply(COVIDReport.deathsMap).apply("China"));
//        new UI();
//
            //1. read from all csv

            //2. create new hashmap/list from original map with (country: max confirmed)
            //3. iterate through map/list and write to pl KB facts

            //4. consult KB

            //5. sort using map.values().toarray()

            //6. create new list of lists where index[0] is sorted array and index[1] is mapped values from KB facts

            //


    }
}
