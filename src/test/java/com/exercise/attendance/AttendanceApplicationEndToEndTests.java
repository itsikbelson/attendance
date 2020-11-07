package com.exercise.attendance;

import com.exercise.attendance.model.ActivityType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/init-schema.sql", "/test-data.sql"})
class AttendanceApplicationEndToEndTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnReportBasedOnUrlParameters() {
        String response = restTemplate.getForObject("http://localhost:" + port + "/v1/report/alice?fromDate=2020-10-08&toDate=2020-10-10", String.class);
        String expectedReportJson = "{\"report\":{\"2020-10-08\":{\"user\":\"alice\",\"date\":\"2020-10-08\",\"fromTime\":\"08:00:00\",\"toTime\":\"17:00:00\"},\"2020-10-09\":{\"user\":\"alice\",\"date\":\"2020-10-09\",\"fromTime\":\"08:30:00\",\"toTime\":\"18:15:00\"},\"2020-10-10\":{\"user\":\"alice\",\"date\":\"2020-10-10\",\"fromTime\":\"09:30:00\",\"toTime\":\"16:15:00\"}}}";
        assertThat(response, is(expectedReportJson));
    }

    @Test
    void shouldReturnBadRequestWhenFromDateIsAfterToDate() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/v1/report/alice?fromDate=2020-10-18&toDate=2020-10-10", String.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void shouldUpdateReportWhenEntryIsAdded() {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/v1/report/bob", ActivityType.ENTRY, Object.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        String response = restTemplate.getForObject("http://localhost:" + port + "/v1/report/bob?fromDate=2020-10-08&toDate=" + LocalDate.now(), String.class);
        String expectedReportJsonStart = "{\"report\":{\"2020-10-09\":{\"user\":\"bob\",\"date\":\"2020-10-09\",\"fromTime\":\"10:30:00\",\"toTime\":\"15:00:00\"},\"2020-10-11\":{\"user\":\"bob\",\"date\":\"2020-10-11\",\"fromTime\":\"07:30:00\",\"toTime\":null},\"2020-11-07\":{\"user\":\"bob\",\"date\":\"2020-11-07\",\"fromTime\":";
        assertThat(response, startsWith(expectedReportJsonStart));
    }

    @Test
    void shouldReturnBadRequestWhenReportingExitOnExistingDate() {
        restTemplate.postForEntity("http://localhost:" + port + "/v1/report/carl", ActivityType.ENTRY, Object.class);
        restTemplate.postForEntity("http://localhost:" + port + "/v1/report/carl", ActivityType.EXIT, Object.class);
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/v1/report/carl", ActivityType.EXIT, Object.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }


}
