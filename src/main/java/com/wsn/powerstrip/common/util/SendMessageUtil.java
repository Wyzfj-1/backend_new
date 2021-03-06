//package com.wsn.smartoutlet.util;
//
//import com.alibaba.fastjson.JSONObject;
//import com.aliyuncs.CommonRequest;
//import com.aliyuncs.CommonResponse;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class SendMessageUtil {
//
//    public static String sendMessageWithAliyun(String phoneNumbers, String signName, String templateCode, String templateParam) {
//        DefaultProfile profile = DefaultProfile.getProfile("default", "LTAIN8Cs5StY4GZr", "ac1jihaEodnPUcv9X46eLLPERPurvd");
//        IAcsClient client = new DefaultAcsClient(profile);
//
//        CommonRequest request = new CommonRequest();
//        //request.setProtocol(ProtocolType.HTTPS);
//        request.setMethod(MethodType.POST);
//        request.setDomain("dysmsapi.aliyuncs.com");
//        request.setVersion("2017-05-25");
//        request.setAction("SendMessageByAliyun");
//
//        request.putQueryParameter("PhoneNumbers", phoneNumbers);
//        request.putQueryParameter("SignName", signName);
//        request.putQueryParameter("TemplateCode", templateCode);
//        request.putQueryParameter("TemplateParam", templateParam);
//        try {
//            CommonResponse response = client.getCommonResponse(request);
//            return response.getData();
//        } catch (ClientException e) {
//            e.printStackTrace();
//            return "ClientException";
//        }
//    }
//
//    public static String querySendDetails(String phoneNumbers, String senDate, String pageSize, String currentPage){
//        DefaultProfile profile = DefaultProfile.getProfile("default", "LTAIN8Cs5StY4GZr", "ac1jihaEodnPUcv9X46eLLPERPurvd");
//        IAcsClient client = new DefaultAcsClient(profile);
//
//        CommonRequest request = new CommonRequest();
//        //request.setProtocol(ProtocolType.HTTPS);
//        request.setMethod(MethodType.POST);
//        request.setDomain("dysmsapi.aliyuncs.com");
//        request.setVersion("2017-05-25");
//        request.setAction("QuerySendDetails");
//
//        request.putQueryParameter("PhoneNumbers", phoneNumbers);
//        request.putQueryParameter("senDate", senDate);
//        request.putQueryParameter("pageSize", pageSize);
//        request.putQueryParameter("currentPage", currentPage);
//
//        try {
//            CommonResponse response  = client.getCommonResponse(request);
//            return response.getData();
//        } catch (ClientException e) {
//            e.printStackTrace();
//            return "ClientException";
//        }
//    }
//
//    public static String sendMessageWithSCU(String mobiles,String content){
//        try {
//            URL url = new URL("http://202.115.62.13/sms");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();//????????????
//
//            connection.setRequestMethod("POST");//?????????????????????POST
//            connection.setDoOutput(true);//??????POST??????????????????????????????true????????????false
//            connection.setDoInput(true);//??????POST??????????????????????????????true????????????false
//            connection.setConnectTimeout(5000);//????????????????????????
//
//            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");//?????????????????????json??????
//            connection.addRequestProperty("Connection", "Keep-Alive");
//
//            JSONObject obj = new JSONObject(true);//JSON??????
//            obj.put("appId", "prim1541666805008");
//            obj.put("secretKey", "40a957c6");
//            obj.put("mobiles", mobiles);
//            obj.put("content", content);
//            obj.put("digest", MD5Util.encrypt("prim154166680500840a957c6"+mobiles+content));//MD5??????
//
//            //??????1????????????????????????2????????????????????????3???iv?????????
//            String body=AESUtil.encrypt(obj.toJSONString());
//            PrintWriter printWriter=new PrintWriter(connection.getOutputStream());
//            if (body != null) {
//                printWriter.write("rmyNsXJxnDdCYCYA"+body);
//            }
//            printWriter.flush();
//            printWriter.close();
//
//            int respondCode = connection.getResponseCode();
//            if (respondCode == HttpURLConnection.HTTP_OK) {
//                InputStream inputStream = connection.getInputStream();
//                return StringAndStreamUtil.Stream2String(inputStream);
//            }else {
//                return "respondCode:"+respondCode;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
