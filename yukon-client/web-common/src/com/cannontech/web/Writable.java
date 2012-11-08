package com.cannontech.web;

import java.io.IOException;
import java.io.Writer;

public interface Writable {
    public void write(Writer out) throws IOException;
}
