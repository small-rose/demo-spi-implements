package cn.xiaocai.springspi.demo.extensionimpl;

import cn.xiaocai.springspi.demo.service.HelloService;
import cn.xiaocai.springspi.demo.service.SpringService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: zqzhou
 * @create: 2020/01/09 00:12
 **/
public class ExtensionImplB implements HelloService {

    @Autowired
    SpringService springService;

    @Override
    public void say(String name) {
        springService.test(name + "->ExtensionImplB");
    }
}
