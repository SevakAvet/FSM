package com.sevak_avet.lr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lexer.Config;
import lexer.Lexer;
import lexer.entity.Token;
import lr.Grammar;
import lr.LR1;
import lr.Utils;
import lr.entity.*;
import org.apache.tools.ant.util.FileUtils;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 23/10/16
 */

public class GrammarTest {

    @Test
    public void testName() throws Exception {
        String file = Lexer.class.getResource("/lexer/lr/example2/lr.json").getFile();
        Config config = Config.parse(file);
        Lexer lexer = new Lexer(config);

        GrammarDeserializer deserializer = new GrammarDeserializer(lexer);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Grammar.class, deserializer)
                .create();

        file = Lexer.class.getResource("/lr/example2/grammar.json").getFile();
        String json = FileUtils.readFully(new FileReader(file));
        Grammar grammar = gson.fromJson(json, Grammar.class);

        LR1 lr = new LR1(grammar);

        Utils.printCombinedItems(lr.items());

        CanonicalTable canonicalTable = lr.getTable();
        System.out.println(canonicalTable.prettyPrint());
        List<Token> input = lexer.tokenize("i*(i+i)").collect(Collectors.toList());

        List<Rule> rules = lr.rulesSequence(input);
        System.out.println(rules);

        SyntaxTree tree = lr.buildTree(input);
        System.out.println(tree.prettyPrint());
    }
}
