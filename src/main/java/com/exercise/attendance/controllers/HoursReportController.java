package com.exercise.attendance.controllers;

import com.exercise.attendance.model.ActivityType;
import com.exercise.attendance.model.HoursReport;
import com.exercise.attendance.model.WorkingHoursFilter;
import com.exercise.attendance.model.Activity;
import com.exercise.attendance.services.HoursReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("v1/report/{user}")
public class HoursReportController {

    private HoursReportService service;

    private Logger logger = LoggerFactory.getLogger(HoursReportController.class);

    public HoursReportController(HoursReportService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public HoursReport getReport(@PathVariable String user,
                                 @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                 @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws ServletRequestBindingException {

        validateDateParametersAreNotEmpty(fromDate, toDate);

        WorkingHoursFilter filter = new WorkingHoursFilter(user,fromDate,toDate);
        logger.debug("Requested hours report. Used filter: " + filter);
        try {
            return service.getReport(filter);
        } catch (IllegalArgumentException ex) {
            throw new ServletRequestBindingException("Hours report filter is invalid. " + ex.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void report(@PathVariable String user,
                       @RequestBody ActivityType activityType) throws ServletRequestBindingException {
        logger.debug("Reporting activity " + activityType + " for user " + user);
        try {
            service.reportActivity(new Activity(user, activityType, LocalDateTime.now()));
        } catch (IllegalArgumentException ex) {
            throw new ServletRequestBindingException("Activity report failed. " + ex.getMessage());
        }
    }

    // Spring MVC considers empty values as valid.
    // Here we enforce empty values are invalid
    private void validateDateParametersAreNotEmpty(LocalDate fromDate, LocalDate toDate) throws MissingServletRequestParameterException {
        validateFromDateIsNotEmpty(fromDate);
        validateToDateIsNotEmpty(toDate);
    }

    private void validateFromDateIsNotEmpty(LocalDate fromDate) throws MissingServletRequestParameterException {
        validateDateParameterIsNotEmpty(fromDate, "fromDate");
    }

    private void validateToDateIsNotEmpty(LocalDate toDate) throws MissingServletRequestParameterException {
        validateDateParameterIsNotEmpty(toDate, "toDate");
    }

    private void validateDateParameterIsNotEmpty(LocalDate date, String paramName) throws MissingServletRequestParameterException {
        if (date == null) {
            throw new MissingServletRequestParameterException(paramName, LocalDate.class.getSimpleName());
        }
    }

}
