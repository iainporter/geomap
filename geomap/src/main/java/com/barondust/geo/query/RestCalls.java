package com.barondust.geo.query;


import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.springframework.beans.factory.annotation.Value;


public class RestCalls {

    @Value("${hostname}")
    private String hostname;


    protected Client createClient(){

        Client client = Client.create();
        client.addFilter(new LoggingFilter());

        return client;
    }


    public ClientResponse getRequest(String url, String acceptContentType){
        System.out.println("hostname: " + hostname);
        System.out.println("url: " + url);
        WebResource webResource = createClient()
                .resource("http://localhost:8080" + url);

        return webResource.accept(acceptContentType)
                .get(ClientResponse.class);
    }


}
