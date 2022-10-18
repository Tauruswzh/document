package triz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("fourTestServiceImpl")
@Slf4j
public class AbstractFourServiceImpl extends AbstractTestServiceImpl {


    @Override
    public String insert() {
        return "4 AbstractFourServiceImpl insert.....";
    }
}