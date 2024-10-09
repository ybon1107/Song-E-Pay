package com.sepay.backend.user.mapper;


import com.sepay.backend.user.dto.UserDTO;

import java.util.Map;

public interface UserMapper {
    UserDTO selectUserByUserNo(Integer userNo);

    String getSecondaryPassword(int userNo);

    UserDTO getUserInfo(String userId);

    UserDTO selectUser(Map map);

    int insertUser(UserDTO userDTO);

    UserDTO selectUserByEmail(String email);
}
