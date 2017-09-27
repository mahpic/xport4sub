package edu.uga.mahpic.submission.dao;


import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by mnural on 8/4/15.
 */
public class BeanFactory {
    private static AbstractApplicationContext beanManager;

    private static AbstractApplicationContext getBeanManager() {
        if (beanManager == null) {
            beanManager = new ClassPathXmlApplicationContext("/application-context.xml");
        }
        return beanManager;
    }

    public static <T> T getBean(Class<T> c) {
        ApplicationContext bm = getBeanManager();
        return bm.getBean(c);
    }

    public static void updateDataSource(Class c){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(c);
//        builder.addPropertyValue("propertyName", someValue);      // set property value
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory)  getBeanManager().getBeanFactory();
        factory.registerBeanDefinition("dataSourceDao", builder.getBeanDefinition());
    }
}
