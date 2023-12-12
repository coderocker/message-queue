#!/bin/bash

# Stop on the first sign of trouble
set -e

echo "Starting the build process."

# Set JAVA_HOME if necessary
# export JAVA_HOME=/path/to/java

# Display Java version, useful for logs
echo "Java version:"
java -version

# If needed, you can setup environment variables or perform preliminary steps
# echo "Setting up environment variables..."

# Clean the project. This ensures you're starting from a clean state.
echo "Cleaning the project..."
./mvnw clean

# Resolve all dependencies.
echo "Resolving dependencies..."
./mvnw dependency:resolve

# Compile the project.
echo "Compiling the project..."
./mvnw compile

# Run any tests. You can skip this step if you don't have tests or don't want to run them.
echo "Running tests..."
./mvnw test

# Package the project. This typically creates a JAR/WAR file depending on your project type.
echo "Packaging the project..."
./mvnw package

# If there are additional steps like copying build artifacts, they can be done here.
# For example:
# cp target/myapp.jar some/destination

echo "Build completed successfully."
