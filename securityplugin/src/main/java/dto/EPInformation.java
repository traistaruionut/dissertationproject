package dto;

import java.util.ArrayList;

public class EPInformation {
    private String path;
    private String HTTPVerb;
    private ArrayList<String> parameters;

    public EPInformation(){
        this.parameters = new ArrayList<String>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHTTPVerb() {
        return HTTPVerb;
    }

    public void setHTTPVerb(String HTTPVerb) {
        this.HTTPVerb = HTTPVerb;
    }

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "EPInformation{" +
                "path='" + path + '\'' +
                ", HTTPVerb='" + HTTPVerb + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
