package com.hqq.api.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by qing on 2017/9/20.
 */
public class NativePath {
    private static final Logger LOG = LogManager.getLogger(NativePath.class.getName());

    public NativePath() {
    }

    public static Path get(String path) {
        if(path.startsWith("/")) {
            path = path.substring(1);
        }

        String java_class_path = get_class_path();
        if(java_class_path.endsWith(".jar")) {
            int lastIndexOf = java_class_path.lastIndexOf("/");
            if(lastIndexOf == -1) {
                java_class_path = "";
            } else {
                java_class_path = java_class_path.substring(0, lastIndexOf);
            }
        }

        if(!java_class_path.isEmpty() && !java_class_path.endsWith("/")) {
            java_class_path = java_class_path.concat("/");
        }

        java_class_path = java_class_path.concat(path);
        LOG.info("final path ---> :".concat(java_class_path));
        return Paths.get(java_class_path, new String[0]);
    }

    public static String get_class_path(Class<?> clazz) {
        String location = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        location = location.replace("file:", "");
        if(System.getProperty("os.name").indexOf("Windows") != -1) {
            location = location.substring(1);
        }

        if(location.contains(".jar!")) {
            location = location.substring(0, location.indexOf(".jar!")).concat(".jar");
        }

        if(location.endsWith("/")) {
            location = location.substring(0, location.length() - 1);
        }

        return location;
    }

    public static String get_class_path() {
        String java_class_path = System.getProperty("java.class.path");
        LOG.debug("java_class_path -> :".concat(java_class_path));
        LOG.debug(System.getProperty("os.name"));
        int indexof_classes;
        int indexof_separator;
        int indexof_web_inf;
        int comma;
        String webroot;
        if(System.getProperty("os.name").indexOf("Windows") != -1) {
            indexof_classes = java_class_path.indexOf("\\classes");
            if(indexof_classes != -1) {
                java_class_path = java_class_path.substring(0, indexof_classes).concat("\\classes");
                indexof_separator = java_class_path.lastIndexOf(";");
                if(indexof_separator != -1) {
                    java_class_path = java_class_path.substring(indexof_separator + 1);
                }

                LOG.debug("windows code start --> :".concat(java_class_path));
            } else {
                webroot = NativePath.class.getResource("").getFile();
                webroot = webroot.replace("file:/", "");
                indexof_web_inf = webroot.indexOf("/WEB-INF/");
                if(indexof_web_inf != -1) {
                    java_class_path = webroot.substring(0, indexof_web_inf).concat("/WEB-INF/classes");
                    LOG.debug("windows server start --> :".concat(java_class_path));
                } else {
                    comma = java_class_path.indexOf(";");
                    if(comma > 0) {
                        java_class_path = java_class_path.substring(0, comma);
                    }

                    LOG.debug("windows jar start --> :".concat(java_class_path));
                }
            }
        } else {
            indexof_classes = java_class_path.indexOf("/classes");
            if(indexof_classes != -1) {
                java_class_path = java_class_path.substring(0, indexof_classes).concat("/classes");
                indexof_separator = java_class_path.lastIndexOf(":");
                if(indexof_separator != -1) {
                    java_class_path = java_class_path.substring(indexof_separator + 1);
                }

                LOG.debug("linux code start --> :".concat(java_class_path));
            } else {
                webroot = NativePath.class.getResource("").getFile();
                webroot = webroot.replace("file:", "");
                indexof_web_inf = webroot.indexOf("/WEB-INF/");
                if(indexof_web_inf != -1) {
                    java_class_path = webroot.substring(0, indexof_web_inf).concat("/WEB-INF/classes");
                    LOG.debug("linux server start --> :".concat(java_class_path));
                } else {
                    comma = java_class_path.indexOf(":");
                    if(comma > 0) {
                        java_class_path = java_class_path.substring(0, comma);
                    }

                    LOG.debug("linux jar start --> :".concat(java_class_path));
                }
            }
        }

        return java_class_path;
    }
}
