package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;

    //先来一个 根据关键字 查询 返回分页的结果即可
    @Override
    public PageResult search(RequestParams requestParams) throws Exception {

        //1.创建查询对象 searchrequest
        SearchRequest request = new SearchRequest("hotel");
        //2.构建查询的各种条件---->增加function-score
        buildBasicQuery(requestParams,request);

        //3.设置分页条件
        Integer page = requestParams.getPage();
        Integer size = requestParams.getSize();

        request.source()
                .from((page - 1) * size).size(size);


        //5.设置 排序的条件（特殊：地理位置坐标的排序 我附近的酒店：以我的GPS定位 升序排序 ）
        if(!StringUtils.isEmpty(requestParams.getLocation())) {
            request.source().sort(
                    SortBuilders.geoDistanceSort("location", new GeoPoint(requestParams.getLocation()))
                            .unit(DistanceUnit.KILOMETERS)
                            .order(SortOrder.ASC)
            );
        }


        //6.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //7.结果处理
        return handleResponse(response);
    }

    private void buildBasicQuery(RequestParams params, SearchRequest request) {
        // 1.构建BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 2.关键字搜索
        String key = params.getKey();
        if (key == null || "".equals(key)) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        }
        // 3.城市条件
        if (params.getCity() != null && !params.getCity().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        // 4.品牌条件
        if (params.getBrand() != null && !params.getBrand().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        // 5.星级条件
        if (params.getStarName() != null && !params.getStarName().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        // 6.价格
        if (params.getMinPrice() != null && params.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .gte(params.getMinPrice())
                    .lte(params.getMaxPrice())
            );
        }
        // 7.放入source

        FunctionScoreQueryBuilder functionScoreQueryBuilder =
                QueryBuilders
                        .functionScoreQuery(
                                boolQuery,
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                     new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                             QueryBuilders.termQuery("isAD",true),
                                             ScoreFunctionBuilders.weightFactorFunction(10)
                                     )
                                }
                        ).boostMode(CombineFunction.MULTIPLY);

        request.source().query(functionScoreQueryBuilder);
    }





    private PageResult handleResponse(SearchResponse response) {
        // 4.解析响应
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;
        // 4.2.文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

            Object[] sortValues = hit.getSortValues();
            if(sortValues!=null&&sortValues.length>0) {
                hotelDoc.setDistance(sortValues[0]);
            }
            // 放入集合
            hotels.add(hotelDoc);
        }
        // 4.4.封装返回
        return new PageResult(total, hotels);
    }
}
