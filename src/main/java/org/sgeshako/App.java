package org.sgeshako;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.groupingBy;

/**
 * Application that identifies the pair of employees who have worked
 * together on common projects for the longest period of time.
 *
 */
public class App
{
    static final String EMPLOYEE_ID = "EmpID";
    static final String PROJECT_ID = "ProjectID";
    static final String DATE_FROM = "DateFrom";
    static final String DATE_TO = "DateTo";

    public static void main( String[] args ) throws IOException {

        String csvFilePath = "src/main/resources/input.csv";

        final Map<Long, List<Employee>> employeesPerProjectMap =
                parseCsv(csvFilePath);

        final Map<Pair<Long>, Long> pairEmployeesTotalOverlap =
                employeesPerProjectMap.values()
                        .stream()
                        .map(SweepLineAlgorithm::calculate)
                        .flatMap(pairOverlapMap -> pairOverlapMap.entrySet().stream())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                pairOverlapEntry -> pairOverlapEntry.getValue().getTotalDurationInDays(),
                                Long::sum
                        ));
//        pairEmployeesTotalOverlap
//                .forEach((longPair, aLong) -> System.out.println(longPair + ": " + aLong + " total duration in days." ));

        // Which pair of employees have worked together the longest in total.
        final Pair<Long> highestOverlapEmployeePair =
                pairEmployeesTotalOverlap
                        .entrySet()
                        .stream()
                        .reduce((pairEntry, pairEntry2) -> pairEntry.getValue() > pairEntry2.getValue() ? pairEntry : pairEntry2)
                        .map(Map.Entry::getKey)
                        .orElseThrow(() -> new RuntimeException("Error: Couldn't find highest overlap pair of employees in " + employeesPerProjectMap.size() + " projects."));


        System.out.println("Employee ID #1, Employee ID #2, Project ID, Days worked");
        employeesPerProjectMap
                .forEach((projectIdKey, projectEmployees) -> {
                    SweepLineAlgorithm.calculate(projectEmployees)
                            .entrySet()
                            .stream()
                            .filter(pairOverlapEntry -> pairOverlapEntry.getKey().equals(highestOverlapEmployeePair))
                            .forEach(pairOverlapEntry -> {
                                var pair = pairOverlapEntry.getKey();
                                var overlap = pairOverlapEntry.getValue();
                                System.out.print(pair.getFirst());
                                System.out.print("\t");
                                System.out.print(pair.getSecond());
                                System.out.print("\t");
                                System.out.print(projectIdKey);
                                System.out.print("\t");
                                System.out.print(overlap.getTotalDurationInDays());
                                System.out.println();
                            });
                });
    }

    static Map<Long, List<Employee>> parseCsv(String csvFilePath) throws IOException {
        try (Reader in = new FileReader(csvFilePath)) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(EMPLOYEE_ID, PROJECT_ID, DATE_FROM, DATE_TO)
                    .setIgnoreSurroundingSpaces(true)
                    .setNullString("NULL")
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(in);

            return StreamSupport.stream(records.spliterator(), false)
                    .map(App::parseEmployee)
                    .collect(groupingBy(Employee::getProjectId));
        }
    }

    static Employee parseEmployee(CSVRecord csvRecord) {
        return Employee.builder()
                .id(Long.valueOf(csvRecord.get(EMPLOYEE_ID)))
                .projectId(Long.valueOf(csvRecord.get(PROJECT_ID)))
                .from(LocalDate.parse(
                        csvRecord.get(DATE_FROM),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .to(csvRecord.get(DATE_TO) != null ?
                        LocalDate.parse(
                                csvRecord.get(DATE_TO),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        : LocalDate.now())
                .build();
    }
}
