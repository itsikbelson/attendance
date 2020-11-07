package com.exercise.attendance.model;

import java.time.LocalDate;

public class WorkingHoursFilter {

    private String user;
    private LocalDate fromDate;
    private LocalDate toDate;

    public WorkingHoursFilter(String user, LocalDate fromDate, LocalDate toDate) {
        this.user = user;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getUser() {
        return user;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    @Override
    public String toString() {
        return "WorkingHoursFilter{" +
                "user='" + user + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
