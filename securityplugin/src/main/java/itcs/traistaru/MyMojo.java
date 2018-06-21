package itcs.traistaru;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dto.EPInformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;
import util.PayloadUtil;
import util.SwaggerUtil;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.parser.SwaggerParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
@Mojo(name = "sqli")
public class MyMojo extends AbstractMojo {

    @Parameter(property = "jsonPath")
    private String jsonPath;

    @Parameter(property = "jsonURL")
    private String jsonURL;

    @Parameter(property = "excludedPaths")
    private String[] excludedPaths;

    @Parameter(defaultValue = "false", property = "skip")
    private boolean skip;

    @Parameter(defaultValue = "true", property = "skipEndpointsWithoutParameters")
    private boolean skipEndpointsWithoutParameters;

    Swagger swagger;

    public void execute() throws MojoExecutionException {

        if (skip) {
            getLog().info("Skipping");
            return;
        }
        this.validateInput();

        SwaggerUtil.filterPaths(this.swagger,excludedPaths);

        ArrayList<EPInformation> epInformationList = SwaggerUtil.parseSwagger(this.swagger);

        //endpoints found
        epInformationList.stream().forEach(epInformation -> getLog().info(epInformation.toString()));
//        for (EPInformation epInformation : epInformationList) {
//            getLog().info(epInformation.toString());
//        }

        if(skipEndpointsWithoutParameters){
            epInformationList = SwaggerUtil.removeEndpointsWithoutParameters(epInformationList);
        }

        //endpoints tested
        getLog().info("Endpoints tested:");
        for(EPInformation epInformation : epInformationList){
            getLog().info(epInformation.getPath());
        }
        getLog().info("================================");
        PayloadUtil payloadUtil = new PayloadUtil();

        // log ep with parameters
        for (String path : SwaggerUtil.getEpWithParameters(epInformationList)) {
            getLog().info(path);
        }

        ArrayList<String> constructedEndpoints = null;
        int delay = 3;
        try {
            constructedEndpoints = payloadUtil.injectPayloads(SwaggerUtil.getEpWithParameters(epInformationList), payloadUtil.getPayloads(),delay);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ArrayList<String> vulnerableEndpoints = this.callEndpoints(constructedEndpoints, delay);
        this.report(vulnerableEndpoints);
    }

    private ArrayList<String> callEndpoints(ArrayList<String> endpointsList, int delay) {
        ArrayList<String> vulnerableEndpoints = new ArrayList<>();
        for (String ep : endpointsList) {
            getLog().info("calling " + ep);
            Timestamp startingTimestamp = new Timestamp(System.currentTimeMillis());
            try {
                HttpResponse<JsonNode> httpResponse = Unirest.get("http://" + ep).asJson();
                if(httpResponse.getStatus() != 404 && httpResponse.getStatus() != 500){
                    Timestamp endingTimestamp = new Timestamp(System.currentTimeMillis());
                    Long durationInSeconds = (endingTimestamp.getTime() - startingTimestamp.getTime())/1000;
                    if (durationInSeconds >= delay) {
                        getLog().info(ep + " might be vulnerable");
                        vulnerableEndpoints.add(ep);
                    }
                }else{
                    continue;
                }
            } catch (UnirestException e) {
//                e.printStackTrace();
            }
        }
        return vulnerableEndpoints;
    }

    private void report(ArrayList<String> vulnerableEndpoints) throws MojoExecutionException {
        if (vulnerableEndpoints.size() > 0) {
            getLog().info("Endpoints found vulnerable: ");
            vulnerableEndpoints.stream().forEach(endpoint -> getLog().info(endpoint));
            throw new MojoExecutionException("Endpoints found vulnerable");
        }

    }

    private void validateInput() throws MojoExecutionException {
        if (StringUtils.isEmpty(jsonPath) && StringUtils.isEmpty(jsonURL)) {
            throw new MojoExecutionException("jsonPath or jsonURL must be set.");
        }

        if (!StringUtils.isEmpty(jsonPath)) {
            this.swagger = SwaggerUtil.getSwaggerDefinition(jsonPath);
        }

        if (!StringUtils.isEmpty(jsonURL)) {
            this.swagger = SwaggerUtil.getSwaggerDefinition(jsonURL);
        }
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getJsonURL() {
        return jsonURL;
    }

    public void setJsonURL(String jsonURL) {
        this.jsonURL = jsonURL;
    }


}
