FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar ewm_main.jar
ENTRYPOINT ["java","-jar","/ewm_main.jar"]