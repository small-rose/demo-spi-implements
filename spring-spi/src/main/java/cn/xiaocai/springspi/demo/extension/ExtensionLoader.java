package cn.xiaocai.springspi.demo.extension;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xiaocai 学习整理
 * @create: 2021/05/27
 **/
public class ExtensionLoader implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    ApplicationContext context;

    BeanDefinitionRegistry beanDefinitionRegistry;

    ConcurrentHashMap<Class<?>, Map<String, Object>> EXTENSIONS = new ConcurrentHashMap<>();

    private static final String SPI_DIRECTORY = "META-INF/xiaocai/";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;

        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
        beanDefinitionRegistry = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        try {
            ClassLoader classLoader = DefaultListableBeanFactory.class.getClassLoader();
            URL resource;
            File[] files;

            if (classLoader != null) {
                resource = classLoader.getResource(this.SPI_DIRECTORY);
            } else {
                resource = ClassLoader.getSystemResource(this.SPI_DIRECTORY);
            }

            files = new File(resource.getFile()).listFiles();

            for (int i = 0; i < files.length; i++) {
                Class<?> clazz = Class.forName(files[i].getName(), true, classLoader);
                EXTENSIONS.putIfAbsent(clazz, loadExtensionClass(clazz.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    /**
     * 获取某个接口类型对应的实现
     *
     * @param type
     * @return
     */
    public Map<String, Object> getExtensions(Class type) {
        if (null == type) {
            throw new IllegalArgumentException("Extension Class is null");
        }

        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension Class is not an interface");
        }

        Map<String, Object> loader = EXTENSIONS.get(type);
        if (loader == null) {
            synchronized (ExtensionLoader.class) {
                loader = EXTENSIONS.get(type);
                if (loader == null) {
                    EXTENSIONS.putIfAbsent(type, loadExtensionClass(type.getName()));
                    loader = EXTENSIONS.get(type);
                }
            }
        }
        return loader;
    }

    /**
     * 从扩展文件中加载类
     *
     * @param type
     * @return
     */
    private Map<String, Object> loadExtensionClass(String type) {
        Map<String, Object> extensionClasses = new HashMap<>();
        loadDirectory(extensionClasses, SPI_DIRECTORY, type);
        return extensionClasses;
    }

    /**
     * 加载文件夹
     *
     * @param extensionClasses
     * @param dir
     * @param type
     */
    private void loadDirectory(Map<String, Object> extensionClasses, String dir, String type) {
        String fileName = dir + type;
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = DefaultListableBeanFactory.class.getClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourcesURL = urls.nextElement();
                    loadResources(extensionClasses, classLoader, resourcesURL);
                }
            }
        } catch (Throwable t) {
        }
    }

    private void loadResources(Map<String, Object> extensionClasses, ClassLoader classLoader, URL resourceURL) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) {
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (line.length() > 0) {
                        try {
                            String name = null;
                            int i = line.indexOf('=');
                            if (i > 0) {
                                name = line.substring(0, i).trim();
                                line = line.substring(i + 1).trim();
                            }
                            if (line.length() > 0) {
                                loadClass(extensionClasses, resourceURL, Class.forName(line, true, classLoader), name);
                            }
                        } catch (Throwable t) {
                            IllegalStateException e = new IllegalStateException("Failed to load extension class (class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
        }
    }

    private void loadClass(Map<String, Object> extensionClasses, java.net.URL resourceURL, Class<?> clazz, String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalStateException("No such extension name for the class " + name + " in the config " + resourceURL);
        }
        Object o = extensionClasses.get(name);
        if (o == null) {
            Object bean = injectBeanToSpring(name, clazz);
            extensionClasses.put(name, bean);
        } else {
            throw new IllegalStateException("Duplicate extension name " + name + " on " + clazz.getName() + " and " + clazz.getName());
        }
    }

    /**
     * 动态注入bean到spring容器
     *
     * @param name
     * @param obj
     * @return
     */
    private Object injectBeanToSpring(String name, Class<?> obj) {
        String beanName = obj.getSimpleName().concat(name);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(obj);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_NAME);
        beanDefinitionRegistry.registerBeanDefinition(beanName, definition);

        // TODO: 2020/1/9  这里动态注入的bean并未将内部的@Autowired的bean依赖注入进去，如何解决？

        // 通过反射设置@Autowired标记的字段的值

        Object bean = context.getBean(beanName);

        Field[] declaredFields = obj.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object aClass = context.getBean(field.getType());

                ReflectHelper.setFieldValue(bean, field.getName(), aClass);
            }
        }

        return bean;
    }
}
