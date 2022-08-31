package Assignment;

import org.jpl7.Query;
import org.jpl7.Term;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Application {
    public static final String _KNOWLEDGEBASE = "src/main/java/Assignment/knowledge_base.pl";
    public static void main(String[] args) throws IOException {
        final FileReader confirmedFile = new FileReader("src/main/resources/time_series_covid19_confirmed_global-2.csv");
        final FileReader deathsFile = new FileReader("src/main/resources/time_series_covid19_deaths_global-2.csv");
        final FileReader recoveredFile = new FileReader("src/main/resources/time_series_covid19_recovered_global-2.csv");
        if(COVIDReport.parseCSVIntoHashMap(confirmedFile, COVIDReport.confirmedMap) &&
                COVIDReport.parseCSVIntoHashMap(deathsFile, COVIDReport.deathsMap) &&
                COVIDReport.parseCSVIntoHashMap(recoveredFile, COVIDReport.recoveredMap)) {
            Query q = new Query("consult('" + _KNOWLEDGEBASE + "').");
            boolean isConnected = q.hasSolution();
            if (isConnected) {
                COVIDReport.WriteToKnowledgeBase();
                String queryString = "quick_sort_asc(" + Arrays.toString(COVIDReport.createMaxConfirmedMap().values().toArray()) + ",S).";
                String queryString2 = "msort_desc(" + Arrays.toString(COVIDReport.createMaxConfirmedMap().values().toArray()) + ",S).";
                q = new Query(queryString);
                Map<String, Term> result = q.oneSolution();
                List<Object> sortedAscValues = COVIDReport.convertListToIntList(Arrays.asList(
                                result.get("S").toString().split("\\s*,\\s*")).stream()
                        .map(i -> i.replaceAll("[\\s\\[\\]]", ""))
                        .toList(), Integer::parseInt);
                q = new Query(queryString2);
                List<Object> sortedDscValues = COVIDReport.convertListToIntList(Arrays.asList(
                                q.oneSolution().get("S").toString().split("\\s*,\\s*")).stream()
                        .map(i -> i.replaceAll("[\\s\\[\\]]", ""))
                        .toList(), Integer::parseInt);
                COVIDReport.generateSortedList(sortedAscValues, COVIDReport.sortedAscConfirmedList);
                COVIDReport.generateSortedList(sortedDscValues, COVIDReport.sortedDscConfirmedList);
                new UI();
            } else {
                System.out.println("knowledge_base.pl is not found or is corrupted.");
            }
        }
    }
}