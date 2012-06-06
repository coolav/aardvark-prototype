
package uib.gui.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Olav
 */
public class UnixGlobFileFilter implements FileFilter {
    private Pattern pattern;

    public UnixGlobFileFilter(String filter) {
        pattern = Pattern.compile(globToRegex(filter));
    }

    @Override
    public boolean accept(File file) {
        String path = file.getName();
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

    private String globToRegex(String glob) {
        char c = '\0';
        boolean escape = false;
        boolean enclosed = false;
        StringBuilder buffer = new StringBuilder(glob.length());

        for (int i = 0; i < glob.length(); i++) {
            c = glob.charAt(i);

            if (escape) {
                buffer.append('\\');
                buffer.append(c);
                escape = false;
                continue;
            }

            switch (c) {
                case '*':
                    buffer.append('.').append('*');
                    break;
                case '?':
                    buffer.append('.');
                    break;
                case '\\':
                    escape = true;
                    break;
                case '.':
                    buffer.append('\\').append('.');
                    break;
                case '{':
                    buffer.append('(');
                    enclosed = true;
                    break;
                case '}':
                    buffer.append(')');
                    enclosed = false;
                    break;
                case ',':
                    if (enclosed)
                        buffer.append('|');
                    else
                        buffer.append(',');
                    break;
                default:
                    buffer.append(c);
            }
        }
        return buffer.toString();
    }
}
