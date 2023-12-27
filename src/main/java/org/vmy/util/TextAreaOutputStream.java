package org.vmy.util;

import java.io.OutputStream;

import javax.swing.*;

/**
 * TextAreaOutputStream creates an outputstream that will output to the
 * given textarea. Useful in setting System.out
 */
public class TextAreaOutputStream extends OutputStream {
    public static final int DEFAULT_BUFFER_SIZE = 1;
    JTextArea mText;
    JScrollPane mScroll;
    JScrollBar horScrBar;
    JScrollBar verScrBar;
    byte mBuf[];
    int mLocation;

    public TextAreaOutputStream(JTextArea component, JScrollPane scrollPane) {
        this(component, DEFAULT_BUFFER_SIZE);
        this.mScroll = scrollPane;
        horScrBar = mScroll.getHorizontalScrollBar();
        verScrBar = mScroll.getVerticalScrollBar();
    }

    public TextAreaOutputStream(JTextArea component, int bufferSize) {
        mText = component;
        if (bufferSize < 1) bufferSize = 1;
        mBuf = new byte[bufferSize];
        mLocation = 0;
    }

    @Override
    public void write(int arg0) {
        //System.err.println("arg = "  + (char) arg0);
        mBuf[mLocation++] = (byte)arg0;
        if (mLocation == mBuf.length) {
            flush();
        }
    }

    public void flush() {
        mText.append(new String(mBuf, 0, mLocation));
        mLocation = 0;
//        try { Thread.sleep(1); } catch (Exception ignored) {}
//        horScrBar.setValue(horScrBar.getMinimum());
//        verScrBar.setValue(verScrBar.getMaximum());
    }
}
