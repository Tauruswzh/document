pagehelper分页


###1.添加依赖
```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.13</version>
</dependency>
```

###2.参数包装类
```java
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件名: PageDto.java
 * 描述: 分页查询参数
 */
@ApiModel(description = "分页查询参数")
public class PageDto<T> implements Serializable {

    /**
     * 页号
     */
    @Min(value = 1, message = "页码必须大于等于1")
    @ApiModelProperty(value = "页号", required = true, example = "1")
    private int pageNum;

    /**
     * 页大小
     */
    @Min(value = 1, message = "页大小必须大于等于1")
    @ApiModelProperty(value = "页大小", required = true, example = "10")
    private int pageSize;

    /**
     * 查询条件
     */
    @Valid
    @NotNull
    @ApiModelProperty(value = "查询条件")
    private T data;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
```
###3.controller
```java
public class CompanyManageController {

    @Autowired
    private CompanyManageFacadeService companyManageFacadeService;

    /**
    * 描述: 订单列表
    */
    @PostMapping("/orderList")
    @PreAuthorize("hasAuthority('company:orderlist')")
    public ResultResponse<PageInfo<ManageOrderListVo>> manageOrderList(@RequestBody PageDto<ManageOrderListQueryDto> page) {
        UserLoginUserDetail currentUser = OrderBaseCommon.getUserLoginUserDetail();
        return ResultResponse.success(companyManageFacadeService.manageOrderList(page,currentUser));
    }
}
```
###4.实现类
```java
public class OrderCompanyClient implements IOrderCompanyClient {
    /**
     * 方法名:  manageOrderList
     */
    @PostMapping("/manageOrderList")
    @Override
    public PageInfo<ManageOrderListVo> manageOrderList(PageDto<ManageOrderListQueryDto> page) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize());
        List<ManageOrderListVo> list = orderCompanyService.manageOrderList(page.getData());
        return new PageInfo<>(list);
    }
}
```

PageInfo 是 com.github.pagehelper下的类
[注意]
PageHelper.startPage(page.getPageNum(),page.getPageSize());
必须在数据库操作的上面，直接不能掺杂其他代码，否则分页不生效