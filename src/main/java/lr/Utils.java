package lr;

import lr.entity.Component;
import lr.entity.Item;
import lr.entity.Rule;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 26/10/16
 */
public abstract class Utils {
    public static <T> boolean trueForAllElements(List<T> list, Function<T, Boolean> f) {
        for (T item : list) {
            if (!f.apply(item)) {
                return false;
            }
        }
        return true;
    }

    public static <T> int findFirstIndex(List<T> list, Function<T, Boolean> f) {
        for (int i = 0; i < list.size(); i++) {
            if (f.apply(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static void printCombinedItems(Set<Component> items) {
        ArrayList<Component> components = new ArrayList<>(items);
        components.sort(Comparator.comparing(Component::getId));
        components.forEach(x -> {
            Map<String, List<Item>> groups = x.getItems()
                    .stream()
                    .collect(Collectors.groupingBy(t -> t.ruleWithDot().toString()));

            System.out.println("I" + x.getId() + "= {");
            groups.forEach((k, v) -> {
                System.out.print("\t");
                System.out.println(k + ", " + v.stream()
                        .map(Item::getLookahead)
                        .map(z -> z.replace("\n", "$"))
                        .collect(Collectors.joining("/"))
                        + "]");
            });
            System.out.println("}");
        });
    }
}
