package com.exercise.attendance.services;

import com.exercise.attendance.model.WorkingHoursFilter;

public interface HoursReportValidator {
    void validateFilter(WorkingHoursFilter filter);
}
