package cn.xiaocai.javaspi.demo.serviceimpl;

import cn.xiaocai.javaspi.demo.service.HelloService;

/**
 * @author: xiaocai
 * @create: 2021/05/27 23:07
 **/
public class HelloServiceImpl implements HelloService {
    public void say(String name) {
        System.out.println(name + " say Hello!");
    }
}
