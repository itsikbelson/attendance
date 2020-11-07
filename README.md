# Attendance 
## Overview
The purpose of this application is to allow attendance report by users (like a clock punch), and get daily working hours report.
The application exposes 2 main APIs:
* Fetching hours report for a specific user in a specified period:
```GET /v1/report/<user>?fromDate=<yyyy-MM-dd>&toDate=<yyyy-MM-dd>```<BR>
The report is returned in a Json format.
Sample:
```json
{
  "report": {
    "2020-10-09": {
      "user": "bob",
      "date": "2020-10-09",
      "fromTime": "10:30:00",
      "toTime": "15:00:00"
    },
    "2020-10-11": {
      "user": "bob",
      "date": "2020-10-11",
      "fromTime": "07:30:00",
      "toTime": null
    }
  }
}
```

* Report entry/exit activity (like a clock punch):
```POST /v1/report/<user>```<br>
body: `"ENTRY"` or `"EXIT"`
If report succeeds, HTTP response 201 (created) is returned.

## Build & Run
The assumption is that ATTENDANCE MySql database exists, and contains required tables.
Once this perquisite is met, you can run `./gradlew clean build test bootRun`


## Frameworks and tools
The following framworks and tools were used:
### Spring Boot
Spring Boot was used for a quick creation of the service, including:
* Spring MVC - for REST API
* Spring Data JDBC - for DB access. I preferred using JDBC over JPA, to have more control on the DB access.
* Spring Actuator - for some production features. For instance, in order to check the health of the application, you can use `GET /actuator/health`
* Spring Boot Testing - used for component and integration tests. Used `MockMvc` for testing the controllers.
### MySql
MySql was used as the database.
As mentioned above, the pre-requisite is to have MySql installed with ATTENDANCE schema.
A recommended enhancement will be to use Liquibase for DB migration, and use a pre-defined MySql docker for local runs.
### Gradle
Gradle was used as the build tool. It's common, conventional, and flexible.
### JUnit 5
Junit 5 was used for unit tests together with `Mockito` stubbing and `Hamcrest` matchers.

## Layers & Testing Methodology
### Application
* `AttendanceApplication` is the `SpringBootApplication` - no specific logic there besides setting timezone to `UTC`.
* `AttendanceApplicationEndToEndTests` includes the end-to-end tests for the application.<BR>
It starts the service, run some API calls, and checks the behavior as well as the responses.<BR>
These are black-box tests, and do not check any internals (e.g. DB content).<BR>
There's a script which inits the database and it's content according to what the test requires.<BR>
The APIs and expected responses are hard-coded, as I wanted it to be as straightforward as possible.<BR>
Ideally, switching to `Karate` will improve the way these tests look.
I've tested very limited number of scenarios (happy path and failures) in the end-to-end testing, as such tests are usually costly, and we have extended coverage in the unit and component tests. 
### Controller
* `HoursReportController` is the main controller and exposes the APIs detailed above.<BR>
* `HoursReportControllerTest` is used for the component test (using `SpringBootTest` and `MockMvc`).<BR>
It tests only the controller level (mocking the service), verifying the REST API layer behavior.
### Service
* `HoursReportService` is the interface representing the logic expected from the 2 APIs.
* `HoursReportServiceImpl` holds the implementation.<BR>
It validates the input using underlying validator.<BR>
It validates the operations (e.g. does not allow to report exit when there's no entry).<BR>
Note that the operation validation is done within the service, as it's tightly coupled to its logic. 
It uses the underlying DAO layer in order to fetch and save data.
* `HoursReportServiceImplTest` is a unit test checking inside validations and behavior.
### Validator
* `HoursReportValidator` interface and it's implementation in `HoursReportValidatorImpl` are used to validate the input for fetching the report.
* `HoursReportValidatorImplTest` is a unit test for the validator.
### DAO
* `WorkingHoursDao` and it's implementation in `WorkingHoursDaoImpl` are used for running the queries to get hours reports and add/update new reports.
* `WorkingHoursDaoImplIntegrationTest` is a `SpringBootTest` using a setup script for the test data, which verifies the get and upsert queries behavior.
### Database
* `WORKING_HOURS` is the table containing the working hours data.
The following SQL can be used to create the table (and represents its structure):
```SQL
CREATE TABLE WORKING_HOURS
(ID serial,
USER varchar(255),
DATE date,
FROM_TIME time,
TO_TIME time,
primary key (ID),
unique key WORKING_HOURS_USER_DATE_IX (USER, DATE));
```

## Assumptions
There are serveral assumptions I took during the implementation, which presents some limitations.
* No user management.
* No timezone handling.
* Working hours are within the same day.
* Only one working period is possible within the same day.

## What's Next?
There are many things which still needs to be done in the application.
Some of them are listed as `TODO` items under `AttendanceApplication`.
Below are outlined the main <B>technical</B> gaps:
* <B>Database</B> - add configurable DB location, add DB migration script, and have embedded DB for local runs.
* <B>Deployment</B> - wrap with Docker, provision instances, and write deployment scripts.
* <B>DTOs</B> - currently the model objects are exposed through the controller. A better approach is to convert them to DTOs and expose them. This will allow extracting client library in the future.
* <B>Integration Tests</B> - end-to-end test and DAO tests currently runs from within the "Unit Test" folder. Since integration tests are heavier, it's recommended to extract the to a different directory, which will allow exclusions during local runs. Also recommended to use `Karate` for the end-to-end API tests.
* <B>Error Handling</B> - I used a very naive approach to return `BadRequest` for problematic input. However, the application needs to return much more descriptive messages. Also need to replace the default whitelabel error page.
* <B>API Documentation</B> - Swagger support will allow easy documented operations on the service
* <B>Monitoring</B> - for a production-grade service, we need to add metrics, monitors and alerts.


