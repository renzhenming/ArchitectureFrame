package com.rzm.commonlibrary.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by renzhenming on 2018/5/4.
 *
 * 集合转换为json字符串
 */

public class JsonUtils {

    public static String format(String sepStr, HashMap<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        sb.append("{\n");
        String mySepStr = sepStr + "\t";
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append(mySepStr).append('\"').append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof HashMap<?, ?>) {
                sb.append(format(mySepStr, (HashMap<String, Object>)value));
            } else if (value instanceof ArrayList<?>) {
                sb.append(format(mySepStr, (ArrayList<Object>)value));
            } else if (value instanceof String) {
                sb.append('\"').append(value).append('\"');
            } else {
                sb.append(value);
            }
            i++;
        }
        sb.append('\n').append(sepStr).append('}');
        return sb.toString();
    }

    private static String format(String sepStr, ArrayList<Object> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("[\n");
        String mySepStr = sepStr + "\t";
        int i = 0;
        for (Object value : list) {
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append(mySepStr);
            if (value instanceof HashMap<?, ?>) {
                sb.append(format(mySepStr, (HashMap<String, Object>)value));
            } else if (value instanceof ArrayList<?>) {
                sb.append(format(mySepStr, (ArrayList<Object>)value));
            } else if (value instanceof String) {
                sb.append('\"').append(value).append('\"');
            } else {
                sb.append(value);
            }
            i++;
        }
        sb.append('\n').append(sepStr).append(']');
        return sb.toString();
    }
}
