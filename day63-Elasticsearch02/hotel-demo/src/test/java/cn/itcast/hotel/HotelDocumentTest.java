package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
class    HotelDocumentTest {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private IHotelService hotelService;

    // 添加一个文档
    @Test
    void testAddDocument() throws IOException {

        //0.先从数据库中模拟获取一行（根据iD获取） POJO--》数据库相关的
        Hotel hotel = hotelService.getById(36934);
        //1.将数据库相关的POJO 转成 ES相关的POJO 再使用fastjson将ES相关的POJO转成json字符串
        HotelDoc hotelDoc = new HotelDoc(hotel);
        String jsonString = JSON.toJSONString(hotelDoc);
        //2.创建一个indexRequest对象
        //参数：指定索引名称
        IndexRequest request = new IndexRequest("hotel");
        //文档的唯一标识 和 字段(field) id是不一样的
        request.id(hotelDoc.getId()+"");
        //3.设置resource(json)
        request.source(jsonString, XContentType.JSON);
        //4.执行动作 创建文档
        client.index(request,RequestOptions.DEFAULT);


    }

    @Test
    void testGetDocumentById() throws IOException {
        // 1.准备Request      // GET /hotel/_doc/{id}
        GetRequest request = new GetRequest("hotel", "36934");
        // 2.发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);

        //获取source的值（就是酒店数据的JSON）
        String sourceAsString = response.getSourceAsString();

        System.out.println(sourceAsString);
    }

    @Test
    void testDeleteDocumentById() throws IOException {
        // 1.准备Request      // DELETE /hotel/_doc/{id}
        DeleteRequest request = new DeleteRequest("hotel", "36934");
        // 2.发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testUpdateById() throws IOException {
        // 1.准备Request
        UpdateRequest request = new UpdateRequest("hotel", "36934");
        // 2.准备参数
        request.doc(
                "price", 870, //修改价格
                "score",38//修改评分
        );
        // 3.发送请求
        client.update(request, RequestOptions.DEFAULT);
    }


    //批量添加数据到es中

    //批量添加数据到ES中   bulkreuqest 每一个批次 尽量控制在15MB左右
    @Test
    void testBulkRequest() throws IOException {


        long start = System.currentTimeMillis();

        //1.查询数据库所有的数据行
        List<Hotel> list = hotelService.list();

        BulkRequest request = new BulkRequest();
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);//立刻
        //2.循环遍历 执行添加即可
        for (Hotel hotel : list) {
            //0.先从数据库中模拟获取一行（根据iD获取） POJO--》数据库相关的

            //1.将数据库相关的POJO 转成 ES相关的POJO 再使用fastjson将ES相关的POJO转成json字符串
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String jsonString = JSON.toJSONString(hotelDoc);
            //2.创建一个indexRequest对象
            //参数：指定索引名称
            IndexRequest indexRequest = new IndexRequest("hotel");
            //文档的唯一标识 和 字段(field) id是不一样的
            indexRequest.id(hotelDoc.getId()+"");
            //3.设置resource(json)
            indexRequest.source(jsonString, XContentType.JSON);

           // client.index(request,RequestOptions.DEFAULT);

            request.add(indexRequest);

        }
        //4.执行动作 批量创建文档
        client.bulk(request,RequestOptions.DEFAULT);


        long end = System.currentTimeMillis();

