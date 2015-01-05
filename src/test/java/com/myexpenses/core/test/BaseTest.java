package com.myexpenses.core.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.SkipException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leandro Loureiro on 04/09/14.
 */

public abstract class BaseTest {

    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);

    public final HttpResponse sendRequest(String method, Map<String, Object> input) {


        final MultivaluedMap inputArguments = new MultivaluedMapImpl();

        for (Map.Entry<String, Object> e : input.entrySet()) {
            inputArguments.add(e.getKey(), e.getValue());
        }

        final Client client = Client.create();
        final String hostname = System.getProperty("test.hostname");
        final String port = System.getProperty("test.port");

        try {
            final WebResource webResource = client.resource(String.format("http://%s:%s/%s", hostname, port, method));
            final ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, inputArguments);
            final Map<String, String> headers = new HashMap<String, String>();
            for (Map.Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
                headers.put(entry.getKey(), entry.getValue().get(0));
            }

            String data = response.getEntity(String.class);

            response.close();
            client.destroy();

            return new HttpResponse(data, response.getStatus(), headers);

        } catch (ClientHandlerException e) {
            LOGGER.warn("Connection error", e);
            throw new SkipException(e.getMessage());
        }

    }

}
