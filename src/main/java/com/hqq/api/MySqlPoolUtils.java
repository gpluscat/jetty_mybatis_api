package com.hqq.api;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public class MySqlPoolUtils {

    private static final Logger LOG = LogManager.getLogger(MySqlPoolUtils.class.getName());

    private static SqlSessionFactory SESSION_FACTORY = null;

    public static SqlSessionFactory initSessionPool() {
        String resource = "conf/mybatis-config.xml";
        try {
            InputStream inputStream = Resources.getResourceAsStream( resource );
            SESSION_FACTORY = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("[MySqlPoolUtils] [initSessionPool] 初始化数据库连接池异常", e);
        }
        return SESSION_FACTORY;
    }

    public static SqlSession createSession() {
        SqlSession session = SESSION_FACTORY.openSession();
        return session;
    }

}
