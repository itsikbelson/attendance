package com.exercise.attendance.dao;

import com.exercise.attendance.model.WorkingHours;
import com.exercise.attendance.model.WorkingHoursFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@Sql({"/init-schema.sql", "/test-data.sql"})
class WorkingHoursDaoImplIntegrationTest {

    @Autowired
    private WorkingHoursDao dao;

    @Test
    void shouldReturnWorkingHoursForUserForFilteredDates() {
        List<WorkingHours> workingHours = dao.getWorkingHours(new WorkingHoursFilter("alice", LocalDate.parse("2020-10-07"), LocalDate.parse("2020-10-09")));
        assertThat(workingHours.size(), is(2));
        assertDate(workingHours.get(0), "2020-10-08");
        assertDate(workingHours.get(1), "2020-10-09");
    }

    @Test
    void shouldNotReturnWorkingHoursForUserIfDatesAreOutOfRange() {
        List<WorkingHours> workingHours = dao.getWorkingHours(new WorkingHoursFilter("bob", LocalDate.parse("2020-10-07"), LocalDate.parse("2020-10-08")));
        assertThat(workingHours.size(), is(0));
    }

    @Test
    void shouldNotReturnWorkingHoursForUserWithoutWorkingHours() {
        List<WorkingHours> workingHours = dao.getWorkingHours(new WorkingHoursFilter("carl", LocalDate.parse("2000-10-07"), LocalDate.parse("2050-10-08")));
        assertThat(workingHours.size(), is(0));
    }

    @Test
    void shouldAddEntryWhenNoRecordExists() {
        WorkingHours newWorkingHours = new WorkingHours("david", LocalDate.parse("2020-10-11"), LocalTime.parse("07:45:00"));
        dao.upsertWorkingHours(newWorkingHours);
        List<WorkingHours> actualWorkingHours = dao.getWorkingHours(new WorkingHoursFilter("david", LocalDate.parse("2000-10-07"), LocalDate.parse("2050-10-08")));
        assertWorkingHours(actualWorkingHours, newWorkingHours);
    }

    @Test
    void shouldUpdateExitWhenEntryExists() {
        WorkingHours newWorkingHours = new WorkingHours("eli", LocalDate.parse("2020-10-11"), LocalTime.parse("06:45:00"));
        dao.upsertWorkingHours(newWorkingHours);

        WorkingHours updatedWorkingHours = new WorkingHours(newWorkingHours.getUser(), newWorkingHours.getDate(), newWorkingHours.getFromTime(), LocalTime.parse("17:20:00"));
        dao.upsertWorkingHours(updatedWorkingHours);

        List<WorkingHours> actualWorkingHours = dao.getWorkingHours(new WorkingHoursFilter("eli", LocalDate.parse("2000-10-07"), LocalDate.parse("2050-10-08")));
        assertWorkingHours(actualWorkingHours, updatedWorkingHours);
    }

    private void assertWorkingHours(List<WorkingHours> actualWorkingHours, WorkingHours expectedWorkingHours) {
        assertThat(actualWorkingHours.size(), is(1));
        WorkingHours dayWorkingHours = actualWorkingHours.get(0);
        assertDate(dayWorkingHours, expectedWorkingHours.getDate());
        assertFromTime(dayWorkingHours, expectedWorkingHours.getFromTime());
        assertToTime(dayWorkingHours, expectedWorkingHours.getToTime());
    }

    private void assertToTime(WorkingHours workingHours, LocalTime expectedTime) {
        assertTime(workingHours.getToTime(), expectedTime);
    }

    private void assertFromTime(WorkingHours workingHours, LocalTime expectedTime) {
        assertTime(workingHours.getFromTime(), expectedTime);
    }

    private void assertDate(WorkingHours workingHours, String expectedDate) {
        assertDate(workingHours, LocalDate.parse(expectedDate));
    }

    private void assertDate(WorkingHours workingHours, LocalDate expectedDate) {
        assertThat(workingHours.getDate(), is(expectedDate));
    }

    private void assertTime(LocalTime time, LocalTime expectedTime) {
        assertThat(time, is(expectedTime));
    }


}