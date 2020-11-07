package com.exercise.attendance.model;

import java.time.LocalDateTime;

public class Activity {
    private final String user;
    private final ActivityType activityType;
    private final LocalDateTime reportDateTime;

    public Activity(String user, ActivityType activityType, LocalDateTime reportDateTime) {
        this.user = user;
        this.activityType = activityType;
        this.reportDateTime = reportDateTime;
    }

    public String getUser() {
        return user;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "user='" + user + '\'' +
                ", activityType=" + activityType +
                ", reportDateTime=" + reportDateTime +
                '}';
    }

}
