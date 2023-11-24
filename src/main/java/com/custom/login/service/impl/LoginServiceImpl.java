package com.custom.login.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.custom.login.service.LoginService;

@Service(value = "loginService")
public class LoginServiceImpl implements LoginService {

    final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);
    
}
