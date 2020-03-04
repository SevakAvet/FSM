package com.sevak_avet.lexer;

import com.sevak_avet.BaseFSMGroupTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by savetisyan on 10/10/16
 */
@Test
public class RealFSMGroupTest extends BaseFSMGroupTest {

    public RealFSMGroupTest() {
        super("/lexer/real.json");
    }

    @DataProvider
    public static Object[][] positiveDataProvider() {
        return new Object[][]{
                {"+1."},
                {"-1"},
                {"0"},
                {"000"},
                {"+123"},
                {"1.1"},
                {"-2.3"},
                {"2e13"},
                {"10.1e+10"},
                {"10.1e-10"},
                {"+123.e2"},
                {".1"},
                {"091283."},
                {".1e01"},
                {"+1.2e-2"},
                {"+.1e10"},
                {"-23.e2"}
        };
    }

    @DataProvider
    public static Object[][] negativeDataProvider() {
        return new Object[][]{
                {"laksjd"},
                {".."},
                {"..0"},
                {"0.0.0"},
                {"+-123"},
                {":)"},
                {"+1.2-e123"},
                {"9e19."},
                {"+e8"},
                {"+."},
                {"-.e1"}
        };
    }

    @Test(dataProvider = "positiveDataProvider")
    public void testPositive(String input) throws Exception {
        assertTrue(fsmGroup.max(input).getKey() == input.length());
    }

    @Test(dataProvider = "negativeDataProvider")
    public void testNegative(String input) throws Exception {
        assertFalse(fsmGroup.max(input).getKey() == input.length());
    }
}
