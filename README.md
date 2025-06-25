# security-vulnerabilities-examples
Examples of vulnerable applications and ways to make them secure

# Requirements
- Java 21
- Maven
- JAVA_HOME environment variable correctly configured

# Running demo applications


Each directory contains 2 demo applications: 
one with vulnerable code (directory: `before`) 
and one with vulnerability patched (directory: `after`)

To run any application go to correct directory (for example `./sql-injection/before`) and run:
```bash
./mvnw spring-boot:run
```

This will start the Postgres dockesr container and start the application (default port: 8080).