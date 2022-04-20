import org.junit.jupiter.api.Test;

public class JavaTest {


    @Test
    public void test() {
        Character c = '5';
        int n = Character.digit(c, 10);
        System.out.println(n);
    }
}
