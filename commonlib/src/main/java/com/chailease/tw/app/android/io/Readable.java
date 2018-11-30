package com.chailease.tw.app.android.io;

import java.io.IOException;

/**
 */
public interface Readable {

    public boolean open(String target) throws IOException;
    public String getText() throws IOException;
    public boolean close() throws IOException;
    public void release();

}
