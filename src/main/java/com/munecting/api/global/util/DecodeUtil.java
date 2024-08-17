package com.munecting.api.global.util;

import java.util.Base64;

public class DecodeUtil {

    public static byte[] decodeBase64(String string) {
        return Base64.getUrlDecoder().decode(string);
    }
}
