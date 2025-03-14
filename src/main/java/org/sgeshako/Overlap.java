package org.sgeshako;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@AllArgsConstructor
@With
@Getter
public class Overlap {
    private LocalDate begin;
    private LocalDate end;

    public long getTotalDurationInDays() {
        return ChronoUnit.DAYS.between(begin, end);
    }
}