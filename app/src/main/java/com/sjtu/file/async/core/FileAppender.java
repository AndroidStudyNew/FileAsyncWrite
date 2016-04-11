package com.sjtu.file.async.core;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2016/4/11.
 */
public class FileAppender extends Appender<FileAppender.OutPrint> {

    private DataOutputStream out;

    public FileAppender(DataOutputStream out, int buffer) {
        super(buffer);
        this.out = out;
    }

    @Override
    void append(OutPrint op) {
        try {
            out.write(intToByte(op.eventId));
            out.write(Appender.TAB);
            out.write(op.value.getBytes("utf-8"));
            out.write(Appender.TAB);
            int time = (int) (System.currentTimeMillis() / 1000);
            out.write(intToByte(time));
            out.write(Appender.CRLF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (out != null) {
                out.flush();
                out.close();
            }
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.close();
    }
    
    public static class OutPrint {
        public OutPrint(int tag, int eventId, String value) {
            this.tag = tag;
            this.eventId = eventId;
            this.value = value;
        }
        int tag;
        int eventId;
        String value;
    }

    public static byte[] intToByte(int n){
        byte[] b = new byte[4];
        b[0] = (byte)(n&0xff);
        b[1] = (byte)(n>>8&0xff);
        b[2] = (byte)(n>>16&0xff);
        b[3] = (byte)(n>>24&0xff);
        return b;
    }
}
