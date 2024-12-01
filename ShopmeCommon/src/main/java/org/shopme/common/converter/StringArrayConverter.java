package org.shopme.common.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class StringArrayConverter implements AttributeConverter<String[], String> {
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        try {
            return MAPPER.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return new String[4];
        }
    }
}
