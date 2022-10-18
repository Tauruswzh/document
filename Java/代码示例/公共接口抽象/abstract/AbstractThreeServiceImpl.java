package triz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("threeTestServiceImpl")
@Slf4j
public class AbstractThreeServiceImpl extends AbstractTestServiceImpl {


    @Override
    public String insert() {
        return "3 AbstractThreeServiceImpl insert.....";
    }
}