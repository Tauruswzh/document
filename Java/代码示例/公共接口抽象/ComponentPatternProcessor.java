package witpdp.pattern.component;

import org.springframework.stereotype.Service;
import witpdp.pattern.service.AbstractPatternProcessor;

/**
 * 文件名：ComponentPatternProcessor.java
 * 作者:  xiahao
 * 时间:  2022/6/13 9:42
 * 描述:  组合模式处理器
 */

@Service("componentPatternProcessor")
public class ComponentPatternProcessor extends AbstractPatternProcessor {

    @Override
    public String update() {
        return "组合模式实现.....";
    }
}
