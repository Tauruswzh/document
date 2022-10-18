package witpdp.controller;

import io.swagger.annotations.Api;
import jnpf.base.ActionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import witpdp.factory.PatternHandlerFactory;


@Api(tags = "测试数据", value = "test")
@Slf4j
@RestController
@RequestMapping("/v1/test")
public class TestController {

    @Autowired
    private PatternHandlerFactory patternHandlerFactory;

    @GetMapping("/update")
    public ActionResult<String> upload() {
        String upload = patternHandlerFactory.getHandler("component").update();
        return ActionResult.success("更新成功",upload);
    }

    @GetMapping("/insert")
    public ActionResult<String> insert() {
        String insert = patternHandlerFactory.getHandler("component").insert("one");
        return ActionResult.success("插入成功",insert);
    }

}
