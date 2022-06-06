package com.wsn.powerstrip.device.POJO.DO.setting;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    private String id;
    @NotBlank(message = "定时设置名称不能为空")
    @Schema(description = "定时设置名称, 用于模糊查找")
    private String name;

    private List<Setting> settings;

    private String detail;

    private Date createTime;

    private Date updateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "设备定时开关实体,其中startTime和endTIme应至少存在一个,daysOfWeek不为空")
    static public class Setting{
        @Schema(description = "打开时间(北京时间,非UTC时间)")
        @JsonFormat(timezone = "Asia/Shanghai")
        private LocalTime onTime;
        @JsonFormat(timezone = "Asia/Shanghai")
        @Schema(description = "关闭时间(为北京时间,非UTC时间)")
        private LocalTime offTime;
        @NotEmpty(message = "执行星期不能为空")
        @Schema(description = "在每周的什么时候执行上述操作")
        private List<DayOfWeek> daysOfWeek;
    }
}
