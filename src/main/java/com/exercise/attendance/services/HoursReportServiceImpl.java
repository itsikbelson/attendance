package com.exercise.attendance.services;

import com.exercise.attendance.dao.WorkingHoursDao;
import com.exercise.attendance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HoursReportServiceImpl implements HoursReportService {

    private final Logger logger = LoggerFactory.getLogger(HoursReportServiceImpl.class);

    private final HoursReportValidator validator;
    private final WorkingHoursDao dao;

    public HoursReportServiceImpl(HoursReportValidator validator, WorkingHoursDao dao) {
        this.validator = validator;
        this.dao = dao;
    }

    @Override
    public HoursReport getReport(WorkingHoursFilter filter) {

        validator.validateFilter(filter);
        logger.debug("Fetching hours report. Filter: " + filter);

        List<WorkingHours> workingHours = dao.getWorkingHours(filter);
        logger.debug("Fetched " + workingHours.size() + " for hour report. Filter: " + filter);

        return buildHoursReport(workingHours);
    }

    @Override
    public WorkingHours reportActivity(Activity activity) {
        logger.debug("Reporting activity.");
        Optional<WorkingHours> existingDayWorkingHours = getExistingWorkingHours(activity.getUser(), activity.getReportDateTime());

        logger.debug("Calculating updated working hours. Existing working hours: " + existingDayWorkingHours);
        WorkingHours updatedWorkingHours = calculateWorkingHours(activity.getUser(), activity.getActivityType(), activity.getReportDateTime());

        logger.debug("Saving updated working hours: " + updatedWorkingHours);
        saveUpdatedWorkingHours(updatedWorkingHours);

        return updatedWorkingHours;
    }

    private void saveUpdatedWorkingHours(WorkingHours updatedWorkingHours) {
        dao.upsertWorkingHours(updatedWorkingHours);
    }

    private Optional<WorkingHours> getExistingWorkingHours(String user, LocalDateTime reportDateTime) {
        WorkingHoursFilter todayFilter = new WorkingHoursFilter(user, reportDateTime.toLocalDate(), reportDateTime.toLocalDate());
        List<WorkingHours> existingWorkingHours = dao.getWorkingHours(todayFilter);
        return existingWorkingHours.stream().findFirst();
    }

    private WorkingHours calculateWorkingHours(String user, ActivityType activityType, LocalDateTime reportDateTime) {
        Optional<WorkingHours> existingWorkingHours = getExistingWorkingHours(user, reportDateTime);

        switch (activityType) {
            case ENTRY:
            default:
                return calculateWorkingHoursForEntry(user, existingWorkingHours, reportDateTime);
            case EXIT:
                return calculateWorkingHoursForExit(user, existingWorkingHours, reportDateTime);
        }
    }

    private WorkingHours calculateWorkingHoursForExit(String user, Optional<WorkingHours> existingWorkingHours, LocalDateTime reportDateTime) {
        WorkingHours workingHours = validateExitReport(existingWorkingHours, reportDateTime);
        return new WorkingHours(user, reportDateTime.toLocalDate(), workingHours.getFromTime(), reportDateTime.toLocalTime());
    }

    private WorkingHours validateExitReport(Optional<WorkingHours> existingWorkingHours, LocalDateTime reportDateTime) {
        if (existingWorkingHours.isEmpty()) {
            throw new IllegalArgumentException("Cannot report exit. Entry was not reported at " + reportDateTime.toLocalDate());
        }
        WorkingHours workingHours = existingWorkingHours.get();
        if (workingHours.getToTime() != null) {
            throw new IllegalArgumentException("Cannot report exit. Exit was already reported at " + workingHours.getToTime());
        }
        return workingHours;
    }

    private WorkingHours calculateWorkingHoursForEntry(String user, Optional<WorkingHours> existingWorkingHours, LocalDateTime reportDateTime) {
        validateEntryReport(existingWorkingHours, reportDateTime);
        return new WorkingHours(user, reportDateTime.toLocalDate(), reportDateTime.toLocalTime());
    }

    private void validateEntryReport(Optional<WorkingHours> existingWorkingHours, LocalDateTime reportDateTime) {
        if (existingWorkingHours.isPresent()) {
            throw new IllegalArgumentException("Cannot report entry for user. Entry was already reported for " + reportDateTime.toLocalDate());
        }
    }

    private HoursReport buildHoursReport(List<WorkingHours> workingHours) {
        HoursReport report = new HoursReport();
        workingHours.forEach(report::addDailyReport);
        return report;
    }

}
