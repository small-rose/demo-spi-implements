package cn.xiaocai.springspi.demo;

import cn.xiaocai.springspi.demo.service.HelloService;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * 此种方式创建的实例并未由spring托管
 * <p>
 * 在helloservice实现类中通过@Autowired无法注入spring bean，
 *
 * @author: zqzhou
 * @create: 2020/01/08 23:15
 **/
public class SpringSpiTestMain {
    public static void main(String[] args) {
        List<HelloService> helloServices = SpringFactoriesLoader.loadFactories(HelloService.class, null);
        helloServices.forEach(helloService -> {
            helloService.say("SpringFactoriesLoader.loadFactories");
        });
    }
}
