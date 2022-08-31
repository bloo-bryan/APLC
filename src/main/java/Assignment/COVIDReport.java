package Assignment;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
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
    static HashMap<String, List<Integer>> confirmedMap = new HashMap<>();
    static HashMap<String, List<Integer>> deathsMap = new HashMap<>();
    static HashMap<String, List<Integer>> recoveredMap = new HashMap<>();
    static List<String[]> deathList = new ArrayList<>();
    static List<List<Object>> sortedAscConfirmedList = new ArrayList<>();
    static List<List<Object>> sortedDscConfirmedList = new ArrayList<>();


    public static boolean parseCSVIntoHashMap(FileReader file, HashMap<String, List<Integer>> map) {
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
                            convertListToIntList(Arrays.asList(tokens[4].split("\\s*,\\s*")).stream()
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
                model.addRow(new Object[]{key, Collections.max(confirmedMap.get(key))}));
        return model;
    }

    public static DefaultTableModel addConfirmedTotalToTable(String country) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region", "Total Confirmed Cases"});
        model.addRow(new Object[]{country, Collections.max(confirmedMap.get(country))});
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

                %sort ascending
                quick_sort_asc([],[]).
                quick_sort_asc([H|T], Sorted) :-
                    partition(H,T,L,G),
                    quick_sort_asc(L, SortedL),
                    quick_sort_asc(G, SortedG),
                    append(SortedL,[H|SortedG],Sorted).

                partition(_,[],[],[]).
                partition(P,[H|T],[H|L],G) :-
                    H =< P,
                    partition(P,T,L,G).
                partition(P,[H|T],L,[H|G]) :-
                    H > P,
                    partition(P,T,L,G).

                %sort descending
                msort_desc([],[]).
                msort_desc(List, Sorted) :- sort(0, @>=, List, Sorted).""");
        fw.close();
    }

    public static void generateSortedList(List<Object> result, List<List<Object>> sortedList) {
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
    }

    public static DefaultTableModel addSortedConfirmedTotalToTable(List<List<Object>> sortedList) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Country/Region", "Total Confirmed Cases"});
        IntStream.range(0, sortedList.get(0).size()).forEachOrdered(i -> {
            model.addRow(new Object[]{sortedList.get(1).get(i), sortedList.get(0).get(i)});
        });
        return model;
    }

    // GENERICS
    public static <T, K> List<K> convertListToIntList(List<T> strList, Function<T, K> converter) {
        return strList.stream().map(converter).collect(Collectors.toList());
    }

    // CURRYING
    public static Function<List<String[]>, Function<String, List<String>>> getRecordByCountry() {
        return list -> (country -> Arrays.asList(Objects.requireNonNull(
                list.stream().filter(i -> Arrays.asList(i).contains(country))
                        .findFirst().orElse(null))));
    }

    // FUNCTIONAL COMPOSITION
    public static Function<String, Integer> getLowestDeathIndex =
            country -> Collections.min(convertListToIntList(getRecordByCountry().apply(deathList).apply(country), Integer::parseInt));
    public static Function<Integer, String> getLowestDeathDateByIndex =
            index -> Arrays.asList(deathList.get(0)).get(index);
    public static Function<String, String> getLowestDeathDateByCountry =
            getLowestDeathIndex.andThen(getLowestDeathDateByIndex);


}
