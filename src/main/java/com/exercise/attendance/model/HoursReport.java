package com.exercise.attendance.model;

import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

public class HoursReport {

    private SortedMap<LocalDate, WorkingHours> report = new TreeMap<>();

    public SortedMap<LocalDate, WorkingHours> getReport() {
        return report;
    }

    public void addDailyReport(WorkingHours workingHours) {
        if (report.containsKey(workingHours.getDate()))
            throw new IllegalArgumentException("Working hours are already filled for date: " + workingHours.getDate());

        report.put(workingHours.getDate(), workingHours);
    }

    @Override
    public String toString() {
        return "HoursReport{" +
                "report=" + report +
                '}';
    }
}
