package com.hqq.api.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.IOException;

/**
 * Created by qing on 2017/9/20.
 */
public class JettyWebApplicationInitializer implements WebApplicationInitializer{

    private static final Logger LOG = LogManager.getLogger(JettyWebApplicationInitializer.class);

    private static ServletContext servletContext;
    private static String mapping_path;
    private static String mapping_pattern;
    private static final byte[] lock = new byte[0];
    private static Boolean started = false;

    private static void run(ServletContext servletContext) throws IOException {
        synchronized (lock) {
            if(!started && servletContext != null && mapping_path != null && ! mapping_path.trim().isEmpty() ) {
                XmlWebApplicationContext mvcContext = new XmlWebApplicationContext();
                mvcContext.setConfigLocation(mapping_path);
                DispatcherServlet dispatcherServlet = new DispatcherServlet( mvcContext );
                ServletRegistration.Dynamic dispatcher = servletContext.addServlet( "dispatcher", dispatcherServlet );
                dispatcher.setAsyncSupported(true);// 支持异步servlet
                dispatcher.setLoadOnStartup(1);// 确保在default servlet加载完成之后再加载
                dispatcher.addMapping(mapping_pattern);
                started = true;
            }
        }
    }

    //指定配置文件
    public static void config_mvc(String mapping_path, String mapping_pattern) throws IOException {
        JettyWebApplicationInitializer.mapping_path = mapping_path;
        JettyWebApplicationInitializer.mapping_pattern = mapping_pattern == null ? "/*" : mapping_pattern;
        run(servletContext);
    }

    public static void config_mvc(String mapping_path) throws IOException {
        config_mvc(mapping_path, null);
    }

    public static void start_mvc(ServletContext servletContext) throws IOException {
        JettyWebApplicationInitializer.servletContext = servletContext;
        run(servletContext);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        try {
            start_mvc(servletContext);
        } catch (IOException e) {
            LOG.error("启动MVC框架失败:",e);
            System.exit(1);
        }
    }
}
