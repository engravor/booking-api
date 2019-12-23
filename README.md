# Rest API for Volcano Island booking
The current API provide basic behavior for camp booking. It allows multiple users to generate and cancell reservations 
according to all requested limitations.  

### Documetation
In order to provide basic knowledge of API endpoints, it has been included Swagger. 
Full documentation can be seen on http://localhost:8080/swagger-ui.html (after the app is up).

### Run App
In order to run the app two different profiles had been included (test and production).
For development purpose "test" profile is the default and it includes an H2 database (which will restart every time the app is restarted).

On "production" profile, will be found all necessary configuration for MySQL database (any other DB could be used but dependencies must be included on pom.xml first).
If this profile wanted to be used it only requires to add `-Dspring-boot.run.profiles=production` param after the `mvn spring-boot:run` command (used to run the app)

```mvn spring-boot:run -Dspring-boot.run.profiles=production```
   