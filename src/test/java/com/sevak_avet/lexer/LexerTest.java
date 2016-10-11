package com.sevak_avet.lexer;

import lexer.Lexer;
import lexer.LexerConfig;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by savetisyan on 11/10/16
 */
public class LexerTest {

    @Test
    public void test() throws Exception {
        String file = Lexer.class.getResource("/lexer/lexer.json").getFile();

        LexerConfig config = LexerConfig.parse(file);
        File input = new File(LexerTest.class.getResource("/lexer_test.txt").getFile());
        new Lexer(config)
                .tokenize(input)
                .map(Lexer::makeWhiteSpaceVisible)
                .forEach(System.out::println);
    }
}
