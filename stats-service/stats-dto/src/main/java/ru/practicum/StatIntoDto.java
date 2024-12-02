package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.group.Marker;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatIntoDto {

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 255)
   private String app;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 255)
   private String uri;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 255)
    private String ip;

    @NotNull(groups = {Marker.OnCreate.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
   private LocalDateTime timestamp;
}
