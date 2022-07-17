package cn.itcast.hotel.controller;

import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private IHotelService hotelService;

    @PostMapping("/list")
    public PageResult search(@RequestBody RequestParams requestParams) throws Exception{
        PageResult result =  hotelService.search(requestParams);
        return result;
    }

    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody RequestParams requestParams) throws Exception{
        //接受页面传递过来的参数 执行聚合查询 返回结果
        return hotelService.getFilters(requestParams);
    }

    @GetMapping("/suggestion")
    public List<String> getSuggestions(@RequestParam("key") String prefix) throws Exception{
        return hotelService.getSuggestions(prefix);
    }
}
