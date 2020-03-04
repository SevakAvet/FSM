package com.sevak_avet.lexer;

import lexer.Lexer;
import lexer.Config;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by savetisyan on 11/10/16
 */
public class LexerTest {

    @Test
    public void test() {
        String file = Lexer.class.getResource("/lexer/lexer.json").getFile();

        Config config = Config.parse(file);
        File input = new File(LexerTest.class.getResource("/lexer_test.txt").getFile());
        new Lexer(config)
                .tokenize(input)
                .map(Lexer::makeWhiteSpaceVisible)
                .filter(x -> !x.getClassName().equals("Whitespace"))
                .forEach(System.out::println);
    }
}
