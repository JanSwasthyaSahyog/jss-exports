# jss-exports

**To Build**
> ./gradlew clean buildRpm

This will create an rpm

**To Install**
> rpm -ivh \<rpm\>

**To run the service**
> service bahmni-export start

**To check status**
> service bahmni-export status

**Running an export** (Currently no UI available)
> curl 'http://localhost:8080/export?startDate=2016-01-01&endDate=2016-04-30' (Dates to be sent correctly)

