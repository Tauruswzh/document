package triz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import triz.service.StrategyService;

@Service("oneStrategyServiceImpl")
@Slf4j
public class StrategyOneServiceImpl implements StrategyService {


    @Override
    public String update(String id) {
        return "strategy 1........"+id;
    }
}