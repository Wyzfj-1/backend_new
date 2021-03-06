//package com.wsn.smartoutlet.util;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SpringContextUtil implements ApplicationContextAware {
//
//    private static ApplicationContext applicationContext; // Spring应用上下文环境
//
//    /*
//     * 实现了ApplicationContextAware 接口，必须实现该方法；
//     *通过传递applicationContext参数初始化成员变量applicationContext
//     */
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        SpringContextUtil.applicationContext = applicationContext;
//    }
//
//
//    public static ApplicationContext getApplicationContext() {
//        return applicationContext;
//    }
//
//
//    @SuppressWarnings("unchecked")
//    public static <T> T getBean(String name) throws BeansException {
//        return (T) applicationContext.getBean(name);
//    }
//
//    /**
//     * 　* 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
//     * 　　 * @return boolean
//     */
//
//    public static boolean containsBean(String name) {
//        return applicationContext.containsBean(name);
//    }
//
//    /**
//     * 　　* 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
//     * 　　* 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
//     * 　　*
//     * 　　* @return boolean
//     */
//
//    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
//        return applicationContext.isSingleton(name);
//    }
//
//    /**
//     * 　　* @return Class 注册对象的类型
//     */
//
//    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
//        return applicationContext.getType(name);
//    }
//
//    /**
//     * 　　* 如果给定的bean名字在bean定义中有别名，则返回这些别名
//     */
//
//    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
//        return applicationContext.getAliases(name);
//    }
//
//}
