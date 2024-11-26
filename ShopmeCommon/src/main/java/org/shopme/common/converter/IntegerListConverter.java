package org.shopme.common.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Converter
public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        try {
            return MAPPER.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}
