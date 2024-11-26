package org.shopme.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shopme.common.converter.IntegerListConverter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 70, unique = true, nullable = false)
    private String name;

    @Column(length = 150)
    private String logo;

    @Column(columnDefinition = "JSON")
    @Convert(converter = IntegerListConverter.class)
    private List<Integer> categories = new ArrayList<>();

    @Transient
    private List<Category> categoryList = new ArrayList<>();
}
