package com.sepay.backend.user.service;

import com.sepay.backend.payment.dto.PasswordDTO;
import com.sepay.backend.user.dto.UserDTO;
import com.sepay.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserMapper mapper;

    @Override
    public UserDTO getInfo(Integer userNo) {
        return mapper.selectUser(userNo);
    }

    @Override
    public boolean checkPassword(PasswordDTO passwordDTO) {
        int userPassword = mapper.getPassword(1);
        System.out.println(passwordDTO.getPassword());
        System.out.println("??"+userPassword);
        return false;
    }
}
