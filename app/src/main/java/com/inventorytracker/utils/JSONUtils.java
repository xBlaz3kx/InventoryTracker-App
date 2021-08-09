package com.inventorytracker.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class JSONUtils {

    public static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonFactory factory = new JsonFactory();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        factory.setCodec(mapper);
    }

    public static JsonParser createJsonParser(InputStream byteInputStream) throws IOException {
        return factory.createParser(byteInputStream);
    }

    public static <T> T readJson(byte[] json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T readJson(InputStream stream, Class<T> clazz) throws IOException {
        return mapper.readValue(stream, clazz);
    }

    public static <T> T readJson(InputStream stream, TypeReference<T> type) throws IOException {
        return mapper.readValue(stream, type);
    }

    public static <T> List<T> readJsonArray(String stream, Class<T> clazz) throws IOException {
        return mapper.readValue(stream, new TypeReference<List<T>>() {
        });
    }

    public static JsonGenerator createGenerator(OutputStream stream) throws IOException {
        return factory.createGenerator(stream);
    }

    public static String writeJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static void writeJson(OutputStream os, Object object) throws IOException {
        mapper.writeValue(os, object);
    }

    public static byte[] writeJsonBytes(Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {

            return null;
        }
    }

    public static String writePrettyJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static void writePrettyJson(File file, Object object) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
        } catch (IOException ignored) {
        }
    }
}
