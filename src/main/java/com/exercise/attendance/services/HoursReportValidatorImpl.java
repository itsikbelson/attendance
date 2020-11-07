package com.exercise.attendance.services;

import com.exercise.attendance.model.WorkingHoursFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class HoursReportValidatorImpl implements HoursReportValidator {

    public void validateFilter(WorkingHoursFilter filter) {
        validateUser(filter);
        validateDateRage(filter);
    }

    private void validateDateRage(WorkingHoursFilter filter) {
        validateFromDateIsNotEmpty(filter);
        validateToDateIsNotEmpty(filter);
        validateFromDateIsNotAfterToDate(filter);
    }

    private void validateFromDateIsNotAfterToDate(WorkingHoursFilter filter) {
        if (filter.getFromDate().isAfter(filter.getToDate())) {
            throw new IllegalArgumentException("fromDate cannot be after toDate");
        }
    }

    private void validateToDateIsNotEmpty(WorkingHoursFilter filter) {
        if (filter.getToDate() == null)
            throw new IllegalArgumentException("toDate cannot be empty");
    }

    private void validateFromDateIsNotEmpty(WorkingHoursFilter filter) {
        if (filter.getFromDate() == null)
            throw new IllegalArgumentException("fromDate cannot be empty");
    }

    private void validateUser(WorkingHoursFilter filter) {
        ValidateUserIsNotMissing(filter);
    }

    private void ValidateUserIsNotMissing(WorkingHoursFilter filter) {
        if (StringUtils.isBlank(filter.getUser()))
            throw new IllegalArgumentException("user cannot be empty");
    }
}
