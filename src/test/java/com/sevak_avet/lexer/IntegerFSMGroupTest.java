package com.sevak_avet.lexer;

import com.sevak_avet.BaseFSMGroupTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by savetisyan on 11/10/16
 */
public class IntegerFSMGroupTest extends BaseFSMGroupTest {
    public IntegerFSMGroupTest() {
        super("/lexer/integer.json");
    }

    @DataProvider
    public static Object[][] positiveDataProvider() {
        return new Object[][]{
                {"123", 3},
                {"0000", 4},
                {"+123123", 7},
                {"-100092", 7},
                {"+1askd", 2},
                {"-111c", 4},
        };
    }

    @DataProvider
    public static Object[][] negativeDataProvider() {
        return new Object[][]{
                {"oai11#t", 0},
                {"eb2323gin", 0},
                {"++23vv#fal", 0},
                {"--000f##fmin", 0},
        };
    }

    @Test(dataProvider = "positiveDataProvider")
    public void testPositive(String input, int expectedLength) throws Exception {
        Assert.assertEquals(fsmGroup.max(input).getKey().intValue(), expectedLength);
    }

    @Test(dataProvider = "negativeDataProvider")
    public void testNegative(String input, int expectedLength) throws Exception {
        Assert.assertEquals(fsmGroup.max(input).getKey().intValue(), expectedLength);
    }
}
