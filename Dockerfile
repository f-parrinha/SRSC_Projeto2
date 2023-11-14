# Get image with java installed
FROM openjdk:17-jdk-slim

# Set work dir
WORKDIR /app

# Copy jar to workdir
COPY target/*jar-with-dependencies.jar /app
