# hot-load
Hot load for Java

This is base version in which you can change TestModule#toString method and track output in running programm with no need to recompile.

To test it you may need to change in ClassWatchDog.

```java
        Iterable sourcefiles = fileManager.getJavaFileObjects("src/TestModule.java");
        Iterable<String> options = Arrays.asList("-d", "out/production/learning/");
```

