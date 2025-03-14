package org.sgeshako;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class Employee {

    private Long id;
    private Long projectId;
    private LocalDate from;
    private LocalDate to;
}
