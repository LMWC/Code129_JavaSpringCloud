package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.indices.CreateIndexRequest;

import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import cn.itcast.hotel.constants.HotelIndexConstants;

@SpringBootTest
class HotelIndexTest {

    private RestHighLevelClient client;

    //创建索引和映射
    @Test
    void testCreateIndex() throws IOException {


        //2.创建request对象
        //创建索引的请求对象 参数 指定索引的名称
        CreateIndexRequest request = new CreateIndexRequest("hotel");

        //3.设置resource(mapping.....)
        request.source(HotelIndexConstants.MAPPING_TEMPLATE, XContentType.JSON);
        //4.执行创建的动作
        //参数1 指定请求对象（封装了数据）
        //参数2 指定请求选项对象（一般使用默认的即可）
        client.indices().create(request, RequestOptions.DEFAULT);


    }

    @Test
    void testExistsIndex() throws IOException {
        // 1.准备Request
        GetIndexRequest request = new GetIndexRequest("hotel");
        // 3.发送请求
        boolean isExists = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(isExists ? "存在" : "不存在");
    }


    @Test
    void testDeleteIndex() throws IOException {
        // 1.准备Request
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");
        // 3.发送请求
        client.indices().delete(request, RequestOptions.DEFAULT);
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
