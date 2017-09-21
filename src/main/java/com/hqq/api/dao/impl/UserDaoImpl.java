package com.hqq.api.dao.impl;

import com.hqq.api.MySqlPoolUtils;
import com.hqq.api.dao.UserDao;
import com.hqq.api.model.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by qing on 2017/9/20.
 */
@Repository
public class UserDaoImpl implements UserDao {
    private static final Logger LOG = LogManager.getLogger(UserDaoImpl.class.getName());
    @Override
    public List<User> findAll() throws Exception {
        SqlSession session = MySqlPoolUtils.createSession();
        try{
            UserDao mapper = session.getMapper(UserDao.class);
            return mapper.findAll();
//            return session.selectList("com.hqq.api.dao.UserDao.findAll");
        }catch (Exception e){
            LOG.error("[UserDaoImpl] [findAll]", e);
        }finally {
            session.close();
        }
        return null;
    }
}
