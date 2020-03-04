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
import org.testng.reporters.Files;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 23/10/16
 */

public class RegexGrammarTest {

    @Test
    public void testName() throws Exception {
        String file = Lexer.class.getResource("/lexer/lr/example5/regex_grammar.json").getFile();
        Config config = Config.parse(file);
        Lexer syntaxRulesLexer = new Lexer(config);
        GrammarDeserializer deserializer = new GrammarDeserializer(syntaxRulesLexer);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Grammar.class, deserializer)
                .create();

        file = Lexer.class.getResource("/lr/example3/grammar.json").getFile();
        String json = FileUtils.readFully(new FileReader(file));
        Grammar grammar = gson.fromJson(json, Grammar.class);

        file = Lexer.class.getResource("/lexer/lr/example5/regex_input.json").getFile();
        config = Config.parse(file);
        Lexer lexer = new Lexer(config);
        System.out.println(grammar);

        LR1 lr = new LR1(grammar);

        System.out.println(grammar.getRules());

        Set<Component> items = lr.items();
//        Utils.printCombinedItems(items);

        CanonicalTable canonicalTable = lr.getTable();
//        System.out.println(canonicalTable.prettyPrint());

        File inputFile = new File(Lexer.class.getResource("/lr/example3/input.txt").getFile());
        List<Token> input = lexer.tokenize(Files.readFile(inputFile))
                .filter(x -> !x.getClassName().equals("Whitespace"))
                .collect(Collectors.toList());

        System.out.println(input);

        SyntaxTree tree = lr.buildTree(input);
//        List<Rule> rules = tree.getAppliedRules();

//        System.out.println(rules);
        System.out.println(tree.prettyPrint());
    }
}
