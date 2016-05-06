package com.myexpenses.core.test;

import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

import java.io.IOException;

/**
 * @author Leandro Loureiro
 */
public class GlobalSettings {

    public static final String TEST_USER = System.getProperty("test.user");
    public static final String TEST_PASSWORD = System.getProperty("test.password");
    public static final String SERVER = System.getProperty("test.server");

    static {
        Unirest.setObjectMapper(new ObjectMapper() {

            private final com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(final String s, final Class<T> aClass) {

                try {
                    return jacksonObjectMapper.readValue(s, aClass);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(final Object o) {
                try {
                    return jacksonObjectMapper.writeValueAsString(o);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
