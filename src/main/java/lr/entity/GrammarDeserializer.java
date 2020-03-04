package lr.entity;

import com.google.gson.*;
import lexer.Lexer;
import lr.Grammar;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 23/10/16
 */
public class GrammarDeserializer implements JsonDeserializer<Grammar> {
    private final Lexer lexer;

    public GrammarDeserializer(Lexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public Grammar deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        Grammar grammar = new Grammar();

        JsonObject json = jsonElement.getAsJsonObject();
        grammar.setNonterms(context.deserialize(json.getAsJsonArray("nonterms"), List.class));
        grammar.setTerms(context.deserialize(json.getAsJsonArray("terms"), List.class));
        grammar.setStart(json.get("start").getAsString());

        Map<String, List<String>> rulesJson = context.deserialize(json.get("rules"), Map.class);
        List<Rule> rules = rulesJson.entrySet().stream()
                .flatMap(x -> x.getValue().stream()
                        .map(rule ->
                                new Rule(x.getKey(), lexer.tokenize(rule)
                                        .filter(z -> !z.getClassName().equals("Whitespace"))
                                        .collect(Collectors.toList()))))
                .collect(Collectors.toList());
        grammar.setRules(rules);

        return grammar;
    }
}
