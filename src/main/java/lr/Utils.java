package lr;

import java.util.List;
import java.util.function.Function;

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
}
