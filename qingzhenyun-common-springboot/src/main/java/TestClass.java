import java.util.HashMap;
import java.util.Map;

public class TestClass {
    public void test() {
        Map<String, ? extends String> ab = new HashMap<String, String>();
    }
}
