package com.viewhigh.hiot.elec.client;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.viewhigh.hiot.elec.client.client.SocketClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class})
public class HiotElecClientApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HiotElecClientApplication.class, args);
        new SocketClient().initSocketClient();
    }

}
