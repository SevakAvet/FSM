package com.sevak_avet.lexer;

import com.sevak_avet.BaseFSMGroupTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by savetisyan on 11/10/16
 */
public class KeywordFSMGroupTest extends BaseFSMGroupTest {

    public KeywordFSMGroupTest() {
        super("/lexer/keyword.json");
    }

    @DataProvider
    public static Object[][] positiveDataProvider() {
        return new Object[][]{
                {"begin", 5},
                {"enddd", 3},
                {"val", 3},
                {"if", 2},
                {"inn", 2},
        };
    }

    @DataProvider
    public static Object[][] negativeDataProvider() {
        return new Object[][]{
                {"not a key word", 0},
                {"ebgin", 0},
                {"vval", 0},
                {"min", 0},
        };
    }

    @Test(dataProvider = "positiveDataProvider")
    public void testPositive(String input, int expectedLength) throws Exception {
        assertEquals(fsmGroup.max(input).getKey().intValue(), expectedLength);
    }

    @Test(dataProvider = "negativeDataProvider")
    public void testNegative(String input, int expectedLength) throws Exception {
        assertEquals(fsmGroup.max(input).getKey().intValue(), expectedLength);
    }
}
