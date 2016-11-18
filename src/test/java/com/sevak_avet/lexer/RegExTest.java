package com.sevak_avet.lexer;

import lexer.Config;
import lexer.Lexer;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by savetisyan on 18/11/16
 */
public class RegExTest {

    @Test
    public void test() throws Exception {
        String file = Lexer.class.getResource("/regex_define/regex_define.json").getFile();
        Config config = Config.parse(file);

        File input = new File(LexerTest.class.getResource("/lexer_test.txt").getFile());
        new Lexer(config)
                .tokenize(input)
                .map(Lexer::makeWhiteSpaceVisible)
                .filter(x -> !x.getClassName().equals("Whitespace"))
                .forEach(System.out::println);
    }
}
