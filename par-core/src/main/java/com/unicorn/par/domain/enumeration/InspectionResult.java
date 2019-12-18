package com.unicorn.par.domain.enumeration;

public class InspectionResult {

    public static final Integer NotYet = null;          // 时间未到

    public static final Integer Nonexistent = -2;       // 不需要巡检（系统在当前日期还未开始参与巡检）

    public static final Integer Unnecessary = -1;       // 不需要巡检（休息日）

    public static final Integer No = 0;                 // 未巡检

    public static final Integer Good = 1;               // 已巡检，功能正常

    public static final Integer Bad = 2;                // 已巡检，发现异常

    public static final Integer GoodAndDelay = 3;       // 延时巡检，功能正常

    public static final Integer BadAndDelay = 4;        // 延时巡检，发现异常

    public static final Integer ExternalCauses = 5;     // 已巡检，发现异常（外部原因）
}
