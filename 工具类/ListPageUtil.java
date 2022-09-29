package com.hellozj.common.util;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
* 文件名: ListPageUtil.java
* 作者: xiahao
* 时间: 2020/5/28 18:42
* 描述: List分页工具类
*/
@Data
public class ListPageUtil<T> {
    private Integer currentPage;//当前页
    private int pageSize;//每页显示记录条数
    private int pages;//总页数
    private List<T> dataList;//每页显示的数据
    private int star;//开始数据
    private int total;//总数目
    private int isLastPage;//是否是最后一页 0:不是 1:是

    /**
    * 方法名:
    * 作者/时间: xiahao-2020/5/28
    * 描述: 分页
    * 参数: getpgindex 第几页  PageSize	一页有多少条数据
    * 返回:
    */
    public static <T> ListPageUtil<T> Paging(int getpgindex,int PageSize,List<T> list){
        ListPageUtil page = new ListPageUtil();
        if (list != null && !list.isEmpty()){

            //初始页
            page.setCurrentPage(getpgindex);
            //设置每页数据
            page.setPageSize(PageSize);

            //每页的开始数
            page.setStar((page.getCurrentPage() - 1) * page.getPageSize());

            //list的大小
            int count = list.size();
            page.setTotal(count);

            //设置总页数
            //防止 by zero 异常
            if (page.getPageSize() != 0){
                page.setPages(count % page.getPageSize() == 0 ? count / page.getPageSize() : count / page.getPageSize() + 1);
            }

            //判断是否是最后一页
            if (page.getCurrentPage() < page.getPages()){
                page.setIsLastPage(0);
            } else {
                page.setIsLastPage(1);
            }

            //对list进行截取
            try {
                page.setDataList(list.subList(page.getStar(),count-page.getStar()>page.getPageSize()?page.getStar()+page.getPageSize():count));
            } catch (Exception e) {
                page.setDataList(new ArrayList<>());
            }
        }
        return page;
    }
}
