package witpdp.pattern.strategy;

import org.springframework.stereotype.Service;

@Service("twoStrategyServiceImpl")
public class TwoStrategyServiceImpl implements StrategyService {

    public String insert(){
        return "策略2 insert......";
    }
}
