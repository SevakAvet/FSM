package com.sevak_avet.lexer;

import com.sevak_avet.BaseFSMGroupTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by savetisyan on 11/10/16
 */
public class WhiteSpaceFSMGroupTest extends BaseFSMGroupTest {
    public WhiteSpaceFSMGroupTest() {
        super("/lexer/whitespace.json");
    }

    @DataProvider
    public static Object[][] positiveDataProvider() {
        return new Object[][]{
                {" ", 1},
                {"      ", 6},
                {"\n\n\n", 3},
                {"\n\r\t", 3},
                {"\n    \r\n\n\n   \t", 13},
        };
    }

    @DataProvider
    public static Object[][] negativeDataProvider() {
        return new Object[][]{
                {"123", 0},
                {"!! \n\n", 0},
                {"1     ", 0},
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
