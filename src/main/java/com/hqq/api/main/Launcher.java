package com.hqq.api.main;

import com.hqq.api.server.JettyServer;
import com.hqq.api.MySqlPoolUtils;

/**
 * Created by qing on 2017/9/20.
 */
public class Launcher {
    public static void main(String[] args) throws Exception {
        JettyServer.getSingleton().startJetty(8080);
        MySqlPoolUtils.initSessionPool();

        Thread.currentThread().join();
    }
}
