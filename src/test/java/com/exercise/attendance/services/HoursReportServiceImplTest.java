package com.exercise.attendance.services;

import com.exercise.attendance.dao.WorkingHoursDao;
import com.exercise.attendance.model.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class HoursReportServiceImplTest {

    private static final String DUMMY_USER = "alice";
    private static final WorkingHours DAY_1_WORKING_HOURS = new WorkingHours(DUMMY_USER, LocalDate.parse("2020-10-08"), LocalTime.parse("08:30:00"), LocalTime.parse("19:30:00"));
    private static final WorkingHours DAY_2_WORKING_HOURS = new WorkingHours(DUMMY_USER,LocalDate.parse("2020-10-09"), LocalTime.parse("08:00:00"), LocalTime.parse("17:15:00"));
    private static final WorkingHours ENTRY_ONLY_WORKING_HOURS = new WorkingHours(DUMMY_USER,LocalDate.parse("2020-10-09"), LocalTime.parse("08:00:00"));

    @Mock
    private HoursReportValidator validator;

    @Mock
    private WorkingHoursDao dao;

    private HoursReportService service;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new HoursReportServiceImpl(validator, dao);
    }

    @Test
    void shouldReturnHoursReportFromFetchedWorkingHours() {
        List<WorkingHours> workingHoursList = List.of(
                DAY_1_WORKING_HOURS,
                DAY_2_WORKING_HOURS);
        when(dao.getWorkingHours(any(WorkingHoursFilter.class)))
                .thenReturn(workingHoursList);
        HoursReport report = service.getReport(dummyFilter());
        assertReport(report, workingHoursList);
    }

    @Test
    void shouldReturnEmptyHoursReportWhenNoWorkingHours() {
        when(dao.getWorkingHours(any(WorkingHoursFilter.class)))
                .thenReturn(Lists.emptyList());
        HoursReport report = service.getReport(dummyFilter());
        assertReportSize(report, 0);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSameDayAppearTwiceInWorkingHours() {
        List<WorkingHours> workingHoursList = List.of(
                DAY_1_WORKING_HOURS,
                DAY_1_WORKING_HOURS);
        when(dao.getWorkingHours(any(WorkingHoursFilter.class)))
                .thenReturn(workingHoursList);
        assertThrows(IllegalArgumentException.class, () -> service.getReport(dummyFilter()));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenFilterIsInvalid() {
        doThrow(new IllegalArgumentException()).when(validator).validateFilter(any(WorkingHoursFilter.class));
        assertThrows(IllegalArgumentException.class, () -> service.getReport(dummyFilter()));
    }

    @Test
    void shouldAddWorkingHoursWhenNewEntry() {
        LocalDateTime reportedDateTime = LocalDateTime.now();
        when(dao.getWorkingHours(any(WorkingHoursFilter.class))).thenReturn(Lists.emptyList());
        WorkingHours updatedHours = service.reportActivity(new Activity(DUMMY_USER, ActivityType.ENTRY, reportedDateTime));
        assertThat(updatedHours.getDate(), is(reportedDateTime.toLocalDate()));
        assertThat(updatedHours.getFromTime(), is(reportedDateTime.toLocalTime()));
        assertThat(updatedHours.getToTime(), is((LocalTime)null));
    }

    @Test
    void shouldUpdateExitWhenEntryExists() {
        WorkingHours entryOnlyWorkingHours = ENTRY_ONLY_WORKING_HOURS;
        LocalDateTime reportedDateTime = LocalDateTime.of(entryOnlyWorkingHours.getDate(), entryOnlyWorkingHours.getFromTime()).plusHours(8);
        when(dao.getWorkingHours(any(WorkingHoursFilter.class))).thenReturn(List.of(entryOnlyWorkingHours));
        WorkingHours updatedHours = service.reportActivity(new Activity(DUMMY_USER, ActivityType.EXIT, reportedDateTime));
        assertThat(updatedHours.getDate(), is(reportedDateTime.toLocalDate()));
        assertThat(updatedHours.getFromTime(), is(entryOnlyWorkingHours.getFromTime()));
        assertThat(updatedHours.getToTime(), is(reportedDateTime.toLocalTime()));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenReportingEntryForExistingDate() {
        LocalDateTime reportedDateTime = LocalDateTime.now();
        when(dao.getWorkingHours(any(WorkingHoursFilter.class))).thenReturn(List.of(ENTRY_ONLY_WORKING_HOURS));
        assertThrows(IllegalArgumentException.class, () -> service.reportActivity(new Activity(DUMMY_USER, ActivityType.ENTRY, reportedDateTime)));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenReportingExitForExistingExit() {
        LocalDateTime reportedDateTime = LocalDateTime.now();
        when(dao.getWorkingHours(any(WorkingHoursFilter.class))).thenReturn(List.of(DAY_1_WORKING_HOURS));
        assertThrows(IllegalArgumentException.class, () -> service.reportActivity(new Activity(DUMMY_USER, ActivityType.EXIT, reportedDateTime)));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenReportingExitAndNoEntry() {
        LocalDateTime reportedDateTime = LocalDateTime.now();
        when(dao.getWorkingHours(any(WorkingHoursFilter.class))).thenReturn(Lists.emptyList());
        assertThrows(IllegalArgumentException.class, () -> service.reportActivity(new Activity(DUMMY_USER, ActivityType.EXIT, reportedDateTime)));
    }

    private void assertReport(HoursReport report, List<WorkingHours> workingHoursList) {
        assertReportSize(report, workingHoursList.size());
        workingHoursList.forEach(workingHours -> assertWorkingHoursInReport(report, workingHours));
    }

    private void assertReportSize(HoursReport report, int workingHoursList) {
        assertThat(report.getReport().size(), is(workingHoursList));
    }

    private void assertWorkingHoursInReport(HoursReport report, WorkingHours workingHours) {
        assertThat(report.getReport().get(workingHours.getDate()).getDate(), is(workingHours.getDate()));
        assertThat(report.getReport().get(workingHours.getDate()).getFromTime(), is(workingHours.getFromTime()));
        assertThat(report.getReport().get(workingHours.getDate()).getToTime(), is(workingHours.getToTime()));
    }

    private WorkingHoursFilter dummyFilter() {
        return new WorkingHoursFilter(DUMMY_USER, LocalDate.now(), LocalDate.now());
    }


}