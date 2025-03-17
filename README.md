## ReadME

### Issues:

*Maven – Fatal error compiling: invalid target release*

Export JAVA_HOME to an appropriate Java SDK
```
export JAVA_HOME=/home/pablo/.jdks/openjdk-23.0.2
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

