package com.hellozj.common.dto.crow;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExcelModelDto extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;


    @ColumnWidth(50)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "建设单位名称", index = 0)
    private String publisherName;


    @ColumnWidth(20)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "联系人", index = 1)
    private String contactsName;


    @ColumnWidth(20)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    @ExcelProperty(value = "联系人号码", index = 2)
    private String contactsPhone;


    @ExcelIgnore
    private Date createTime;

}
