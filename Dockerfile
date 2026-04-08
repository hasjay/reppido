FROM amazoncorretto:17
ADD target/reppido.jar reppido.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "reppido.jar", "--spring.profiles.active=prod"]
