package spaska.gui;

import java.lang.reflect.Method;
import java.util.Map;

public class Utils {

    public static final String FILE_DATASET = "FILE_DATASET";
    public static final String EXIT = "EXIT";
    public static final String VALIDATOR_SET = "VALIDATOR_SET";
    public static final String PARAMETERS_READY = "PARAMETERS_READY";
    public static final String SHOW_PARAMETERS = "SHOW_PARAMETERS";
    public static final String REMOVE_ITEM = "REMOVE_ITEM";
    public static final String ITEM_SELECTED = "ITEM_SELECTED";
    public static final String COMMIT_PARAMETERS = "COMMIT_PARAMETERS";
    public static final String START = "START";
    public static final String STOP = "STOP";
    public static final String OPEN_STATISTICS = "OPEN_STATISTICS";

    @SuppressWarnings("unchecked")
    public static <T> Map<String, String> getParamsOfClass(
            Class<? extends T> cls) {
        try {
            Method m = cls.getMethod("getParameters");
            if (m != null) {
                return (Map<String, String>) m.invoke(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
