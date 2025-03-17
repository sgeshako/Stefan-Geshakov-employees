# Java CSV Processing: Identify Longest Employee Overlaps
A Maven-packaged fat JAR application

## Task description: 
### Objective: 
Create an application that identifies the pair of employees who have worked together on common projects for the longest period of time.

### Sample input
A CSV file with the following format: `EmpID, ProjectID, DateFrom, DateTo`
```csv
112, 25, 2025-03-01, 2025-03-10
113, 25, 2012-03-04, 2025-03-12
114, 25, 2025-01-01, NULL
115, 25, 2025-01-01, 2025-01-31
112, 19, 2025-01-01, 2025-01-09
113, 19, 2025-01-03, 2025-01-08
117, 19, 2025-01-01, 2025-01-31
```

## How to build and run
1) Clone repo and run Maven build to create fat JAR:
```sh
git clone https://github.com/sgeshako/Stefan-Geshakov-employees.git EmployeesOverlap
cd EmployeesOverlap

mvn clean package
```

2) Run the generated JAR without arguments and select .csv file from File system:
```sh
java -jar target/EmployeesOverlap-1.0-SNAPSHOT.jar

# or pass the path to .csv file as argument:
java -jar target/EmployeesOverlap-1.0-SNAPSHOT.jar src/main/resources/input.csv
```

## Expected output:
```
[Employee ID #1, Employee ID #2, Project ID, Days worked]
113     114     25      70
```
