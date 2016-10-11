package lexer;

import javafx.util.Pair;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by savetisyan on 11/10/16
 */
public class Lexer {
    private LexerConfig config;

    public Lexer(LexerConfig config) {
        this.config = config;
    }

    public Stream<LexerToken> tokenize(File input) {
        try {
            return tokenize(FileUtils.readFully(new FileReader(input)));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Stream<LexerToken> tokenize(String input) {
        Stream.Builder<LexerToken> builder = Stream.builder();
        final int[] skip = new int[]{0};

        while (true) {
            List<Pair<LexerEntry, Pair<Integer, Boolean>>> results = config.getEntries().stream()
                    .map(x -> new Pair<>(x, x.getMachine().max(input, skip[0])))
                    .collect(Collectors.toList());

            Pair<Integer, Boolean> max = results.stream()
                    .map(Pair::getValue)
                    .max(Comparator.comparing(Pair::getKey))
                    .orElse(new Pair<>(0, false));

            if (max.getKey() == 0) {
                break;
            }

            LexerToken token = results.stream()
                    .filter(x -> x.getValue().equals(max))
                    .max((x, y) -> Integer.compare(x.getKey().getPriority(), y.getKey().getPriority()))
                    .map(x -> new LexerToken(
                            x.getKey().getClassName(),
                            input.substring(skip[0], skip[0] + x.getValue().getKey())
                    )).orElse(new LexerToken("", ""));

            if (token.getClassName().isEmpty()) {
                break;
            }

            builder.add(token);
            skip[0] += token.getValue().length();
        }

        return builder.build();
    }

    public static LexerToken makeWhiteSpaceVisible(LexerToken token) {
        if (token.getClassName().equals("Whitespace")) {
            token.setValue(token.getValue()
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
                    .replace(' ', '_')
            );
        }
        return token;
    }

    public static void main(String[] args) {

    }


}
