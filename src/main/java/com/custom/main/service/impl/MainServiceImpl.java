package com.custom.main.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.custom.main.service.MainService;

@Service(value = "mainService")
public class MainServiceImpl implements MainService {

    final Logger LOGGER = LoggerFactory.getLogger(MainServiceImpl.class);
    
}
