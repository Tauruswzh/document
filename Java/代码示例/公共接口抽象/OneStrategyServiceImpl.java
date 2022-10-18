package witpdp.pattern.strategy;

import org.springframework.stereotype.Service;

@Service("oneStrategyServiceImpl")
public class OneStrategyServiceImpl implements StrategyService {

    public String insert(){
        return "策略1 insert......";
    }
}
