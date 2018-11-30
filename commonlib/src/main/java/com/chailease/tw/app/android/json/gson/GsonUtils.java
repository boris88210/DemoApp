package com.chailease.tw.app.android.json.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 *
 */
public class GsonUtils {

    public static final ExposeExclusionStrategy EXPOSE_STRATEGY_SERIALIZE = new ExposeExclusionStrategy();
    public static final ExposeExclusionStrategy EXPOSE_STRATEGY_DESERIALIZE = new ExposeExclusionStrategy();
    static {
        EXPOSE_STRATEGY_SERIALIZE.serialize = true;
        EXPOSE_STRATEGY_DESERIALIZE.serialize = false;
    }


    private Gson serializeGson;
    private Gson deserializeGson;
    private ExclusionStrategy serializeStrategy;
    private ExclusionStrategy deserializeStrategy;

    public static GsonUtils genGsonUtils(final ExclusionStrategy serializeStrategy, final ExclusionStrategy deserializeStrategy) {
        GsonUtils instance = new GsonUtils();
        instance.serializeStrategy = serializeStrategy;
        instance.deserializeStrategy = deserializeStrategy;
        return instance;
    }

    public static String jsonString(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
    public static <T> T parseJson(String jsonStr, Class<T> tClass) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, tClass);
    }

    private void initGson() {
        if (null!= serializeStrategy && null == serializeGson) {
            final GsonBuilder builder = new GsonBuilder();
            builder.setExclusionStrategies(serializeStrategy);
            serializeGson = builder.create();
        } else {
            serializeGson = new Gson();
        }
        if (null != deserializeStrategy && null == deserializeGson) {
            final GsonBuilder builder = new GsonBuilder();
            builder.setExclusionStrategies(deserializeStrategy);
            deserializeGson = builder.create();
        } else {
            deserializeGson = new Gson();
        }
    }

    public String toJsonString(Object obj) {
        initGson();
        return serializeGson.toJson(obj);
    }
    public <T> T parse(String jsonStr, Class<T> tClass) {
        initGson();
        return deserializeGson.fromJson(jsonStr, tClass);
    }

    static public class ExposeExclusionStrategy implements ExclusionStrategy {
        private boolean serialize;

        public boolean shouldSkipField(FieldAttributes f) {
            //return (f.getDeclaringClass() == Plane.class && f.getName().equals("cost"));
            Collection<Annotation> anns = f.getAnnotations();
            if (null != anns && anns.size()>0) {
                for (Annotation ann : anns) {
                    if (ann instanceof Expose) {
                        Expose exp = (Expose) ann;
                        if (serialize) return !exp.serialize();
                        else return !exp.deserialize();
                    }
                }
            }
            return false;
        }

        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

    }

}