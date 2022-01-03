FROM openjdk:11
EXPOSE 8082
ADD target/workmotion-employee.jar workmotion-employee.jar
ENTRYPOINT ["java","-jar","workmotion-employee.jar"]