package com.hh.test.logs;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Record error cat
 * <p/>
 * Created in 2018.09.06
 * <p/>
 *
 * @author Liaozihong
 */
public final class RecordErrorCat {
    /**
     * Get trace string.
     *
     * @param e the e
     * @return the string
     */
    public static String getTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        StringBuffer sb = sw.getBuffer();
        return sb.toString();
    }
}
