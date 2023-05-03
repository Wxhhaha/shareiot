package com.wxh.shareiot.utils;

import android.content.Context;
import android.util.Base64;

import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;

public class WechatUtil {
    /**
     * 生成32位随机数
     *
     * @return
     */
    public static String getNonceStr() {
        Random random = new Random();
        StringBuffer nonceStr = new StringBuffer();
        for (int i = 0; i < 32; i++) {
            String str = random.nextInt(2) % 2 == 0 ? "num" : "char";
            if ("char".equalsIgnoreCase(str)) { // 产生字母
                nonceStr.append((char) (97 + random.nextInt(26)));
            } else if ("num".equalsIgnoreCase(str)) { // 产生数字
                nonceStr.append(String.valueOf(random.nextInt(10)));
            }
        }
        return nonceStr.toString();
    }

    /**
     * 签名
     * @param context
     * @param appid
     * @param timestamp
     * @param nonceStr
     * @param prepayid
     * @param filename
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static String sign(Context context, String appid, String timestamp, String nonceStr, String prepayid, String filename) throws NoSuchAlgorithmException, IOException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(getRSAPrivateKey(context, filename));
        String message = appid + "\n" + timestamp + "\n" + nonceStr + "\n" + prepayid + "\n";
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(sign.sign(), Base64.DEFAULT);
    }

    /**
     * 获取秘钥
     *
     * @param context
     * @param filename
     * @return
     * @throws IOException
     */
    public static PrivateKey getRSAPrivateKey(Context context, String filename) throws IOException {
        String pem = convertStreamToString(context.getAssets().open(filename));
        pem = pem.replace("-----BEGIN PRIVATE KEY-----", "");
        pem = pem.replace("-----END PRIVATE KEY-----", "");
        pem = pem.replace("/n", "");
        LogUtils.e(pem);
        byte[] decoded = Base64.decode(pem, Base64.DEFAULT);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        PrivateKey privateKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");

            privateKey = kf.generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式", e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return privateKey;
    }


    /**
     * 数据流转成字符串
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
