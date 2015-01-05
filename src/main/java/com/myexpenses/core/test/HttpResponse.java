package com.myexpenses.core.test;

import java.util.Map;

/**
 * Created by Leandro Loureiro on 04/09/14.
 */
public final class HttpResponse {

    private String body;
    private int code;
    private Map headers;

    public HttpResponse(String body, int code) {
        this.body = body;
        this.code = code;
    }

    public HttpResponse(String body, int code, Map headers) {
        this.body = body;
        this.code = code;
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map getHeaders() {
        return headers;
    }

    public void setHeaders(Map headers) {
        this.headers = headers;
    }
}
