
# Solar Farm

## TODO

### New Tasks

#### Choose a Project

* [x] Choose a project that manages a single model from earlier in the course

#### Database Schema

* [x] Create a one-table database schema to manage your model
  * Use the model's properties to determine column names and data types
  * Decide on a primary key and key strategy (auto_increment or application-generated)

#### JdbcTemplate Repository

* [x] Add the appropriate Spring dependencies to pom.xml
* [x] If the project doesn't use Spring annotation configuration, update it to use annotation configuration
* [x] Create a test-specific Spring configuration class
* [x] Create a new concrete implementation of the project's repository interface that uses JdbcTemplate
  * Do not rewrite an existing repository
* [x] Start with empty method bodies that return a default value
* [x] Generate tests for the JdbcTemplate repository and complete methods one at a time with accompanying tests
* [x] Consider creating a test database to safely execute tests and establish known good state

#### Spring Boot

* [x] Add Spring Boot parent, MVC, and DevTools dependencies to pom.xml
* [x] Add a @RestController to your project that supports all operations from your project's domain service class
* [x] Determine appropriate HTTP response statuses for each domain outcome
  * ResponseEntity<T> is helpful here, though it's perfectly acceptable to start by returning the raw output of each domain method
* [x] Configure the repositories DataSource in application.properties
* [x] Modify your main method and class to launch with Spring Boot
* [x] Run the app
  * Exercise service endpoints with REST Client
  * With each operation, confirm the response is correct and data is updated in the database

##### Bonus

* [x] Existing service tests shouldn't need to be modified since there's already a repository test double
  * Test controller methods with a mocked MVC environment
* [x] Implement global exception handling with @ControllerAdvice and @ExceptionHandler
  * One "catch all" handler is allowed, but try to catch more specific exceptions as well

#### Validation API

* [x] Add the validation dependency to pom.xml
* [x] Add validation constraint annotations to your model
  * Messages aren't required (there's a default), but they could come in handy
  * Try to replace all current validations with annotations
* [x] Manually validate the model with ValidatorFactory in the service and remove Java validation methods or conditions
  * Iterate over `Set<ConstraintViolation<ModelType>>` to populate your service response with messages
* [x] Write at least one test to trigger a validation failure
  * If you already have validation tests, run them and confirm they're working as before
* [x] Add automatic validation with the `@Valid` annotation and `BindingResult` in a `@RestController`

##### Stretch Goals

* [x] If your model includes validations that can't be represented with constraint annotations, create a custom validator class and annotation
  * Even if validation is straight-forward, try custom validation
  * Pick something weird that requires more than one field and validate it

### Original Tasks

* [x] Define the models
  * [x] Define the `SolarPanel` class
  * [x] Define the `Material` enumeration
* [x] Define the data layer
  * [x] Define the `DataAccessException` class
  * [x] Define the `SolarPanelRepository` interface
  * [x] Define the `SolarPanelFileRepository` class
  * [x] Create seed data
  * [x] Test the repository
* [x] Define the domain layer
  * [x] Define the `SolarPanelService` class
  * [x] Test the service
* [x] Define the UI layer
  * [x] Define the `TextIO` interface
  * [x] Define the `ConsoleIO` class
  * [x] Define the `View` class
  * [x] Define the `Controller` class
* [x] Define the `App` class

## High Level Requirements

The user is a solar farm administrator.

- Add a solar panel to the farm.
- Update a solar panel.
- Remove a solar panel.
- Display all solar panels in a section.

## Background

Solar panels are arranged in sections, rows, and columns. A panel can be uniquely identified with those three things.

## Panels

The application is only required to track solar panels. The concept of **sections** is not a separate class. It is a field in the solar panel class.

### Data

- **Section**: name that identifies where the panel is installed.
- **Row**: the row number in the section where the panel is installed.
- **Column**: the column number in the section where the panel is installed.
- **Year Installed**
- **Material**: multicrystalline silicon, monocrystalline silicon, amorphous silicon, cadmium telluride, or copper indium gallium selenide.
- **Is Tracking**: determines if the panel is installed with sun-tracking hardware.

Panels can be uniquely identified by section, row, and column.

### Rules

- **Section** is required and cannot be blank.
- **Row** is a positive number less than or equal to 250.
- **Column** is a positive number less than or equal to 250.
- **Year Installed** must be in the past.
- **Material** is required and can only be one of the five materials listed.
- **Is Tracking** is required.
- The combined values of **Section**, **Row**, and **Column** may not be duplicated.

## Sample UI

### Start Up / Main Menu

```
Welcome to Solar Farm
=====================

Main Menu
=========
0. Exit
1. Find Panels by Section
2. Add a Panel
3. Update a Panel
4. Remove a Panel
Select [0-4]:
```

### Find Panels by Section

```
Find Panels by Section
======================

Section Name: The Ridge

Panels in The Ridge
Row Col Year Material Tracking
  1   1 2014     CIGS      yes
  1   2 2014     GIGS      yes
  1   3 2015     GIGS      yes
  2   1 2018   PolySi       no
  2   3 2018   PolySi       no
```

### Add a Panel

```
Add a Panel
===========

Section: Flats
Row: 251
[Err]
Row must be between 1 and 250.
Row: 250
Column: 43
Material: CdTe
Installation Year: 2020
Tracked [y/n]: n

[Success] 
Panel Flats-250-43 added.
```

### Update a Panel

Section, Row, or Column are not required to be updatable, but editing of other fields is required.

```
Update a Panel
==============

Section: Treeline
Row: 10
Column: 5

Editing Treeline-10-5
Press [Enter] to keep original value.

Section (Treeline):
Row (10): 11
Column (5):
Material (CdTe):
Installation Year (2020):
Tracked (no) [y/n]: y

[Success] 
Panel Treeline-11-5 updated.
```

### Remove a Panel - Success

```
Remove a Panel
==============

Section: Flats
Row: 50
Column: 50

[Success] 
Panel Flats-50-50 removed.
```

### Remove a Panel - Failure

```
Remove a Panel
==============

Section: Flats
Row: 20
Column: 21

[Err] 
There is no panel Flats-20-21.
```

## Technical Requirements

Use a three-layer architecture.

Data must be stored in a delimited file. Stopping and starting the application should not change the underlying data. The application picks up where it left off.

Repositories should throw a custom exception, never file-specific exceptions.

Repository and service classes must be fully tested with both negative and positive cases. Do not use your "production" data file to test your repository.

Solar panel material should be a Java enum with five values. Since solar technology is changing quickly, an enum may be a risky choice. The requirement is included specifically to practice with enums.
