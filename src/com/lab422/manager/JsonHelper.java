package com.lab422.manager;

public class JsonHelper {

    public static String json(String str) {
        if (str == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\r':
                    sb.append("\\r");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                default:
                    sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

}
