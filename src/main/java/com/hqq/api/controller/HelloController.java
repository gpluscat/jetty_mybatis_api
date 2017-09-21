package com.hqq.api.controller;

import com.hqq.api.dao.UserDao;
import com.hqq.api.dao.impl.UserDaoImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by qing on 2017/9/20.
 */
@RestController
public class HelloController {

    private static final Logger LOG = LogManager.getLogger(UserDaoImpl.class.getName());

    @Autowired
    private UserDao userDao;

    @RequestMapping("/say")
    public Object say(){

        LOG.info("[HelloController] [say]");
        try {
            return userDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "hello world";
    }
}
