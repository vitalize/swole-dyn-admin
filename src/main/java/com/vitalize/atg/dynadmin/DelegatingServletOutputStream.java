package com.vitalize.atg.dynadmin;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * An implementation of a ServletOutputStream that delegates all methods to a passed in ServletOuputStream
 * This is used extensively to detect when out of the box ATG is outputting some html you want to inject your stuff
 * before or after.
 */
public class DelegatingServletOutputStream extends ServletOutputStream {

    private final ServletOutputStream delegate;

    public DelegatingServletOutputStream(ServletOutputStream o){
        delegate = o;
    }

    @Override
    public void print(String s) throws IOException {
        delegate.print(s);
    }

    @Override
    public void print(boolean b) throws IOException {
        delegate.print(b);
    }

    @Override
    public void print(char c) throws IOException {
        delegate.print(c);
    }

    @Override
    public void print(int i) throws IOException {
        delegate.print(i);
    }

    @Override
    public void print(long l) throws IOException {
        delegate.print(l);
    }

    @Override
    public void print(float f) throws IOException {
        delegate.print(f);
    }

    @Override
    public void print(double d) throws IOException {
        delegate.print(d);
    }

    @Override
    public void println() throws IOException {
        delegate.println();
    }

    @Override
    public void println(String s) throws IOException {
        delegate.println(s);
    }

    @Override
    public void println(boolean b) throws IOException {
        delegate.println(b);
    }

    @Override
    public void println(char c) throws IOException {
        delegate.println(c);
    }

    @Override
    public void println(int i) throws IOException {
        delegate.println(i);
    }

    @Override
    public void println(long l) throws IOException {
        delegate.println(l);
    }

    @Override
    public void println(float f) throws IOException {
        delegate.println(f);
    }

    @Override
    public void println(double d) throws IOException {
        delegate.println(d);
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
