package com.ttdys.common.xls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 模板Excel属性描述
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TemplateDescriptor {
    /** column index */
    private int index;
    /** 属性名 */
    private String fieldName;
    /** 格式 */
    private String format;

}
