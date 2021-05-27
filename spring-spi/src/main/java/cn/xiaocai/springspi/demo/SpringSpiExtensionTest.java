package cn.xiaocai.springspi.demo;

import cn.xiaocai.springspi.demo.extension.ExtensionLoader;
import cn.xiaocai.springspi.demo.service.HelloService;
import cn.xiaocai.springspi.demo.service.SpringService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author: zqzhou
 * @create: 2020/01/08 23:15
 **/
public class SpringSpiExtensionTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("spring.xml");
        SpringService bean = classPathXmlApplicationContext.getBean(SpringService.class);
        bean.test("test");

        ExtensionLoader extensionLoader = classPathXmlApplicationContext.getBean(ExtensionLoader.class);

        HelloService helloServicea = (HelloService) extensionLoader.getExtensions(HelloService.class).get("a");
        helloServicea.say("a");

        HelloService helloServiceb = (HelloService) extensionLoader.getExtensions(HelloService.class).get("b");
        helloServiceb.say("b");
    }
}
