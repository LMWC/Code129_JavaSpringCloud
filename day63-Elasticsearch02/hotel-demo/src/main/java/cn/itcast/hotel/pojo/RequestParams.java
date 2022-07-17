package cn.itcast.hotel.pojo;

import lombok.Data;

@Data
public class RequestParams {
    private String key;//关键字
    private Integer page;
    private Integer size;
    private String sortBy;
    //参数 接受页面传递过来的点击到的城市名称 品牌名称 星级名称 价格区间

    private String city;
    private String brand;
    private String starName;
    private Integer minPrice;
    private Integer maxPrice;

    //增加一个地理位置的字段 来接受页面传递过来的值
    private String location;
}