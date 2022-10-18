package witpdp.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import witpdp.exception.PdpRuntimeException;
import witpdp.pattern.service.PatternProcessor;

import java.util.Map;
import java.util.Objects;

/**
 * 文件名：PatternHandlerFactory.java
 * 作者:  xiahao
 * 时间:  2022/6/13 9:43
 * 描述:  处理器仓库
 */

@Component
public class PatternHandlerFactory {
    //根据路径判断获取不同的设计模式

    @Autowired
    private Map<String, PatternProcessor> patternProcessorMap;

    public PatternProcessor getHandler(String type){
        //拼接处理器
        String name = type.toLowerCase() + PatternProcessor.class.getSimpleName();
        PatternProcessor patternProcessor = patternProcessorMap.get(name);
        if (Objects.isNull(patternProcessor)){
            throw new PdpRuntimeException("未获取到处理器");
        }
        return patternProcessor;
    }
}
