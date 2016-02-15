package org.mokey.stormv.das.worker.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by wcyuan on 2015/2/15.
 */
public class Configurator {
    private static Logger logger = LoggerFactory.getLogger(Configurator.class);
    private static final String appName = "dalworker";

    private volatile static String environment;
    private static String configDir;
    private volatile static boolean initialized = false;

    public static void config(String configDir){
        Configurator.configDir = configDir;
        environment = System.getProperty("archaius.deployment.environment");

        if(!initialized) {
            if(environment == null || environment.isEmpty()){
                environment = "test";
                System.setProperty("archaius.deployment.environment", environment);
            }

            configLogback();
            configProperties();
            initialized = true;
        }
    }

    private static void configLogback() {
        logger.info("To reconfigure logbook.");
        try {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            try {
                JoranConfigurator joranConfigurator = new JoranConfigurator();
                joranConfigurator.setContext(lc);
                lc.reset();

                String configFileName = "/logback-" + environment + ".xml";

                String logback = getConfigFilePath(configFileName);

                logger.info("logback config file:" + logback);

                if (!new File(logback).exists()) {
                    throw new Exception("Can't find the logbook config file [ " + configFileName + " ].");
                }

                joranConfigurator.doConfigure(logback);

                logger.info("Reconfigure logbook.");
            } catch (JoranException e) {
                e.printStackTrace();
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        } catch (Exception e) {
            logger.warn("Failed to reconfigure logbook.", e);
        }
    }

    private static void configProperties(){
        logger.info("To reconfigure properties.");
        try {
            String additionalUrls = System.getProperty("archaius.configurationSource.additionalUrls");
            if (additionalUrls == null || additionalUrls.isEmpty()) {
                String propertiesFileName = "/" + appName + "-" + environment + ".properties";

                String pros = getConfigFilePath(propertiesFileName);

                logger.info("archaius.configurationSource.additionalUrls=" + pros);
                System.setProperty("archaius.configurationSource.additionalUrls", "file://" + pros);
            }
            logger.info("Reconfigure properties.");
        }catch (Exception e){
            logger.warn("Failed to reconfigure properties.", e);
        }
    }

    private static String getConfigFilePath(String configFile){
        String confPath;
        if(configDir == null || configDir.isEmpty()){
            return Configurator.class.getResource(configFile).getPath();
        }
        File dir = new File(configDir);
        if(dir.exists() && dir.isDirectory()){
            confPath = dir.getPath() + configFile;
        }else{
            confPath = Configurator.class.getResource(configFile).getPath();
        }
        return confPath;
    }
}
