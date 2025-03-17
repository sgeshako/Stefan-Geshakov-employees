package org.sgeshako;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

        boolean consoleMode = args.length != 0;

        final String csvFilePath;
        if (consoleMode) {
            csvFilePath = args[0];
        } else {
            csvFilePath = Gui.openDialog();
        }

        // Group employees per project
        final Map<Long, List<Employee>> employeesPerProjectMap =
                parseCsv(csvFilePath);

        // Calculate for every project all overlaps that occurred between employees
        final Map<Long, Map<Pair<Long>, Overlap>> perProjectPairEmployeesOverlaps =
                employeesPerProjectMap.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                employeesPerProjectEntry -> SweepLineAlgorithm.calculate(employeesPerProjectEntry.getValue())
                        ));

        // For each unique pair of employees sum their total overlap duration across all projects
        final Map<Pair<Long>, Long> pairEmployeesTotalOverlapDuration =
            perProjectPairEmployeesOverlaps
                    .values()
                    .stream()
                    .flatMap(pairOverlapMap -> pairOverlapMap.entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            pairOverlapEntry -> pairOverlapEntry.getValue().getTotalDurationInDays(),
                            Long::sum
                    ));

        // Which pair of employees have worked together the longest in total.
        final Pair<Long> highestOverlapEmployeePair =
                pairEmployeesTotalOverlapDuration
                        .entrySet()
                        .stream()
                        .reduce((pairEntry, pairEntry2) -> pairEntry.getValue() > pairEntry2.getValue() ? pairEntry : pairEntry2)
                        .map(Map.Entry::getKey)
                        .orElseThrow(() -> new RuntimeException("Error: Couldn't find highest overlap pair of employees in " + employeesPerProjectMap.size() + " projects."));


        // Print output
        final List<String[]> printRecords = new ArrayList<>();

        perProjectPairEmployeesOverlaps
                .forEach((projectId, pairOverlapMap) -> {
                        if (pairOverlapMap.containsKey(highestOverlapEmployeePair)) {
                            var overlap = pairOverlapMap.get(highestOverlapEmployeePair);
                            printRecords.add(new String[]{
                                    String.valueOf(highestOverlapEmployeePair.getFirst()),
                                    String.valueOf(highestOverlapEmployeePair.getSecond()),
                                    String.valueOf(projectId),
                                    String.valueOf(overlap.getTotalDurationInDays())
                            });
                        }
                });

        String[] headers = { "Employee ID #1", "Employee ID #2", "Project ID", "Days worked" };
        if (!consoleMode) {
            Gui.showDialog(headers, printRecords.toArray(new String[0][]));
        } else {
            System.out.println(Arrays.toString(headers));
            printRecords.forEach(rowData -> System.out.println(String.join("\t", rowData)));
        }
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
                .from(DateUtil.parse(csvRecord.get(DATE_FROM)))
                .to(csvRecord.get(DATE_TO) != null ?
                        DateUtil.parse(csvRecord.get(DATE_TO))
                        : LocalDate.now())
                .build();
    }
}
