package com.exercise.attendance.dao;

import com.exercise.attendance.model.WorkingHours;
import com.exercise.attendance.model.WorkingHoursFilter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
public class WorkingHoursDaoImpl implements WorkingHoursDao {

    private static final String FETCH_WORKING_HOURS_SQL = "SELECT USER,DATE,FROM_TIME,TO_TIME FROM WORKING_HOURS " +
            "WHERE USER= :user " +
            "AND DATE >= :fromDate " +
            "AND DATE <= :toDate";
    private static final String UPSERT_WORKING_HOURS_SQL = "INSERT INTO WORKING_HOURS (USER, DATE, FROM_TIME, TO_TIME) " +
            "VALUES (:user, :date, :fromTime, :toTime) " +
            "ON DUPLICATE KEY UPDATE FROM_TIME=:fromTime, TO_TIME=:toTime";
    private WorkingHoursRowMapper mapper = new WorkingHoursRowMapper();
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public WorkingHoursDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<WorkingHours> getWorkingHours(WorkingHoursFilter filter) {
        return namedParameterJdbcTemplate.query(
                FETCH_WORKING_HOURS_SQL,
                Map.of("user", filter.getUser(),
                        "fromDate", filter.getFromDate(),
                        "toDate", filter.getToDate()),
                mapper
        );
    }

    @Override
    public void upsertWorkingHours(WorkingHours workingHours) {
        namedParameterJdbcTemplate.update(
                UPSERT_WORKING_HOURS_SQL,
                createUpsertParamsMap(workingHours)
        );
    }

    private Map<String, Object> createUpsertParamsMap(WorkingHours workingHours) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", workingHours.getUser());
        map.put("date", workingHours.getDate());
        map.put("fromTime", workingHours.getFromTime());
        map.put("toTime", workingHours.getToTime());
        return map;
    }

    private class WorkingHoursRowMapper implements RowMapper<WorkingHours> {

        @Override
        public WorkingHours mapRow(ResultSet rs, int rowNum) throws SQLException {
            String user = rs.getString("USER");
            LocalDate date = rs.getDate("DATE", Calendar.getInstance(TimeZone.getDefault())).toLocalDate();
            LocalTime fromTime = rs.getTime("FROM_TIME", Calendar.getInstance(TimeZone.getDefault())).toLocalTime();
            Time toTimeInRs = rs.getTime("TO_TIME", Calendar.getInstance(TimeZone.getDefault()));
            LocalTime toTime = toTimeInRs != null ? toTimeInRs.toLocalTime() : null;
            return new WorkingHours(user, date, fromTime, toTime);
        }
    }

}
