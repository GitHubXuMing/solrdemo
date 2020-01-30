package com.spring.boot.solrdemo;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Map;
@SpringBootTest
public class SolrTest {
    public static final String SOLR_URL = "http://localhost:8983/solr/city_core";
    //添加数据
    @Test
    public void addDocument()throws Exception{
        HttpSolrClient client = new HttpSolrClient.Builder().withBaseSolrUrl(SOLR_URL).build();
        SolrInputDocument document = new SolrInputDocument();
        document.addField("cid",21);
        document.addField("city","大连市");
        document.addField("pid",8);
        client.add(document);
        client.commit();
    }

    //根据ID删除数据
    @Test
    public void deleteDocumentById()throws Exception{
        HttpSolrClient client = new HttpSolrClient.Builder().withBaseSolrUrl(SOLR_URL).build();
        client.deleteById("386ffbc0-da2d-4c16-ba55-2ee51f44c348");
        client.commit();
    }
    //根据查询条件删除数据
    @Test
    public void deleteDocumentByQuery()throws Exception{
        HttpSolrClient client = new HttpSolrClient.Builder().withBaseSolrUrl(SOLR_URL).build();
        client.deleteByQuery("city:大连*");
        client.commit();
    }

    //solr没有update方法，如果出现id一致情况，则delete原有数据，add新数据

    //查询所有
    @Test
    public void findAll()throws Exception{
        HttpSolrClient client = new HttpSolrClient.Builder().withBaseSolrUrl(SOLR_URL).build();
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");//设置查询条件，查询所有
        QueryResponse queryResponse = client.query(query);
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        System.out.println("查询出的结果数量："+solrDocumentList.getNumFound());
        for(SolrDocument solrDocument:solrDocumentList){
            System.out.println("cid:"+solrDocument.get("cid"));
            System.out.println("city:"+solrDocument.get("city"));
            System.out.println("pid:"+solrDocument.get("pid"));
            System.out.println("*************************************");}
        //第二种方式：直接获取List集合
        //List<City> cityList = client.query(query).getBeans(City.class);
        //cityList.forEach(System.out::println);
    }
    //复杂查询
    @Test
    public void findByXY()throws Exception{
        HttpSolrClient client = new HttpSolrClient.Builder().withBaseSolrUrl(SOLR_URL).build();
        SolrQuery query = new SolrQuery();
        //设置默认搜索域
        query.set("df","city");
        //设置查询条件，根据默认搜索域city，相当于city:*直辖市
        query.setQuery("*直辖市");
        //设置排序  将cid进行倒序排列
        query.setSort("cid", SolrQuery.ORDER.desc);
        //设置过滤查询，在查询结果的基础上，进行过滤 cid在1-5之间
        query.setFilterQueries("cid:[1 TO 5]");
        //分页处理 首页 4条数据
        query.setStart(0);
        query.setRows(4);
        //设置结果域中的字段列表
        query.setFields("id,cid,city");
        //根据复杂执行条件，执行查询
        QueryResponse queryResponse = client.query(query);
        //取得查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        //显示信息
        System.out.println("搜索到的数据数量是："+solrDocumentList.getNumFound());
        for(SolrDocument solrDocument:solrDocumentList){
            System.out.println("id --> "+solrDocument.get("id"));
            System.out.println("cid --> "+solrDocument.get("cid"));
            System.out.println("city --> "+solrDocument.get("city"));
            System.out.println("***********************************************");
        }
    }

    //高亮查询
    @Test
    public void findByHighLighting()throws Exception{
        HttpSolrClient client = new HttpSolrClient.Builder().withBaseSolrUrl(SOLR_URL).build();
        SolrQuery query = new SolrQuery();
        query.setQuery("city:*直辖市");
        //设置结果域中的字段列表
        query.setFields("id,cid,city");
        //高亮显示
        query.setHighlight(true);
        query.addHighlightField("city");
        query.setHighlightSimplePre("<em color='red'>");
        query.setHighlightSimplePost("</em>");
        //根据复杂执行条件，执行查询
        QueryResponse queryResponse = client.query(query);
        //取得查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        //显示高亮信息
        Map<String, Map<String,List<String>>> highLightings = queryResponse.getHighlighting();
        // 8bd78df1-1e56-4f21-afaf-45c6acdb58f7={city=[广州<em color='red'>直辖市</em>]}......
        System.out.println(highLightings);
        /*遍历高亮信息：
         *广州<em color='red'>直辖市</em>
         *重庆<em color='red'>直辖市</em>
         *上海<em color='red'>直辖市</em>
         *天津<em color='red'>直辖市</em>
         */
        for(SolrDocument sd:solrDocumentList) {
            List<String> result = highLightings.get(sd.get("id")).get("city");
            if(result != null){ System.out.println(result.get(0)); }
        }
    }
}
