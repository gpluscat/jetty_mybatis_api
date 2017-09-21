package com.hqq.api.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by qing on 2017/9/20.
 */
public class JettyServer {
    private static final Logger LOG = LogManager.getLogger(JettyServer.class);

    private volatile static JettyServer singleton;
    private List<String> classPaths = new ArrayList<>();

    private JettyServer(){

    }

    public static JettyServer getSingleton() {
        if (singleton == null) {
            synchronized(JettyServer.class ) {
                if (singleton == null) {
                    singleton = new JettyServer();
                }
            }
        }
        return singleton;
    }

    public JettyServer start(Integer port){
        return start(port, "/");
    }

    public JettyServer start(Integer port, String context){
        return start(port, context, NativePath.get("webapp").toString(), NativePath.get("webapp/WEB-INF/web.xml").toString());
    }

    public JettyServer start(Integer port, String context, String webapp, String descriptor){
        classPaths.add(NativePath.get_class_path(JettyServer.class));
        Thread t = new Thread( () -> {
            try {
                final Server server = createServer(port, context, webapp, descriptor, classPaths.toArray(new String[]{}));
                server.start();
                server.join();
            } catch ( Exception e ) {
                LOG.error( "启动jetty失败", e );
                System.exit( - 1 );
            }
        } );
        t.setDaemon( true );
        t.start();
        return this;
    }

    public Server createServer(int port, String context, String webapp, String descriptor, String[] classpaths) throws MalformedURLException {
        Server server = new Server();
        // 设置在JVM退出时关闭Jetty的钩子。
        server.setStopAtShutdown(true);

        // 这是http的连接器
        HttpConfiguration config = new HttpConfiguration();
        HttpConnectionFactory http1_1 = new HttpConnectionFactory(config);
        ServerConnector connector = new ServerConnector(server, new ConnectionFactory[]{http1_1});
        connector.setPort(port);

        server.setConnectors(new Connector[]{connector});

        WebAppContext webContext = new WebAppContext();
        webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        webContext.setContextPath(context);
        webContext.setResourceBase(webapp);
        webContext.setDescriptor(descriptor);

        Set<Resource> set = new HashSet();
        set.add(Resource.newResource(NativePath.get_class_path()));
        set.add(Resource.newResource(NativePath.get_class_path(JettyServer.class)));
        if(classpaths != null) {
            String[] var12 = classpaths;
            int var13 = classpaths.length;

            for(int var14 = 0; var14 < var13; ++var14) {
                String classpath = var12[var14];
                set.add(Resource.newResource(classpath));
            }
        }

        Iterator var16 = set.iterator();

        while(var16.hasNext()) {
            Resource resource = (Resource)var16.next();
            LOG.info("add jetty annotation config dir => " + resource.getName());
            webContext.getMetaData().addContainerResource(resource);
        }

        webContext.setConfigurations(new Configuration[]{new JettyWebXmlConfiguration(), new WebXmlConfiguration(), new AnnotationConfiguration()});
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[]{webContext});
        server.setHandler(handlerCollection);

        return server;
    }

    public void startJetty(int port) throws IOException {
        LOG.info("[JettyServer] [startJetty] start");
        JettyWebApplicationInitializer.config_mvc("classpath:conf/spring_mvc.xml");
        JettyServer.getSingleton().start(port, "/api");
    }
}
