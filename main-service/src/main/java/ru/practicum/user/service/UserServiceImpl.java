package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.UserIntoDto;
import ru.practicum.user.dto.UserOutDto;
import ru.practicum.user.mapper.UserMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserOutDto create(UserIntoDto userIntoDto) {
        return UserMapper.toUserOutDto(userRepository.save(UserMapper.toUser(userIntoDto)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserOutDto> get(List<Long> ids, Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        List<UserOutDto> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).map(UserMapper::toUserOutDto).toList();
        } else {
            users = userRepository.findAllByIdIn(ids, pageable).stream().map(UserMapper::toUserOutDto).toList();
        }
        return users;
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} успешно удален", userId);
    }
}
