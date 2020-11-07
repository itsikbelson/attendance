package com.exercise.attendance.dao;

import com.exercise.attendance.model.WorkingHours;
import com.exercise.attendance.model.WorkingHoursFilter;

import java.util.List;

public interface WorkingHoursDao {
    List<WorkingHours> getWorkingHours(WorkingHoursFilter filter);

    void upsertWorkingHours(WorkingHours workingHours);
}
