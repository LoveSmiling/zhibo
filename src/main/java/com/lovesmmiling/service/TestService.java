package com.lovesmmiling.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lovesmmiling.entity.Tv;
import com.lovesmmiling.entity.Zhubo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;

@Service
@Slf4j
public class TestService {

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void getFile() {
        String url = "http://api.hclyz.com:81/mf/json.txt";
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
        String body = exchange.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONArray pingtai = jsonObject.getJSONArray("pingtai");
        List<Tv> tvs = JSONArray.parseArray(pingtai.toJSONString(), Tv.class);

        String getUrl = "";
        for (Tv tv : tvs) {
            getUrl = "http://api.hclyz.com:81/mf/" + tv.getAddress();
            ResponseEntity<String> ex = restTemplate.exchange(getUrl, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
            String bodys = ex.getBody();
            JSONObject jsonObjects = JSONObject.parseObject(bodys);
            JSONArray zhubo = jsonObjects.getJSONArray("zhubo");
            List<Zhubo> zhubos = JSONArray.parseArray(zhubo.toJSONString(), Zhubo.class);

            StringBuilder stringBuilder = new StringBuilder();

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(tv.getAddress()));
                for (Zhubo zhubo1 : zhubos) {
                    stringBuilder.append(zhubo1.getAddress()+"\n");
                }
                out.write(stringBuilder.toString(),0,stringBuilder.toString().length());
                out.close();
                System.out.println("?????????????????????");
            } catch (IOException e) {
            }
        }
    }
}