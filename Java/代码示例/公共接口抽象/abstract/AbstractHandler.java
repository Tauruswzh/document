package triz.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import triz.service.TestService;

import javax.annotation.PostConstruct;
import java.util.Map;


@Component
public class AbstractHandler {

    @Autowired
    private Map<String,TestService> testServiceMap;
    private static Map<String,TestService> testServiceMapStatic;

    @PostConstruct
    public void init(){
        testServiceMapStatic = testServiceMap;
    }

    public static TestService getInstance(String str){
        String name = StringUtils.lowerCase(str) + TestService.class.getSimpleName()+"Impl";
        return testServiceMapStatic.get(name);
    }
}
