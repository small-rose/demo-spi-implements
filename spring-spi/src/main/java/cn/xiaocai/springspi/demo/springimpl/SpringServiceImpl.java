package cn.xiaocai.springspi.demo.springimpl;

import cn.xiaocai.springspi.demo.service.HelloService;
import cn.xiaocai.springspi.demo.service.SpringService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author: zqzhou
 * @create: 2020/01/08 23:44
 **/
public class SpringServiceImpl implements SpringService, ApplicationContextAware {

    HelloService helloService;


    @Override
    public void test(String name) {
        System.out.println(name + " 通过SPI扩展并注入了本Spring大人托管的Bean了哟！");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.helloService = applicationContext.getBean(HelloService.class);
    }
}
