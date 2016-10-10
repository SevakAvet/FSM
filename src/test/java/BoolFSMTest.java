import com.sun.xml.internal.rngom.parse.host.Base;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by savetisyan on 11/10/16
 */
public class BoolFSMTest extends BaseFSMTest{
    public BoolFSMTest() {
        super("lex/bool.json");
    }

    @DataProvider
    public static Object[][] positiveDataProvider() {
        return new Object[][]{
                {"#t", 2},
                {"#f", 2},
                {"#tasd", 2},
                {"#fad", 2},
        };
    }

    @DataProvider
    public static Object[][] negativeDataProvider() {
        return new Object[][]{
                {"oai#t", 0},
                {"ebgin", 0},
                {"vv#fal", 0},
                {"f##fmin", 0},
        };
    }

    @Test(dataProvider = "positiveDataProvider")
    public void testPositive(String input, int expectedLength) throws Exception {
        assertTrue(fsm.max(input).getKey() == expectedLength);
    }

    @Test(dataProvider = "negativeDataProvider")
    public void testNegative(String input, int expectedLength) throws Exception {
        assertTrue(fsm.max(input).getKey() == expectedLength);
    }
}
