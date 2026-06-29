package com.ems.service;

import com.ems.common.Result;
import com.ems.dto.LoginDTO;
import com.ems.dto.LoginResultDTO;

public interface AuthService {
    LoginResultDTO login(LoginDTO dto);
    Result<Void> resetAllPasswords();
}
