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
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;

    //???????????? ??????????????? ?????? ???????????????????????????
    @Override
    public PageResult search(RequestParams requestParams) throws Exception {

        //1.?????????????????? searchrequest
        SearchRequest request = new SearchRequest("hotel");
        //2.???????????????????????????---->??????function-score
        buildBasicQuery(requestParams,request);

        //3.??????????????????
        Integer page = requestParams.getPage();
        Integer size = requestParams.getSize();

        request.source()
                .from((page - 1) * size).size(size);


        //5.?????? ?????????????????????????????????????????????????????? ??????????????????????????????GPS?????? ???????????? ???
        if(!StringUtils.isEmpty(requestParams.getLocation())) {
            request.source().sort(
                    SortBuilders.geoDistanceSort("location", new GeoPoint(requestParams.getLocation()))
                            .unit(DistanceUnit.KILOMETERS)
                            .order(SortOrder.ASC)
            );
        }


        //6.????????????
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //7.????????????
        return handleResponse(response);
    }

    @Override
    public Map<String, List<String>> getFilters(RequestParams requestParams) throws Exception{
        //1.??????????????????
        SearchRequest request = new SearchRequest("hotel");
        //2.?????????????????????
        buildBasicQuery(requestParams,request);
        //3.??????3?????????????????? ?????? ??????  ??????
        request.source().size(10);
        request.source()
                .aggregation(AggregationBuilders.terms("cityAgg").field("city").size(100));
        request.source()
                .aggregation(AggregationBuilders.terms("starAgg").field("starName").size(100));
        request.source()
                .aggregation(AggregationBuilders.terms("brandAgg").field("brand").size(100));
        //4.????????????
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //5.????????????????????? ?????? ??????map
        Map<String,List<String>> map = new HashMap<>();
        Aggregations aggregations = response.getAggregations();

        Terms cityAgg = aggregations.get("cityAgg");
        List<? extends Terms.Bucket> buckets = cityAgg.getBuckets();
        List<String> cityList = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            cityList.add(bucket.getKeyAsString());
        }
        map.put("city",cityList);


        Terms starAgg = aggregations.get("starAgg");
        List<? extends Terms.Bucket> bucketsstar = starAgg.getBuckets();
        List<String> starList = new ArrayList<>();
        for (Terms.Bucket bucket : bucketsstar) {
            starList.add(bucket.getKeyAsString());
        }
        map.put("starName",starList);

        Terms brandAgg = aggregations.get("brandAgg");
        List<? extends Terms.Bucket> bucketsbrand = brandAgg.getBuckets();
        List<String> brandList = new ArrayList<>();
        for (Terms.Bucket bucket : bucketsbrand) {
            brandList.add(bucket.getKeyAsString());
        }
        map.put("brand",brandList);


        return map;
    }

    @Override
    public List<String> getSuggestions(String prefix) throws Exception {
        SearchRequest request = new SearchRequest("hotel");
        request.source()
                .suggest(
                     new SuggestBuilder()
                             .addSuggestion(
                                     //??????
                                     "suggestions",
                                     //?????? ???????????????
                                     SuggestBuilders.completionSuggestion("suggestion")//??????????????????????????????
                                             .skipDuplicates(true)//???????????????
                                             .size(100)//size
                                             .prefix(prefix)//text

                             )
                );
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        Suggest suggest = response.getSuggest();
        CompletionSuggestion suggestions
                = suggest.getSuggestion("suggestions");

        List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
        //return options.stream().map(p -> p.getText().string()).collect(Collectors.toList());
        List<String> resultList = new ArrayList<>();
        for (CompletionSuggestion.Entry.Option option : options) {
            resultList.add(option.getText().string());

        }
        return resultList;
    }

    private void buildBasicQuery(RequestParams params, SearchRequest request) {
        // 1.??????BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 2.???????????????
        String key = params.getKey();
        if (key == null || "".equals(key)) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        }
        // 3.????????????
        if (params.getCity() != null && !params.getCity().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        // 4.????????????
        if (params.getBrand() != null && !params.getBrand().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        // 5.????????????
        if (params.getStarName() != null && !params.getStarName().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        // 6.??????
        if (params.getMinPrice() != null && params.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .gte(params.getMinPrice())
                    .lte(params.getMaxPrice())
            );
        }
        // 7.??????source

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
        // 4.????????????
        SearchHits searchHits = response.getHits();
        // 4.1.???????????????
        long total = searchHits.getTotalHits().value;
        // 4.2.????????????
        SearchHit[] hits = searchHits.getHits();
        // 4.3.??????
        List<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits) {
            // ????????????source
            String json = hit.getSourceAsString();
            // ????????????
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

            Object[] sortValues = hit.getSortValues();
            if(sortValues!=null&&sortValues.length>0) {
                hotelDoc.setDistance(sortValues[0]);
            }
            // ????????????
            hotels.add(hotelDoc);
        }
        // 4.4.????????????
        return new PageResult(total, hotels);
    }
}
