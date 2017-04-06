package com.vitalize.atg.dynadmin;

import org.junit.Test;

import javax.servlet.ServletOutputStream;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class DelegatingServletOuputStreamTest {

    static Random r = new Random();


    @Test
    public void testDelegation() throws IOException {

        ServletOutputStream mockServletOutputStream = mock(ServletOutputStream.class);


        DelegatingServletOutputStream subject = new DelegatingServletOutputStream(mockServletOutputStream);


        subject.flush();
        verify(mockServletOutputStream, times(1)).flush();
        verifyNoMoreInteractions(mockServletOutputStream);


        subject.close();
        verify(mockServletOutputStream, times(1)).close();
        verifyNoMoreInteractions(mockServletOutputStream);


        String s = UUID.randomUUID().toString();
        subject.print(s);
        verify(mockServletOutputStream, times(1)).print(s);
        verifyNoMoreInteractions(mockServletOutputStream);


        subject.print(true);
        verify(mockServletOutputStream, times(1)).print(true);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.print(false);
        verify(mockServletOutputStream, times(1)).print(false);
        verifyNoMoreInteractions(mockServletOutputStream);

        char c = (char)r.nextInt();
        subject.print(c);
        verify(mockServletOutputStream, times(1)).print(c);
        verifyNoMoreInteractions(mockServletOutputStream);

        int i = r.nextInt();
        subject.print(i);
        verify(mockServletOutputStream, times(1)).print(i);
        verifyNoMoreInteractions(mockServletOutputStream);

        long l = r.nextLong();
        subject.print(l);
        verify(mockServletOutputStream, times(1)).print(l);
        verifyNoMoreInteractions(mockServletOutputStream);

        float f = r.nextFloat();
        subject.print(f);
        verify(mockServletOutputStream, times(1)).print(f);
        verifyNoMoreInteractions(mockServletOutputStream);

        double d = r.nextDouble();
        subject.print(d);
        verify(mockServletOutputStream, times(1)).print(d);
        verifyNoMoreInteractions(mockServletOutputStream);


        subject.println();
        verify(mockServletOutputStream, times(1)).println();
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.println(s);
        verify(mockServletOutputStream, times(1)).println(s);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.println(true);
        verify(mockServletOutputStream, times(1)).println(true);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.println(false);
        verify(mockServletOutputStream, times(1)).println(false);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.println(c);
        verify(mockServletOutputStream, times(1)).println(c);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.println(i);
        verify(mockServletOutputStream, times(1)).println(i);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.println(l);
        verify(mockServletOutputStream, times(1)).println(l);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.println(f);
        verify(mockServletOutputStream, times(1)).println(f);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.println(d);
        verify(mockServletOutputStream, times(1)).println(d);
        verifyNoMoreInteractions(mockServletOutputStream);

        subject.write(i);
        verify(mockServletOutputStream, times(1)).write(i);
        verifyNoMoreInteractions(mockServletOutputStream);

        byte[] bytes = new byte[]{(byte)i};

        subject.write(bytes);
        verify(mockServletOutputStream, times(1)).write(bytes);
        verifyNoMoreInteractions(mockServletOutputStream);

        int j = r.nextInt();
        subject.write(bytes, i, j);
        verify(mockServletOutputStream, times(1)).write(bytes, i, j);
        verifyNoMoreInteractions(mockServletOutputStream);


    }
}
