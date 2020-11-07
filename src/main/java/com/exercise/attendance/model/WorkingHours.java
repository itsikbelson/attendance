package com.exercise.attendance.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

//TODO consider using a builder for WorkingHours
public class WorkingHours {
    private String user;
    private LocalDate date;
    private LocalTime fromTime;
    private LocalTime toTime;

    public WorkingHours(String user, LocalDate date, LocalTime fromTime) {
        this.user = user;
        this.date = date;
        this.fromTime = fromTime;
    }

    public WorkingHours(String user, LocalDate date, LocalTime fromTime, LocalTime toTime) {
        this.user = user;
        this.date = date;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public String getUser() {
        return user;
    }
    public LocalDate getDate() {
        return date;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    @Override
    public String toString() {
        return "WorkingHours{" +
                "user='" + user + '\'' +
                ", date=" + date +
                ", fromTime=" + fromTime +
                ", toTime=" + toTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkingHours hours = (WorkingHours) o;
        return user.equals(hours.user) &&
                date.equals(hours.date) &&
                fromTime.equals(hours.fromTime) &&
                Objects.equals(toTime, hours.toTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, date, fromTime, toTime);
    }
}
