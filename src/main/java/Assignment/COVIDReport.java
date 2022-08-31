package Assignment;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.jpl7.Query;
import org.jpl7.Term;

import javax.swing.table.DefaultTableModel;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class COVIDReport {
    public static List<String[]> confirmedList = new ArrayList<>(); //CHANGE TO PRIVATE
    public static List<String[]> deathList = new ArrayList<>();
    public static HashMap<String, List<Integer>> confirmedMap = new HashMap<>();
    public static HashMap<String, List<Integer>> deathsMap = new HashMap<>();
    public static HashMap<String, List<Integer>> recoveredMap = new HashMap<>();
    //public static HashMap<String, HashMap<String, List<Integer>>> fullMap = new HashMap<>();
    public static List<List<Object>> sortedAscConfirmedList = new ArrayList<>();
    public static List<List<Object>> sortedDscConfirmedList = new ArrayList<>();


    public static boolean readConfirmedIntoList() throws IOException, CsvException {
        FileReader file = new FileReader("src/main/resources/time_series_covid19_confirmed_global-2.csv");
        try(CSVReader csvReader = new CSVReader(file)) {
            confirmedList = csvReader.readAll();
        }
        return true;
    }

    public static boolean readDeathIntoList() throws IOException, CsvException {
        FileReader file = new FileReader("src/main/resources/time_series_covid19_deaths_global-2.csv");
        try(CSVReader csvReader = new CSVReader(file)) {
            deathList = csvReader.readAll();
        }
        return true;
    }

    public static void removeDuplicateCountryFromList() {
        //deathList.stream().filter(i -> i.equals(deathList.indexOf(generateFrequencyMapForDuplicateCountries(deathList).get())))
        //IntStream.range(0, 890).skip(4).map(i -> IntStream.)
    }
    public static boolean parseCSVIntoHashMap(FileReader file, HashMap<String, List<Integer>> map) throws IOException {
        try(CSVReader csvReader = new CSVReaderBuilder(file).withSkipLines(1).build()) {
            String[] nextline;
            while((nextline = csvReader.readNext()) != null) {
                String[] tokens = Arrays.toString(nextline).split(",", 5);
                if(map.containsKey(tokens[1].trim())) {
                    List<Integer> newList = Arrays.stream(tokens[4].split("\\s*,\\s*"))
                            .map(i -> i.replaceAll("[\\s\\]]", ""))
                            .map(Integer::parseInt).toList();
                    map.put(tokens[1].trim(), IntStream.range(0, newList.size())
                            .mapToObj(i -> newList.get(i) + map.get(tokens[1].trim())
                                            .get(i)).collect(Collectors.toList()));
                } else {
                    map.put(tokens[1].trim(),
                            convertStrtoIntList(Arrays.asList(tokens[4].split("\\s*,\\s*")).stream()
                                    .map(i -> i.replaceAll("[\\s\\]]", ""))
                                    .toList(), Integer::parseInt));
                }
            }
        } catch(Exception ex) {
            System.out.println("Parsing csv to hashmap failed.");
            return false;
        }
        return true;
    }

    public static DefaultTableModel addConfirmedTotalToTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region", "Total Confirmed Cases"});
        confirmedMap.forEach((key, value) ->
                model.addRow(new Object[]{key, value.get((confirmedMap.get(key).size()-1))}));
        return model;
    }

    public static DefaultTableModel addConfirmedTotalToTable(String country) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region", "Total Confirmed Cases"});
        model.addRow(new Object[]{country, confirmedMap.get(country)
                .get(confirmedMap.get(country).size()-1)});
        return model;
    }

    public static DefaultTableModel addHighestLowestToTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region", "Highest Deaths",
                "Lowest Deaths", "Highest Recoveries", "Lowest Recoveries"});
        deathsMap.forEach((key, value) ->
                model.addRow(new Object[]{key, Collections.max(deathsMap.get(key)),
                        returnNonZeroMinimumInt(deathsMap.get(key)),
                        Collections.max(recoveredMap.get(key)),
                        returnNonZeroMinimumInt(recoveredMap.get(key))}));
        return model;
    }

    public static DefaultTableModel addHighestLowestToTable(String country) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region", "Highest Deaths",
                "Lowest Deaths", "Highest Recoveries", "Lowest Recoveries"});
        model.addRow(new Object[]{country, Collections.max(deathsMap.get(country)),
                returnNonZeroMinimumInt(deathsMap.get(country)),
                Collections.max(recoveredMap.get(country)),
                returnNonZeroMinimumInt(recoveredMap.get(country))});
        return model;
    }

    public static DefaultTableModel addCfmDeathsRecvrToTable(String country) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region",
                "Total Confirmed Cases", "Total Deaths", "Total Recoveries"});
        model.addRow(new Object[]{country, Collections.max(confirmedMap.get(country)),
                Collections.max(deathsMap.get(country)), Collections.max(recoveredMap.get(country))});
        return model;
    }

    public static DefaultTableModel addCfmDeathsRecvrToTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region",
                "Total Confirmed Cases", "Total Deaths", "Total Recoveries"});
        deathsMap.forEach((key, value) ->
                model.addRow(new Object[]{key, Collections.max(confirmedMap.get(key)),
                        Collections.max(deathsMap.get(key)), Collections.max(recoveredMap.get(key))}));
        return model;
    }

    public static int returnNonZeroMinimumInt(List<Integer> list) {
        return list.stream().filter(i -> i != 0).findFirst().orElse(0);
    }

    public static HashMap<String, Integer> createMaxConfirmedMap() {
        HashMap<String, Integer> map = new HashMap<>();
        confirmedMap.forEach((key, value) -> {
            map.put(key, Collections.max(value));
        });
        return map;
    }

    public static void WriteToKnowledgeBase() throws IOException {  //boolean?
        FileWriter fw = new FileWriter("src/main/java/Assignment/knowledge_base.pl");
        createMaxConfirmedMap().forEach((key, value) -> {
            try {
                fw.write("fact('"+key+"',"+value+").\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fw.write("""
                
                quick_sort([],[]).
                quick_sort([H|T], Sorted) :-
                    partition(H,T,L,G),
                    quick_sort(L, SortedL),
                    quick_sort(G, SortedG),
                    append(SortedL,[H|SortedG],Sorted).

                partition(_,[],[],[]).
                partition(P,[H|T],[H|L],G) :-
                    H =< P,
                    partition(P,T,L,G).
                partition(P,[H|T],L,[H|G]) :-
                    H > P,
                    partition(P,T,L,G).
                   \s
                msort([],[]).
                msort(List, Sorted) :- sort(0, @>=, List, Sorted).""");
        fw.close();
    }

    public static boolean generateSortedList(List<Object> result, List<List<Object>> sortedList) {
        sortedList.add(0, result);
        List<Object> sortedCountries = new ArrayList<>();
        result.stream().forEachOrdered(x -> {
            Query q2;
            q2 = new Query("consult('"+"src/main/java/Assignment/knowledge_base.pl"+"').");
            String qs = "fact(X," + x + ").";
            q2 = new Query(qs);
            Map<String, Term> map = q2.oneSolution();
            sortedCountries.add(map.get("X").toString().replaceAll("'", ""));
        });
        sortedList.add(1, sortedCountries);
        return true;
    }

    public static DefaultTableModel addSortedConfirmedTotalToTable(List<List<Object>> sortedList) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region", "Total Confirmed Cases"});
        IntStream.range(0, sortedList.get(0).size()).forEachOrdered(i -> {
            model.addRow(new Object[]{sortedList.get(1).get(i), sortedList.get(0).get(i)});
        });
        return model;
    }

    public static HashMap<String, Integer> generateFrequencyMapForDuplicateCountries(List<String[]> list) {
        HashMap<String, Integer> countryFrequencyMap = new HashMap<>();
        for(String[] s: list) {
            if(countryFrequencyMap.containsKey(s[1])) countryFrequencyMap.put(s[1], countryFrequencyMap.get(s[1]) + 1);
            else countryFrequencyMap.put(s[1], 1);
        }
        return countryFrequencyMap;
    }

    // GENERICS
    public static <T, K> List<K> convertStrtoIntList(List<T> strList, Function<T, K> converter) {
        return strList.stream().map(converter).collect(Collectors.toList());
    }

    public static int totalConfirmedCasesByCountry(String country) {
        return confirmedMap.get(country).stream().reduce(0, Integer::sum);
    }

//    public static List<String> function(List<String[]> list, String country) {
//        return Arrays.asList(Objects.requireNonNull(list.stream().filter(i -> Arrays.asList(i).contains(country)).findFirst().orElse(null)));
//    }
    // CURRYING
    public static Function<List<String[]>, Function<String, List<String>>> getRecordByCountry() {
        return list -> (country -> Arrays.asList(Objects.requireNonNull(
                list.stream().filter(i -> Arrays.asList(i).contains(country))
                        .findFirst().orElse(null))));
    }


    // FUNCTIONAL COMPOSITION
    public static Function<String, Integer> getLowestDeathIndex =
            country -> getRecordByCountry().apply(deathList).apply(country).lastIndexOf("0") + 1;
    public static Function<Integer, String> getLowestDeathDateByIndex =
            index -> Arrays.asList(deathList.get(0)).get(index);
    public static Function<String, String> getLowestDeathDateByCountry = getLowestDeathIndex.andThen(getLowestDeathDateByIndex);


}
