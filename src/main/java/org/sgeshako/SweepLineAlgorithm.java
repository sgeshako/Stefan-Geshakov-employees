package org.sgeshako;

import java.time.LocalDate;
import java.util.*;

public final class SweepLineAlgorithm {

    private SweepLineAlgorithm() {}

    enum EventType { START(1), END(-1);

        final int value;
        EventType(int value) {
            this.value = value;
        }
    }

    public record Event (LocalDate date, EventType type, Long personId) {}
    
    static Map<Pair<Long>, Overlap> calculate(List<Employee> employees) {
        var events = new ArrayList<Event>();
        for (var employee : employees) {
            events.add(new Event(employee.getFrom(), EventType.START, employee.getId()));
            events.add(new Event(employee.getTo(), EventType.END, employee.getId()));
        }

        events.sort((e1, e2) -> !e1.date().isEqual(e2.date()) ? e1.date().compareTo(e2.date()) : e1.type().value - e2.type().value);

        final Map<Pair<Long>, Overlap> personsOverlapMap = new HashMap<>();

        Set<Long> currentOverlappingEmployees = new LinkedHashSet<>();

        for (var event : events) {

            if (event.type == EventType.START) {

                currentOverlappingEmployees
                        .forEach(otherEmployeeId -> {
                            if (!personsOverlapMap.containsKey(Pair.of(event.personId, otherEmployeeId))) {
                                personsOverlapMap.put(
                                        Pair.of(event.personId, otherEmployeeId),
                                        new Overlap().withBegin(event.date));
                            }
                        });

                // Add after we're done iterating employees
                currentOverlappingEmployees.add(event.personId);

            } else if (event.type == EventType.END) {
                // Remove before we begin iterating employees
                currentOverlappingEmployees.remove(event.personId);

                currentOverlappingEmployees
                        .forEach(otherEmployeeId -> {
                            personsOverlapMap.compute(
                                    Pair.of(event.personId, otherEmployeeId),
                                    (key, overlap) -> overlap.withEnd(event.date));
                        });
            }
        }

        return personsOverlapMap;
    }
}
