package cn.xiaocai.javaspi.demo;

import cn.xiaocai.javaspi.demo.service.HelloService;
import sun.misc.Service;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author: xiaocai
 * @create: 2021/05/27 23:07
 **/
public class TestMain {
    public static void main(String[] args) {

        // 方式1：通过 Service.providers 获取实现类的实例
        Iterator<HelloService> providers = Service.providers(HelloService.class);
        while (providers.hasNext()) {
            HelloService helloService = providers.next();
            helloService.say("Service.providers");
        }

        // 方式2：通过 ServiceLoader.load 获取
        ServiceLoader<HelloService> load = ServiceLoader.load(HelloService.class);
        Iterator<HelloService> iterator = load.iterator();
        while (iterator.hasNext()) {
            HelloService helloService = iterator.next();
            helloService.say("Service.providers");
        }
    }
}
