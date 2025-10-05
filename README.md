<img src="https://javastart.pl/img/jakartaee_logo.jpg" width="30%" height="auto">
# Car Management App

Code for the Jakarta Enterprise Edition project in the 7th semester.
## Tech Stack
- Java 17 (Eclipse Temurin)
- Jakarta EE 10
- Open Liberty
- Maven

Tested on Eclipse Temurin 17 JDK

## How to build and run the applicaton

#### 1. Pull this repository from GitHub
```bash
git clone https://github.com/Pablo1618/JEE-Car-Manager
```
#### 2. Compile and run the program using:
```bash
./mvnw clean package liberty:run
```
or use Maven:
```bash
mvn liberty:dev
```

#### 3. Once the runtime starts, you can access the project at http://localhost:9080