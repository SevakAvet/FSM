package com.sevak_avet.lexer;

import com.sevak_avet.BaseFSMTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by savetisyan on 11/10/16
 */
public class IdFSMTest extends BaseFSMTest {
    public IdFSMTest() {
        super("/lexer/id.json");
    }

    // w(w+d+_+')* + rr* (d-цифра, w-буква, r = (< + > + ! + # + '+' + '-' + '*' + '/' + & + $ + @ + ~))
    @DataProvider
    public static Object[][] positiveDataProvider() {
        return new Object[][]{
                {"someid", 6},
                {"a_d_d___", 8},
                {"a'", 2},
                {"@", 1},
                {"@@@aaa", 3},
                {"~>", 2}
        };
    }

    @DataProvider
    public static Object[][] negativeDataProvider() {
        return new Object[][]{
                {"1asd", 0},
                {"_asd", 0},
                {"'asd", 0},
                {"", 0},
        };
    }

    @Test(dataProvider = "positiveDataProvider")
    public void testPositive(String input, int expectedLength) throws Exception {
        assertEquals(fsm.max(input).getKey().intValue(), expectedLength);
    }

    @Test(dataProvider = "negativeDataProvider")
    public void testNegative(String input, int expectedLength) throws Exception {
        assertEquals(fsm.max(input).getKey().intValue(), expectedLength);
    }
}
