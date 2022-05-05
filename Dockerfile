FROM openjdk:11
EXPOSE 8080
ARG JAR_FILE=target/java-spring-boot-mongodb-starter-1.0.0.jar
ADD ${JAR_FILE} backend.jar
ENTRYPOINT ["java","-jar","/backend.jar"]