package com.sevak_avet.lexer;

import com.sevak_avet.BaseFSMGroupTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by savetisyan on 10/10/16
 */
@Test
public class OperationFSMGroupTest extends BaseFSMGroupTest {

    public OperationFSMGroupTest() {
        super("/lexer/operation.json");
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
        assertEquals(fsmGroup.max(operation).getKey().intValue(), expectedLength);
    }

    @Test(dataProvider = "negativeDataProvider")
    public void testNegative(String operation, int expectedLength) throws Exception {
        assertEquals(fsmGroup.max(operation).getKey().intValue(), expectedLength);
    }
}
