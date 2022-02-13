package com.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OperationStatus {

    public static final OperationStatus OK = new OperationStatus();

    private String error;
    private Object data = "true";

    public OperationStatus() {
    }

    public OperationStatus(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toResponse() throws JsonProcessingException {
        String response = new ObjectMapper().writeValueAsString(this);
        System.out.println(response);
        return response;
    }

    @Override
    public String toString() {
        try {
            String str = new ObjectMapper().writeValueAsString(this);
            System.out.println(str);
            return str;
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
