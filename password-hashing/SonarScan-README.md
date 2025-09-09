# SonarQube scan

To perform the scan first start SonarQube:
```bash
docker run --rm -d --name sonar -p 9000:9000 -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true sonarqube:latest
```

When SonarQube is ready go to http://localhost:9000 and configure the project:
1. Login (default login/password: `admin`/`admin`).
2. Create a new project with a key: `spring-boot-app`
3. Generate a project token and save it.

Now you can run the analysis from the project directory (`./vulnerable` or `./fixed`) with the following command (remember to replace `$PROJECT_TOKEN` with your generated token value):

```bash
docker run --rm --link sonar:sonar \
  -v "$(pwd):/usr/src/mymaven" \
  -v "$HOME/.m2:/root/.m2" \
  -w /usr/src/mymaven \
  maven:3.9.4-eclipse-temurin-21-alpine \
  mvn clean compile sonar:sonar \
    -Dsonar.projectKey=spring-boot-app \
    -Dsonar.host.url=http://sonar:9000 \
	-Dsonar.login=$PROJECT_TOKEN
```
