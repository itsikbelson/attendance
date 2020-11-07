package com.exercise.attendance.services;

import com.exercise.attendance.model.Activity;
import com.exercise.attendance.model.HoursReport;
import com.exercise.attendance.model.WorkingHours;
import com.exercise.attendance.model.WorkingHoursFilter;

public interface HoursReportService {

    HoursReport getReport(WorkingHoursFilter filter);

    WorkingHours reportActivity(Activity activity);
}
