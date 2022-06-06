package com.wsn.powerstrip.communication.POJO.VO;

import net.sf.json.JSONObject;
import org.apache.commons.net.util.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @Author: jia
 * Time: 2022/6/1  11:04
 * Description:
 * Version:
 */
public class SmsNet {

    public static String smsUri = "http://10.3.1.216/sms";

    public static final int KEY_LENGTH = 16;

    public static final String FILL_CHAR = "0";

    /** AES偏移量，必须16字节 */
    public static String IV_PARAMETER = "hfouH6789087#754";

    public static String AES_MODE = "AES/CBC/PKCS5Padding";


    public static final String UTF8 = "UTF-8";

    public static final String MD5 = "MD5";

    public static final String LINE = "-";

    public static final String AES = "AES";

    public static final String ASCII = "ASCII";




    public static String sendMsg(String appId, String secretkey, String mobiles, String content, String type) {
        String encryptRequest = encrytString(appId, secretkey, mobiles, content, type);
        // 请求接口
        String result = invokeSmsInterface(encryptRequest);
        return result;
    }

    public static String encrytString(String appId, String secretkey, String mobiles, String content, String type) {
        StringBuilder sb = new StringBuilder();
        // 拼接参数
        sb.append(appId).append(secretkey).append(mobiles).append(content);
        // 生成信息摘要
        String digest = EncoderByMd5(sb.toString());
        // 封装请求信息
        Request request = new Request();
        request.setAppId(appId);
        request.setContent(content);
        request.setDigest(digest);
        request.setMobiles(mobiles);
        request.setSecretKey(secretkey);
        request.setType(type);

        // 加密
        String encryptRequest = encryptRequest(JSONObject.fromObject(request).toString());
        return encryptRequest;
    }

    public static String encrytString(String appId, String secretkey, String mobiles, String content) {
        StringBuilder sb = new StringBuilder();
        // 拼接参数
        sb.append(appId).append(secretkey).append(mobiles).append(content);
        // 生成信息摘要
        String digest = EncoderByMd5(sb.toString());
        // 封装请求信息
        Request request = new Request();
        request.setAppId(appId);
        request.setContent(content);
        request.setDigest(digest);
        request.setMobiles(mobiles);
        request.setSecretKey(secretkey);

        // 加密
        String encryptRequest = encryptRequest(JSONObject.fromObject(request).toString());
        return encryptRequest;
    }




    //util
    /**
     * AES-CBC-PKCS5Padding加密
     *
     * @param sSrc
     * @param encodingFormat
     * @param sKey
     * @param ivParameter
     * @return
     * @throws Exception
     */
    public static String encrypt(String sSrc, String encodingFormat, String sKey, String ivParameter) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_MODE);
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(encodingFormat));
        // 此处使用BASE64做转码。
        return new Base64().encodeBase64String(encrypted);
    }

    /**
     * AES-CBC-PKCS5Padding解密
     *
     * @param sSrc
     * @param encodingFormat
     * @param sKey
     * @param ivParameter
     * @return
     * @throws Exception
     */
    public static String decrypt(String sSrc, String encodingFormat, String sKey, String ivParameter) throws Exception {
        try {
            byte[] raw = sKey.getBytes(ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            // 先用base64解密
            byte[] encrypted1 = new Base64().decodeBase64(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, encodingFormat);
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String byteToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; ++i) {
            int temp = bytes[i] & 0xff;
            String tempHex = Integer.toHexString(temp);
            if (tempHex.length() < 2) {
                result += "0" + tempHex;
            } else {
                result += tempHex;
            }
        }
        return result;
    }

    public static String EncoderByMd5(String src) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
        }
        byte[] bytes = null;
        try {
            bytes = md5.digest(src.getBytes(UTF8));
        } catch (UnsupportedEncodingException e) {
        }
        return byteToHexString(bytes).toLowerCase();
    }

    public static String prd16BKey() {
        String uuid = UUID.randomUUID().toString().replace(LINE, "");

        String result = uuid.substring(0, KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH - result.length(); ++i) {
            result += FILL_CHAR;
        }
        return result.toUpperCase();
    }

    public static String invokeSmsInterface(String requestBody) {
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.postForEntity(smsUri, requestBody, String.class);
        return response.getBody();

    }

    public static String encryptRequest(String request) {
        StringBuilder result = new StringBuilder();
        String key = prd16BKey();
        String ciphertext = null;
        try {
            ciphertext = encrypt(request, UTF8, key, IV_PARAMETER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.append(key).append(ciphertext);
        return result.toString();
    }



}
