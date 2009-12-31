package com.libraryh3lp.smsgateway;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Stack;

import org.w3c.dom.Element;

class KeySequence {
    public void addKeys(Element element) {
        if (! keys.empty()) {
            element.setAttribute("key", keys.pop());
        }
        if (keys.empty()) {
            generateSequence();
            element.setAttribute("newkey", keys.pop());
        }
    }

    private void generateSequence() {
        int    n     = (int) Math.floor(Math.random() * 20) + 30;
        byte[] bytes = new byte[40];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) Math.floor(Math.random() * 256.0);
        }

        String key = hexSha1(bytes);
        for (int i = 0; i < n; ++i, key = hexSha1(key)) {
            keys.push(key);
        }
    }

    private String hexSha1(String hexdigits) {
        byte[] bytes = new byte[hexdigits.length() / 2];
        for (int i = 0; i < hexdigits.length(); i += 2) {
            int hi = Integer.parseInt(""+hexdigits.charAt(i+0), 16);
            int lo = Integer.parseInt(""+hexdigits.charAt(i+1), 16);
            bytes[i/2] = (byte) (16*hi + lo);
        }
        return hexSha1(bytes);
    }

    private String hexSha1(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(bytes);
            byte[] result = digest.digest();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < result.length; ++i) {
                builder.append(Integer.toString(result[i], 16));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private final Stack<String> keys = new Stack<String>();
}
