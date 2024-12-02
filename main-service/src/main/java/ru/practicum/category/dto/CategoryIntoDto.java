package ru.practicum.category.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.model.Marker;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CategoryIntoDto {

    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Length(max = 50,
            min = 1,
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Column(unique = true)
    private String name;

}
