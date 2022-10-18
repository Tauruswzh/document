package witpdp.pattern.service;

import org.springframework.beans.factory.annotation.Autowired;
import witpdp.exception.PdpRuntimeException;
import witpdp.pattern.strategy.StrategyService;

import java.util.Map;
import java.util.Objects;

/**
 * 文件名：AbstractPatternProcessor.java
 * 作者:  xiahao
 * 时间:  2022/6/13 9:45
 * 描述:  公共接口实现
 */

public abstract class AbstractPatternProcessor implements PatternProcessor {

    @Autowired
    private Map<String, StrategyService> strategyServiceMap;
    /**
     * 组合模式处理
     */
    public abstract String update();

    /**
     * 策略模式处理
     */
    @Override
    public String insert(String type) {
        String name = type.toLowerCase() + StrategyService.class.getSimpleName() + "Impl";
        StrategyService strategyService = strategyServiceMap.get(name);
        if (Objects.isNull(strategyService)){
            throw new PdpRuntimeException("未获取到策略实现");
        }
        return strategyService.insert();
    }
}
