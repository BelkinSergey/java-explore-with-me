package ru.practicum.user.mapper;

import ru.practicum.user.dto.UserIntoDto;
import ru.practicum.user.dto.UserOutDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

public class UserMapper {

    public static User toUser(UserIntoDto dto) {
        return new User(
                null,
                dto.getName(),
                dto.getEmail()
        );
    }

    public static UserOutDto toUserOutDto(User user) {
        return new UserOutDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}