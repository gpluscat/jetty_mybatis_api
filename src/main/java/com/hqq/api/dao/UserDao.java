package com.hqq.api.dao;

import com.hqq.api.model.User;

import java.util.List;

/**
 * Created by qing on 2017/9/20.
 */
public interface UserDao {
    List<User> findAll() throws Exception;
}
