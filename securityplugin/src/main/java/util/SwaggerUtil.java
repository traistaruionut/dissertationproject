package util;

import dto.EPInformation;
import org.codehaus.plexus.util.StringUtils;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.parser.SwaggerParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class SwaggerUtil {

    private SwaggerUtil(){

    }

    public static Swagger getSwaggerDefinition(String swaggerLocation) {
        return new SwaggerParser().read(swaggerLocation);
    }

    public static ArrayList<EPInformation> parseSwagger(Swagger swagger) {
        ArrayList<EPInformation> epInformationList = new ArrayList<EPInformation>();

        for (Map.Entry<String, Path> pair : swagger.getPaths().entrySet()) {
            EPInformation epInformation = new EPInformation();
            Path path = pair.getValue();
            epInformation.setPath(swagger.getHost() + pair.getKey());
            if (pair.getValue().getGet() != null) {
                epInformation.setHTTPVerb("GET");
            }
            for (v2.io.swagger.models.parameters.Parameter p : path.getGet().getParameters()) {
                if (StringUtils.isNotEmpty(p.getName())) {
                    epInformation.getParameters().add(p.getName());
                }
            }
            epInformationList.add(epInformation);

        }
        return epInformationList;
    }

    public static void filterPaths(Swagger swagger, String[] excludedPaths) {
        Iterator<Map.Entry<String, Path>> entryIterator = swagger.getPaths().entrySet().iterator();

        while (entryIterator.hasNext()) {
            String key = entryIterator.next().getKey();
            for (int i = 0; i < excludedPaths.length; i++) {
                if (key.contains(excludedPaths[i])) {
                    entryIterator.remove();
                }
            }
        }
    }

    public static ArrayList<EPInformation> removeEndpointsWithoutParameters(ArrayList<EPInformation> epInformationList){

        return epInformationList.stream()
                .filter(epInformation -> epInformation.getParameters().size() != 0)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public static ArrayList<String> getEpWithParameters(ArrayList<EPInformation> epInformationList) {
        ArrayList<String> paths = new ArrayList<>();
        for (EPInformation epInformation : epInformationList) {
            StringBuilder sb = new StringBuilder(epInformation.getPath());

            ArrayList<String> parametersList = epInformation.getParameters();
            if (parametersList.size() > 0) {
                sb.append("?");
            }
            for (int i = 0; i < parametersList.size(); i++) {
                sb.append(parametersList.get(i));
                sb.append("=%s");
                if (i < parametersList.size()-1) {
                    sb.append("&");
                }
            }
            paths.add(sb.toString());
        }
        return paths;
    }

}
