import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by savetisyan on 10/10/16
 */
@Test
public class OperationFSMTest extends BaseFSMTest {

    public OperationFSMTest() {
        super("lex/operation.json");
    }

    @DataProvider
    public static Object[][] positiveDataProvider() {
        return new Object[][]{
                {">", 1},
                {"<>", 2},
                {"*90as8d", 1},
                {"///", 1},
        };
    }

    @DataProvider
    public static Object[][] negativeDataProvider() {
        return new Object[][]{
                {"123", 0},
                {"!!", 0},
                {"@<>", 0},
        };
    }

    @Test(dataProvider = "positiveDataProvider")
    public void testPositive(String operation, int expectedLength) throws Exception {
        assertEquals(fsm.max(operation).getKey().intValue(), expectedLength);
    }

    @Test(dataProvider = "negativeDataProvider")
    public void testNegative(String operation, int expectedLength) throws Exception {
        assertEquals(fsm.max(operation).getKey().intValue(), expectedLength);
    }
}
