import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by hongzhan on 2018/6/30.
 */
public class FileUtil {
    static public String readResource(final String fileName, Charset charset) throws IOException {
        return Resources.toString(Resources.getResource(fileName), charset);
    }
}
