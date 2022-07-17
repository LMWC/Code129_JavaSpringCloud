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
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class HotelDocumentTest {

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
}
