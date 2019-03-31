package com.development.borissu.demoapp.network.api.factory;

import android.content.Context;

import static com.development.borissu.demoapp.constant.CoreConstant.TestFactoryName;

public abstract class ApiFactory {

    private static ApiFactory INSTANCE;


    public static ApiFactory getInstance(Context context) {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        try {
            INSTANCE = (ApiFactory) Class.forName(TestFactoryName).newInstance();
            INSTANCE.initFactory(context.getApplicationContext());

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return INSTANCE;
    }

    public abstract void initFactory(Context context);

}
