package com.wsn.powerstrip.common.config.feign;

import feign.Client;
import feign.Logger;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 10:53 AM 6/18/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Configuration
public class FeignConfig {

    final private ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    // new一个form编码器，实现支持form表单提交
    @Bean
    public SpringFormEncoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    /**
     * 开启Feign的日志
     *
     * @return
     */
    @Bean
    public Logger.Level logger() {
        return Logger.Level.BASIC;
    }

    //SSL忽略证书
    @Bean
    public Client feignClient() throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, PrivilegedActionException {
        return new Client.Default(
                getClientSSLSocketFactory(),new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession sslSession) {
                return true;
            }
        });
    }

    public SSLSocketFactory getClientSSLSocketFactory()
            throws NoSuchAlgorithmException, KeyStoreException, PrivilegedActionException,
            CertificateException, IOException, KeyManagementException, UnrecoverableKeyException
    {
        //创建KeyStore, 并导入证书:
        KeyStore kStore = KeyStore.getInstance(KeyStore.getDefaultType());

        ClassPathResource classPathResource = new ClassPathResource("cert/oceanconnect.jks");
        InputStream inputStream = classPathResource.getInputStream();

        kStore.load(inputStream, "".toCharArray());

        //创建KeyManager, 并导入keyStore
        KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(kStore, "".toCharArray());
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        // This supports TLSv1.2
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagers, new TrustManager[]{x509TrustManager}, null);

        return sslContext.getSocketFactory();
    }
}
