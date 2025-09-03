# security-vulnerabilities-examples
Examples of vulnerable applications and ways to make them secure

# Requirements
- Java 21
- Maven
- JAVA_HOME environment variable correctly configured

# Running demo applications


Each directory contains 2 demo applications: 
one with vulnerable code (directory: `vulnerable`) 
and one with vulnerability patched (directory: `fixed`)

To run any application go to correct directory (for example `./sql-injection/vulnerable`) and run:
```bash
./mvnw spring-boot:run
```

This will start the Postgres docker container and start the application (default port: 8080).


# Docker 

You can also run demo applications as docker containers. Navigate to correct directory and run:
```bash
docker-compose up --build
```
