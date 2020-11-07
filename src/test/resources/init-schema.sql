DROP TABLE IF EXISTS WORKING_HOURS;

CREATE TABLE WORKING_HOURS
(
    ID        serial,
    USER      varchar(255),
    DATE      date,
    FROM_TIME time,
    TO_TIME   time,
    primary key (ID),
    unique key WORKING_HOURS_USER_DATE_IX (USER, DATE)
);