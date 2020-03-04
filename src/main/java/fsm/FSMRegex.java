package fsm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by savetisyan on 15/11/16
 */
public class FSMRegex {
    private int curState = 0;

    private String genSym() {
        return String.format("%s%d", "S", curState++);
    }

    public FSM parse(String str, Map<String, String> inputs) {
        return parse(str, 0, str.length(), brackets(str), inputs);
    }

    private FSM parse(String str,
                      int startIndex,
                      int endIndex,
                      Map<Integer, Integer> brackets,
                      Map<String, String> inputs) {
        FSM fsm = new FSM();
        String start = genSym();
        fsm.getStarts().add(start);
        fsm.getFinishes().add(start);
        boolean isEmpty = true;

        for (int i = startIndex; i < endIndex; i++) {
            String curChar = String.valueOf(str.charAt(i));

            switch (str.charAt(i)) {
                case '(':
                    int end = brackets.get(i);
                    FSM curFsm = parse(str, i + 1, end, brackets, inputs);
                    if (end + 1 < endIndex && str.charAt(end + 1) == '*') {
                        curFsm.iterate();
                    }

                    if (isEmpty) {
                        fsm = curFsm;
                        isEmpty = false;
                    } else {
                        boolean emptyTransition = curFsm.getFunctionTransitions().entrySet().stream()
                                .filter(x -> curFsm.getStarts().contains(x.getKey().getKey()))
                                .anyMatch(x -> x.getKey().getValue().equals("\\$"));
                        fsm.concat(curFsm, emptyTransition);
                    }

                    i = end;
                    break;
                case '\\':
                    if (i + 1 < endIndex) {
                        String function = "\\" + str.charAt(i + 1);
                        if (!inputs.containsKey(function) && !FSM.DEFAULT_INPUTS.containsKey(function)) {
                            throw new IllegalArgumentException("Unknown function: " + function);
                        } else {
                            curFsm = oneSymbolAutomat(function, true);
                            if (i + 2 < endIndex && str.charAt(i + 2) == '*') {
                                curFsm.iterate();
                                i++;
                            }
                            if (isEmpty) {
                                fsm = curFsm;
                                isEmpty = false;
                            } else {
                                fsm.concat(curFsm, function.equals("\\$"));
                            }
                        }

                        i += function.length() - 1;
                    } else {
                        throw new IllegalArgumentException("Unknown function at pos " + i);
                    }
                    break;
                case '+':
                    FSM joinFsm = parse(str, i + 1, endIndex, brackets, inputs);
                    fsm.join(joinFsm);
                    i = endIndex;
                    break;
                case '*':
                    fsm.iterate();
                    break;
                default:
                    curFsm = oneSymbolAutomat(curChar, false);
                    if (i + 1 < endIndex && str.charAt(i + 1) == '*') {
                        curFsm.iterate();
                        i++;
                    }
                    if (isEmpty) {
                        fsm = curFsm;
                        isEmpty = false;
                    } else {
                        fsm.concat(curFsm, false);
                    }
                    break;
            }
        }

        return fsm;
    }


    private FSM oneSymbolAutomat(String symbol, boolean isFunction) {
        FSM fsm = new FSM();
        String from = genSym();
        String to = genSym();

        fsm.getStarts().add(from);
        fsm.getFinishes().add(to);

        if (isFunction) {
            fsm.addFunctionTransition(from, to, symbol);
        } else {
            fsm.addCharTransition(from, to, symbol);
        }

        return fsm;
    }

    public static Map<Integer, Integer> brackets(String str) {
        Map<Integer, Integer> brackets = new HashMap<>();

        int level = 0;
        Map<Integer, Integer> startPos = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                startPos.put(level, i);
                level++;
            }

            if (str.charAt(i) == ')') {
                level--;
                brackets.put(startPos.get(level), i);
            }
        }

        return brackets;
    }
    public void resetState() {
        this.curState = 0;
    }

    public static void main(String[] args) {
        String regex = "(\\++-+\\$)(\\d\\d*(\\$+.\\d\\d*))";
        Map<String, String> inputs = new HashMap<>();

        FSM parse = new FSMRegex().parse(regex, inputs);
        System.out.println(parse.getStarts());
        System.out.println(parse.getFinishes());
        System.out.println(parse.getCharTransitions());
        System.out.println(parse.getFunctionTransitions());

        FSMGroup group = new FSMGroup(parse);
        System.out.println(group.max("123")); // false, 0
        System.out.println(group.max("123.")); // false, 0
        System.out.println(group.max("12.3.")); // true, 4
        System.out.println(group.max("123.1")); // true, 5
        System.out.println(group.max("123.123")); // true, 7
        System.out.println(group.max("+123.123")); // true, 8
        System.out.println(group.max("-123.123")); // true, 8
        System.out.println(group.max("a123.123")); // false, 0
        System.out.println(group.max("a123.123", 1)); // true, 7
        System.out.println(group.max("a123.123", 4)); // false, 0
        System.out.println(group.max("a123.123", 5)); // false, 0
    }

}
