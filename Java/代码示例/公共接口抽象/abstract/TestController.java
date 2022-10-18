package triz.controller;

import io.swagger.annotations.Api;
import jnpf.base.ActionResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import triz.constant.ActionConstant;



@Api(tags = "抽象测试", value = "AbstractTestController")
@RestController
@Validated
@RequestMapping("/v1/test")
public class TestController {

    @GetMapping("/insert")
    public ActionResult<String> getDetails(@RequestParam("str")String str) {
        String insert = AbstractHandler.getInstance(str).insert();
        return ActionResult.success(ActionConstant.COMMON_OPERATION_SUCCESS, insert);
    }

    @GetMapping("/update")
    public ActionResult<String> update(@RequestParam("str")String str, @RequestParam("id")String id) {
        String update = AbstractHandler.getInstance(str).update(id);
        return ActionResult.success(ActionConstant.COMMON_OPERATION_SUCCESS, update);
    }
}
