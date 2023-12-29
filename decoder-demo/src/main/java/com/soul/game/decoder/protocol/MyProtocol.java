package com.soul.game.decoder.protocol;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class MyProtocol {

    /**
     * 消息头开始
     */
    private String head = "SOUL";

    /**
     * 消息长度
     */
    private int contentLen;

    /**
     * 消息内容
     *
     */
    private byte[] content;

    public MyProtocol(){}

    public MyProtocol(int contentLen, byte[] content) {
        this.contentLen = contentLen;
        this.content = content;
    }

    public String byteToHex(byte[] bytes, int cnt){
        String strHex;
        StringBuffer sb = new StringBuffer();
        for (int n = 0; n < cnt; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    public String byteToString(byte[] bytes, int cnt){
        return new String(bytes);
    }

    public static void main(String[] args) {
        String header = "SOUL";
        String messages[] = {"HELLO", "MATE"};
        byte[] res = new byte[256];
        byte[] headerBytes = header.getBytes();
        int useLen = 0;
        for(String message: messages) {
            byte[] messageBytes = message.getBytes();
            Short len = new Integer(messageBytes.length).shortValue();
            byte[] intBytes = ByteBuffer.allocate(2).putShort(len).array();
            System.arraycopy(headerBytes, 0, res, useLen, headerBytes.length);
            useLen += headerBytes.length;
            System.arraycopy(intBytes, 0, res,  useLen, intBytes.length);
            useLen += intBytes.length;
            System.arraycopy(messageBytes, 0, res,  useLen, messageBytes.length);
            useLen += message.length();
        }
        System.out.println(new MyProtocol().byteToHex(res, useLen));

    }

    @Override
    public String toString() {
        return "MyProtocol [head=" + head + ", contentLength="
                + contentLen + ", content=" + byteToString(content, contentLen) + " " + byteToHex(content, contentLen) + "]";
    }
}
