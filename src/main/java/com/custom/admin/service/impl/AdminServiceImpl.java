package com.custom.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.custom.admin.service.AdminService;

@Service(value = "adminService")
public class AdminServiceImpl implements AdminService {

    final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);
    
}
