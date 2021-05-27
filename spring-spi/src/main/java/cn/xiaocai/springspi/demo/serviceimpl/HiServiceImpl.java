package cn.xiaocai.springspi.demo.serviceimpl;

import cn.xiaocai.springspi.demo.service.HelloService;

/**
 * @author: zqzhou
 * @create: 2020/01/08 23:08
 **/
public class HiServiceImpl implements HelloService {
    public void say(String name) {
        System.out.println(name + " say Hi!");
    }
}
