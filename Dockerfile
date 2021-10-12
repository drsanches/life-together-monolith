FROM openjdk:11

ADD ./build/libs/life-together-1.0.jar /app/
CMD ["java", "-jar", "/app/life-together-1.0.jar"]

EXPOSE ${PORT}