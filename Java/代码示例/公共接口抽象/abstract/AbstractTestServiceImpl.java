package triz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import triz.service.StrategyService;
import triz.service.TestService;

import java.util.Map;

@Service
@Slf4j
public abstract class AbstractTestServiceImpl  implements TestService {

    @Autowired
    private Map<String, StrategyService> strategyServiceMap;


    public abstract String insert();


    @Override
    public String update(String id) {
        String name = StringUtils.lowerCase(id) + StrategyService.class.getSimpleName()+"Impl";
        StrategyService strategyService = strategyServiceMap.get(name);
        return strategyService.update(id);
    }
}