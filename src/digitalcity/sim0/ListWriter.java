package digitalcity.sim0;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class ListWriter {

    private static final char DEFAULT_SEPARATOR = ',';

    public static void writeLine(Writer w, List<?> values) throws IOException{
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<?> values, char separators) throws IOException{
        writeLine(w, values, separators, ' ');
    }

    public static void writeLine(Writer w, List<?> values, char separators, char customQuote) throws IOException{

        boolean first = true;

        if (separators == ' '){
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' '){
                sb.append(value);
            }else {
                if (value instanceof String) {
                    sb.append(customQuote).append(value).append(customQuote);
                } else {
                    sb.append(customQuote).append(String.valueOf(value)).append(customQuote);
                }
            }
            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
}
