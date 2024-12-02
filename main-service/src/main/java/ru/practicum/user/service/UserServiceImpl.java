package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.UserIntoDto;
import ru.practicum.user.dto.UserOutDto;
import ru.practicum.user.mapper.UserMapper;

import java.util.List;

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
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Нет пользователя по id %d", userId));
        }
        userRepository.deleteById(userId);
    }
}
