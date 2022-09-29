package com.xx.order.util;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 文件名: EasyExcelCellWriteHandler.java
 * 作者: xiahao
 * 时间: 2021/6/7 下午12:24
 * 描述: easyExcel自定义头属性
 */
public class EasyExcelCellWriteHandler implements SheetWriteHandler {
    private List<Map<Integer, String[]>> list = null;

    public EasyExcelCellWriteHandler(List<Map<Integer, String[]>> list){
        this.list = list;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        // 这里可以对cell进行任何操作
        DataValidationHelper helper = sheet.getDataValidationHelper();

        // k 为存在下拉数据集的单元格下表 v为下拉数据集
        if(!CollectionUtils.isEmpty(list)){
            list.forEach((item) -> {
                // k 为存在下拉数据集的单元格下表 v为下拉数据集
                item.forEach((k, v) -> {
                    // 下拉列表约束数据
                    DataValidationConstraint constraint = helper.createExplicitListConstraint(v);
                    // 设置下拉单元格的首行 末行 首列 末列
                    CellRangeAddressList rangeList = new CellRangeAddressList(1, 65536, k, k);
                    // 设置约束
                    DataValidation validation = helper.createValidation(constraint, rangeList);
                    // 阻止输入非下拉选项的值
                    validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                    validation.setShowErrorBox(true);
                    validation.setSuppressDropDownArrow(true);
                    validation.createErrorBox("提示","此值与单元格定义格式不一致");
                    sheet.addValidationData(validation);
                });
            });
        }
    }
}
