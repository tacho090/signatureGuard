# SignatureGuard

## Project Overview

SignatureGuard is a sophisticated signature verification application that uses deep learning techniques to analyze and compare handwritten signatures for authenticity. The system employs a Siamese neural network architecture to determine the similarity between signature images and detect potential forgeries.

## Architecture

The project is structured as a modular Java application with the following components:

1. **UI Module**: A JavaFX-based user interface that allows users to select and compare signature images.
2. **Siamese Network Module**: Core functionality for processing and comparing signatures using neural networks.
3. **API Module**: REST API endpoints for handling signature upload and verification requests.
4. **Utilities Module**: Common utilities used across the application.

## Key Features

- Signature comparison using Siamese neural networks
- ONNX model implementation for efficient inference
- User-friendly JavaFX interface for uploading and comparing signatures
- RESTful API for programmatic access to signature verification
- Image preprocessing and normalization

## Technologies Used

- Java 21
- Maven for dependency management and build automation
- OpenCV for image processing 
- ONNX Runtime for neural network inference
- Spring Boot for the API backend
- JavaFX for the user interface

## Setup and Installation

### System Requirements
- Java 21 (OpenJDK)
- Maven 3.6+

### Steps to run this in a new computer:

1. Add these two variables to the bashrc
```shell
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME:$PATH
```

2. Verify Java and Maven installations:
```shell
java -version
mvn -version
```
   These two commands should return valid version information.

3. Install Java compiler if needed:
```shell
sudo apt install openjdk-21-jdk
```

4. Build the project:
```shell
mvn clean compile
```

5. Run the application:
```shell
mvn spring-boot:run
```

6. Run the ui:
```shell
cd ui
mvn clean compile exec:java
```

## Troubleshooting

### Issues:

#### Maven – Fatal error compiling: invalid target release

Export JAVA_HOME to an appropriate Java SDK
```
export JAVA_HOME=/home/pablo/.jdks/openjdk-21.0.2
```

#### OpenCV documentation

```
https://docs.opencv.org/4.10.0/
```

#### Unable to find opencv_4.9.0 package

Add the following block to the Main.java program so that it may load the library statically:

```java
static {
    System.out.println("Core version: " + Core.NATIVE_LIBRARY_NAME);
    OpenCV.loadLocally();
}
```

#### Unable to find Class

```
mvn clean compile
mvn exec:java -Dexec.mainClass=com.signatureGuard.OpenCVTest
```

#### Run Server

```
mvn spring-boot:run
```

#### Pending Issues

- "message": "Error: images are either empty or null. Cannot process image."

## Development Resources

For debugging and development reference:
- [SiameseSigNetCompare.java](https://github.com/tacho090/signatureGuard/blob/58830910ca3e18b085da39476a6ef2a3eaa456ee/src/main/java/com/siameseNetwork/SiameseSigNetCompare.java)

