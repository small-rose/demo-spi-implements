package cn.xiaocai.springspi.demo.springinjectnull;

import cn.xiaocai.springspi.demo.service.HelloService;
import cn.xiaocai.springspi.demo.service.SpringService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: zqzhou
 * @create: 2020/01/08 23:48
 **/
public class SayServiceImpl implements HelloService {

    @Autowired
    SpringService springService;

    @Override
    public void say(String name) {
        System.out.println(name);
        springService.test(name);
    }
}
