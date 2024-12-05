package ru.practicum.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Marker;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
public class UserIntoDto {

    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Size(min = 2, max = 250, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;

    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "формат почты должен быть корректным")
    @NotBlank(groups = Marker.OnCreate.class)
    @Column(unique = true)
    @Size(min = 6, max = 254, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String email;


}