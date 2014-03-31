package com.barondust.geo.query;

import com.sun.jersey.api.client.ClientResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LocationServlet extends HttpServlet {


    public String getLocationsAsJson() {
        RestCalls restCalls = new RestCalls();
        ClientResponse response = restCalls.getRequest("/devices", "application/json");

        System.out.println("response: " + response.getStatus());

        String body = response.getEntity(String.class);
        System.out.println("body: " + body);

        return body;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.getWriter().write(getLocationsAsJson());
    }

}
