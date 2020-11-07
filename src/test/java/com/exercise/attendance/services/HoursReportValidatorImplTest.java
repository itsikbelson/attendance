package com.exercise.attendance.services;

import com.exercise.attendance.model.WorkingHoursFilter;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HoursReportValidatorImplTest {
    private static final String USER = "alice";
    private static final LocalDate FROM_DATE = LocalDate.parse("2020-10-08");
    private static final LocalDate TO_DATE = LocalDate.parse("2020-10-10");

    private HoursReportValidator validator = new HoursReportValidatorImpl();

    @Test
    void shouldNotThrowExceptionWhenFilterIsValid() {
        validator.validateFilter(new WorkingHoursFilter(USER, FROM_DATE, TO_DATE));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUserIsNull() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFilter(new WorkingHoursFilter(null, FROM_DATE, TO_DATE)));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUserIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFilter(new WorkingHoursFilter("", FROM_DATE, TO_DATE)));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenFromDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFilter(new WorkingHoursFilter(USER, null, TO_DATE)));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionQhenToDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFilter(new WorkingHoursFilter(USER, FROM_DATE, null)));
    }

}