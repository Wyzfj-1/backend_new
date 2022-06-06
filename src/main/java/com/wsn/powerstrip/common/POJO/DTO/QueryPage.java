package com.wsn.powerstrip.common.POJO.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 4:32 PM 5/7/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Data
@NoArgsConstructor
public class QueryPage implements Serializable {


    /**
     * 查询页数
     */
    @Min(1)
    @NotNull
    private int page;
    /**
     * 每页限制
     */
    @Min(1)
    @NotNull
    private int limit;

    /**
     * 之所以手写一个构造函数而不用lombok是因为Lombok不会写明传参顺序,两个int可能传反
     * @param page 查询的页数,从1开始
     * @param limit 每页的长度
     */
    public QueryPage(int page, int limit) {
        this.page = page;
        this.limit = limit;
    }


    public int getPageForMongoDB() {
        //这个方法只会被mongodb用到, mongodb中,第一页是的index是0
        return page - 1;
    }
}
