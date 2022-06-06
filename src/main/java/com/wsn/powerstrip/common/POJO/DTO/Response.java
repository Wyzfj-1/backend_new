package com.wsn.powerstrip.common.POJO.DTO;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: wangzilinn@gmail.com
 * @Description: 这个类用作所有Restful请求的包装类
 * @Date: Created in 9:56 PM 5/6/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Builder
@ToString
@AllArgsConstructor //Build解决父类依赖
public class Response<T> implements Serializable {
    @Getter
    @Setter
    private Integer code = 200;

    @Getter
    @Setter
    private Object msg = "success";

    @Getter
    @Setter
    private T data;

    public Response() {
        super();
    }

    public Response(T data) {
        super();
        this.data = data;
    }

    public Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response(T data, String msg) {
        super();
        this.data = data;
        this.msg = msg;
    }


    /**
     * 网页请求当返回页时, 使用这个包装类
     * @param <T> 列表中内容的类型
     */
    @Data
    @NoArgsConstructor
    static public class Page<T> {
        private List<T> elements;
        private Integer currentNumber;//当前页面数据长度, 最大为页面大小
        private Long totalNumber;
        private Integer currentPage;
        private Integer totalPages;

        public Page(List<T> elements, QueryPage queryPage, long totalNumber) {
            this.elements = elements;
            this.currentNumber = elements.size();
            this.currentPage = queryPage.getPage();
            this.totalNumber = totalNumber;
            this.totalPages = (int) Math.ceil((double) totalNumber / queryPage.getLimit());
        }
    }
}
