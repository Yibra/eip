package com.fsg.fsgdata.eiprestlet.entities;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;

public class DataResponse extends HashMap<String, List<JsonNode>> {
    private long size = 0;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
