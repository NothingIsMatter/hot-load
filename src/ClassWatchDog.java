
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.Arrays;
import java.util.logging.Logger;


import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class ClassWatchDog {
    private static final Logger LOGGER = Logger.getLogger("ClassWatchDog");
    private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

    private final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    private final StandardJavaFileManager manager = javaCompiler.getStandardFileManager(
            diagnostics, null, null);

    private final File file = new File(
            "src/TestModule.java");

    private final Iterable<? extends JavaFileObject> sources =
            manager.getJavaFileObjectsFromFiles(Arrays.asList(file));
    private WatchKey watchKey;
    private WatchService watcher = FileSystems.getDefault().newWatchService();
    private Path path;

    public ClassWatchDog() throws IOException {
        Path path = Paths.get("src/");
        this.watchKey = path.register(watcher, ENTRY_MODIFY);
    }

    public void listen() {
        WatchKey key;
        try {
            while ((key = watcher.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {

                    if (event.kind().equals(ENTRY_MODIFY)) {
                        int res = compile();

                        if (res == 0) {

                            LOGGER.info("Successfully compiled ");
                            Class<?> aClass = loadClass();
                            Object testModule = aClass.getDeclaredConstructor().newInstance();
                            System.out.println(testModule.toString());
                            testModule = null;
                        }
                    }
                }
                key.reset();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    // loads compiled java class to memory
    private Class<?> loadClass() {
        ClassLoader loader = TestModule.class.getClassLoader().getParent();
        try (CustomClassLoader urlClassLoader = new CustomClassLoader(new URL[]{TestModule.class.getProtectionDomain().getCodeSource().getLocation()}, loader);) {
            Class<?> reloaded = urlClassLoader.loadClass("TestModule");
            return reloaded;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    // Compiles modified java class
    private int compile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable sourcefiles = fileManager.getJavaFileObjects("src/TestModule.java");
        Iterable<String> options = Arrays.asList("-d", "out/production/learning/");
        compiler.getTask(null, fileManager, null, options, null, sourcefiles).call();
        return 0;

    }
}
