package org.mokey.stormv.das.client;

import org.mokey.stormv.das.models.DalModels;

/**
 * Created by wcyuan on 2015/1/30.
 */
public class BuilderUtils {

    private final static String CLIENT = "java das client";
    private final static String VERSION = "2.0.0";

    public static DalModels.Request.Builder newRequestBuilder(String databaseName) {
        DalModels.Request.Builder builder = DalModels.Request.newBuilder();
        builder.setHints(DalModels.DalHints.newBuilder().build());
        builder.setLogicDb(databaseName);
        builder.setVersion(DalModels.Version.newBuilder().setClient(CLIENT)
                .setVersion(VERSION).build());
        return builder;
    }
}
