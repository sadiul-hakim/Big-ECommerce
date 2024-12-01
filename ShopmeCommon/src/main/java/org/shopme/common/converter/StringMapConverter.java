package org.shopme.common.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Converter
@Component
public class StringMapConverter implements AttributeConverter<Map<String, String>, String> {
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        try {
            return MAPPER.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }
}
