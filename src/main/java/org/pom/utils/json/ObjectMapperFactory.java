package org.pom.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.pom.TransportSystem;

public class ObjectMapperFactory {

    public static ObjectMapper createTransportSystemMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TransportSystem.class, new TransportSystemDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
