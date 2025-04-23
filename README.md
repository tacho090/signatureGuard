## ReadME

### Issues:

*Maven – Fatal error compiling: invalid target release*

Export JAVA_HOME to an appropriate Java SDK
```
export JAVA_HOME=/home/pablo/.jdks/openjdk-21.0.2
```

*OpenCV documentation*

```commandline
https://docs.opencv.org/4.10.0/
```

*Unable to find opencv_4.9.0 package*

Add the following block to the Main.java program so that it may load the library statically

```
    static {
        System.out.println("Core version: " + Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadLocally();
    }
```

*Unable to find Class*

```commandline
mvn clean compile
mvn exec:java -Dexec.mainClass=com.signatureGuard.OpenCVTest
```

*Run Server*

``mvn spring-boot:``

Pending:     "message": "Error: images are either empty or null. Cannot process image.",

Steps to run this in a new computer:

1. Add these two variables to the bashrc
```shell
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME:$PATH

```
2. java -version
3. mvn -version
4. These two above have to return something
5. Install java compiler with

```shell
sudo apt install openjdk-21-jdk
```
6. mvn clean compile
7. mvn spring-boot:run

For debugging
https://github.com/tacho090/signatureGuard/blob/58830910ca3e18b085da39476a6ef2a3eaa456ee/src/main/java/com/siameseNetwork/SiameseSigNetCompare.java

2. 
