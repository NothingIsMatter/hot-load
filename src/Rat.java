
import java.io.IOException;
import java.net.URISyntaxException;

public class Rat {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ClassWatchDog classWatchDog  =new ClassWatchDog();
        Thread th = new Thread(classWatchDog::listen);
        th.start();


    }
}