        System.out.println("时间："+(end-start));


    }

    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://localhost:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        client.close();
    }









    //=======================================================完美分隔============================day02


    @Test
    public void matchAll() throws Exception{

        //1.创建查询对象 指定从哪一个索引中搜索
        SearchRequest request = new SearchRequest("hotel");

        // request.source()  =   new SearchSourceBuilder();
        //2 构建各种各样查询条件(查询所有) 设置到查询对象中
        request.source().query( QueryBuilders.matchAllQuery());

        //3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4.获取结果集
        handleResponse(response);

    }

    /*@Test
    public void matchAll1() throws Exception{
        //1.创建查询对象 指定从哪一个索引中搜索
        //SearchRequest request = new SearchRequest();
        //request.indices("hotel");
        SearchRequest request = new SearchRequest("hotel");

        // SearchSourceBuilder builder = new SearchSourceBuilder();
        //2 构建各种各样查询条件(查询所有) 设置到查询对象中
        *//*builder.query(
                QueryBuilders.matchAllQuery()
        );*//*
        request.source().query( QueryBuilders.matchAllQuery());

        //3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4.获取结果集
        long value = response.getHits().getTotalHits().value;
        System.out.println("总记录数："+value);
        SearchHit[] hits = response.getHits().getHits();

        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }*/

    @Test
    public void matchQuery() throws Exception{

        //1.创建查询对象 指定从哪一个索引中搜索
        SearchRequest request = new SearchRequest("hotel");

        //2 构建各种各样查询条件 设置到查询对象中
        //匹配查询 1指定要查询的字段 2 指定要查询的文本
        request.source().query( QueryBuilders.matchQuery("name","虹桥"));

        //3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);

    }

    /*@Test
    public void matchQuery1() throws Exception{

        //1.创建查询对象 指定从哪一个索引中搜索
        SearchRequest request = new SearchRequest("hotel");

        //2 构建各种各样查询条件 设置到查询对象中
        //匹配查询 1指定要查询的字段 2 指定要查询的文本
        request.source().query( QueryBuilders.matchQuery("name","虹桥"));

        //3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4.获取结果集
        long value = response.getHits().getTotalHits().value;
        System.out.println("总记录数：" +value);
        SearchHit[] hits = response.getHits().getHits();

        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }*/

    //词条查询  不分词 整体进行匹配查询 比如 品牌查询 城市的查询
    @Test
    public void termquery() throws Exception{
        //1.创建查询对象 指定从哪一个索引中搜索
        SearchRequest request = new SearchRequest("hotel");

        //2 构建各种各样查询条件 设置到查询对象中
        //term查询 1指定要查询的字段 2 指定要查询的文本
        request.source().query( QueryBuilders.termQuery("city","深圳"));

        //3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    public void rangequery() throws Exception{
        //1.创建查询对象 指定从哪一个索引中搜索
        SearchRequest request = new SearchRequest("hotel");

        //2 构建各种各样查询条件 设置到查询对象中
        //range查询
        request.source().query( QueryBuilders.rangeQuery("price").gte(100).lte(200));

        //3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    //需求： 查询城市在深圳的 价格在100-200酒店

    @Test
    public void boolquery() throws Exception{
        //1.创建查询对象 指定从哪一个索引中搜索
        SearchRequest request = new SearchRequest("hotel");

        //2 构建查询条件（bool查询条件）
        //2.0 创建bool查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //2.1 设置子查询条件 1
        TermQueryBuilder condition1 = QueryBuilders.termQuery("city", "深圳");
        //2.2 设置子查询条件 2
        RangeQueryBuilder condition2 = QueryBuilders.rangeQuery("price").gte(100).lte(200);
        //2.3 组合他们（模式： must must_not should,filter）
        boolQueryBuilder.must(condition1).must(condition2);

        request.source().query(boolQueryBuilder);

        //3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }


    @Test
    public void pageandsort() throws Exception{
        //1.创建查询对象 指定从哪一个索引中搜索
        SearchRequest request = new SearchRequest("hotel");

        //2 构建查询条件
        request.source().query(QueryBuilders.matchAllQuery());

        //3.设置分页条件 设置 排序的条件
        request.source().from(0).size(10);//0 : page-1 * rows  size: rows
        request.source().sort("price", SortOrder.DESC);

        //4.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    public void highLignt() throws Exception{
        //1.创建查询对象 指定从哪一个索引中搜索
        SearchRequest request = new SearchRequest("hotel");

        //2 构建查询条件 匹配查询
        request.source().query(QueryBuilders.matchQuery("name","虹桥"));

        //3.设置高亮条件 1 设置高亮的字段 2 设置前缀和后缀 3 设置是否必须匹配
        request.source()
                .highlighter(new HighlightBuilder()
                        .field("name")
                        .requireFieldMatch(false)
                        .preTags("<em style=\"color:red\">")
                        .postTags("</em>"));

        //4.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }


    private void handleResponse(SearchResponse response) {
        // 4.解析响应
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 4.2.文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        for (SearchHit hit : hits) {
            // 获取文档source  没有高亮的
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            System.out.println("高亮获取之前的数据："+hotelDoc.getName());
            //获取高亮的数据进行替换
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null ) {
                HighlightField highlightField = highlightFields.get("name");
                if(highlightField!=null) {
                    Text[] fragments = highlightField.getFragments();
                    if(fragments!=null && fragments.length>0) {
                        //高亮的数据
                        String gaoliangstr = fragments[0].string();
                        hotelDoc.setName(gaoliangstr);
                    }
                }
            }

            System.out.println("高亮获取之后的数据："+hotelDoc.getName());

        }
    }

    /*private void handleResponse1(SearchResponse response) {
        // 4.解析响应
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 4.2.文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            //HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            //System.out.println("hotelDoc = " + hotelDoc);
            System.out.println(json);
        }
    }*/








}
