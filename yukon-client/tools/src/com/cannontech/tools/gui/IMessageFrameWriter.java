package com.cannontech.tools.gui;

import java.io.IOException;
import java.io.Writer;

public class IMessageFrameWriter extends Writer {
    
    
    private final IMessageFrame messageFrame;

    public IMessageFrameWriter(IMessageFrame messageFrame) {
        this.messageFrame = messageFrame;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void write(char[] arg0, int arg1, int arg2) throws IOException {
        messageFrame.addOutputNoLN(new String(arg0).substring(arg1, arg1+arg2));
    }

}
