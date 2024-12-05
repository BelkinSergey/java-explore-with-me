package ru.practicum.user.service;

import ru.practicum.user.dto.UserIntoDto;
import ru.practicum.user.dto.UserOutDto;

import java.util.List;

public interface UserService {

    UserOutDto create(UserIntoDto userIntoDto);

    List<UserOutDto> get(List<Long> ids, Integer from, Integer size);

    void delete(Long userId);
}