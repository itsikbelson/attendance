package com.exercise.attendance.controllers;

import com.exercise.attendance.model.*;
import com.exercise.attendance.services.HoursReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(HoursReportController.class)
class HoursReportControllerTest {

    private static final String REPORT_RESOURCE = "/v1/report";
    private static final String TEST_USER = "alice";
    private static final String TEST_USER_REPORT_RESOURCE = REPORT_RESOURCE + "/" + TEST_USER;

    private static final String FROM_PARAM_NAME = "fromDate";
    private static final String FROM_DATE = "2020-10-08";
    private static final String TO_PARAM_NAME = "toDate";
    private static final String TO_DATE = "2020-10-09";
    private static final String INVALID_DATE = "INVALID";


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HoursReportService service;

    private HoursReport sampleReport;


    @BeforeEach
    private void setUp() {
        sampleReport = sampleReport();
        when(service.getReport(any(WorkingHoursFilter.class))).thenReturn(sampleReport);
    }

    @Test
    void shouldReturnHoursReportWhenUserAndDateRangeIsValid() throws Exception {

        mockMvc.perform(
                get(TEST_USER_REPORT_RESOURCE)
                        .queryParam(FROM_PARAM_NAME, FROM_DATE)
                        .queryParam(TO_PARAM_NAME, TO_DATE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(reportContentMatcher(sampleReport));
    }

    @Test
    void shouldReturnOkWhenReportActivityIsValid() throws Exception {
        mockMvc.perform(
                post(TEST_USER_REPORT_RESOURCE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createActivityTypeJson())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void shouldReturnNotFoundWhenUserIsNotSpecified() throws Exception {
        mockMvc.perform(get(REPORT_RESOURCE))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenFromDateIsNotSpecified() throws Exception {
        mockMvc.perform(
                get(TEST_USER_REPORT_RESOURCE)
                        .queryParam(TO_PARAM_NAME, TO_DATE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.status().reason(missingParameterMatcher(FROM_PARAM_NAME)));
    }

    @Test
    void shouldReturnBadRequestWhenFromDateIsInInvalidFormat() throws Exception {
        mockMvc.perform(
                get(TEST_USER_REPORT_RESOURCE)
                        .queryParam(FROM_PARAM_NAME, INVALID_DATE)
                        .queryParam(TO_PARAM_NAME, TO_DATE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenFromDateIsEmpty() throws Exception {
        mockMvc.perform(
                get(TEST_USER_REPORT_RESOURCE)
                        .queryParam(FROM_PARAM_NAME, "")
                        .queryParam(TO_PARAM_NAME, TO_DATE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.status().reason(missingParameterMatcher(FROM_PARAM_NAME)));
    }

    @Test
    void shouldReturnBadRequestWhenToDateIsNotSpecified() throws Exception {
        mockMvc.perform(
                get(TEST_USER_REPORT_RESOURCE)
                        .queryParam(FROM_PARAM_NAME, FROM_DATE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.status().reason(missingParameterMatcher(TO_PARAM_NAME)));
    }

    @Test
    void shouldReturnBadRequestWhenToDateIsInInvalidFormat() throws Exception {
        mockMvc.perform(
                get(TEST_USER_REPORT_RESOURCE)
                        .queryParam(FROM_PARAM_NAME, FROM_DATE)
                        .queryParam(TO_PARAM_NAME, INVALID_DATE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenToDateIsEmpty() throws Exception {
        mockMvc.perform(
                get(TEST_USER_REPORT_RESOURCE)
                        .queryParam(FROM_PARAM_NAME, FROM_DATE)
                        .queryParam(TO_PARAM_NAME, ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.status().reason(missingParameterMatcher(TO_PARAM_NAME)));
    }

    @Test
    void shouldReturnBadRequestWhenGetReportThrowsIllegalArgumentException() throws Exception {
        doThrow(new IllegalArgumentException()).when(service).getReport(any(WorkingHoursFilter.class));
        mockMvc.perform(
                get(TEST_USER_REPORT_RESOURCE)
                        .queryParam(FROM_PARAM_NAME, FROM_DATE)
                        .queryParam(TO_PARAM_NAME, TO_DATE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenReportActivityTypeIsNotSpecified() throws Exception {
        doThrow(new IllegalArgumentException()).when(service).reportActivity(any(Activity.class));
        mockMvc.perform(
                post(TEST_USER_REPORT_RESOURCE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenReportActivityThrowsIllegalArgumentException() throws Exception {
        doThrow(new IllegalArgumentException()).when(service).reportActivity(any(Activity.class));
        mockMvc.perform(
                post(TEST_USER_REPORT_RESOURCE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createActivityTypeJson()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private Matcher<String> missingParameterMatcher(String paramName) {
        return Matchers.containsString("parameter '" + paramName + "' is not present");
    }

    private HoursReport sampleReport() {
        HoursReport report = new HoursReport();
        report.addDailyReport(new WorkingHours(TEST_USER, LocalDate.parse("2020-10-08"), LocalTime.parse("08:00"), LocalTime.parse("17:00")));
        report.addDailyReport(new WorkingHours(TEST_USER, LocalDate.parse("2020-10-09"), LocalTime.parse("09:15"), LocalTime.parse("17:00")));
        report.addDailyReport(new WorkingHours(TEST_USER, LocalDate.parse("2020-10-10"), LocalTime.parse("08:30"), LocalTime.parse("18:30")));
        return report;
    }

    private ResultMatcher reportContentMatcher(HoursReport report) {
        ResultMatcher[] matchers = report.getReport().values().stream()
                .map(this::dailyReportMatcher)
                .toArray(ResultMatcher[]::new);
        return ResultMatcher.matchAll(matchers);
    }

    private ResultMatcher dailyReportMatcher(WorkingHours workingHours) {
        return ResultMatcher.matchAll(
                elementMatch(workingHours, "date", workingHours.getDate().format(DateTimeFormatter.ISO_DATE)),
                elementMatch(workingHours, "fromTime", workingHours.getFromTime().format(DateTimeFormatter.ISO_TIME)),
                elementMatch(workingHours, "toTime", workingHours.getToTime().format(DateTimeFormatter.ISO_TIME))
        );
    }

    private ResultMatcher elementMatch(WorkingHours workingHours, String checkedElemeent, String expectedValue) {
        return MockMvcResultMatchers.jsonPath("$.report['" + workingHours.getDate() + "']." + checkedElemeent, Matchers.is(expectedValue));
    }

    private String createActivityTypeJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(ActivityType.ENTRY);
    }


}