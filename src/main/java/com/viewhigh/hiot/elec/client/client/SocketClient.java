package com.viewhigh.hiot.elec.client.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SocketClient {

    /**
     * 初始化一个客户端应用
     */
    public void initSocketClient() throws UnknownHostException, InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bs = new Bootstrap();
            bs.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_SNDBUF, 128) //设置发送缓冲区
                    .option(ChannelOption.SO_RCVBUF, 256) //设置接收缓冲区
                    .option(ChannelOption.SO_KEEPALIVE, true) //保持连接
//                    .remoteAddress(new InetSocketAddress(InetAddress.getLocalHost(), 55884)) // todo ip和端口绑定可以在connect(host,post)中
                    .handler(new SClientInitializer());
            ChannelFuture cf = bs.connect(InetAddress.getLocalHost(), 55884).sync(); // 异步连接服务器
//            cf.addListener(new ConnectionListener()); // 启动失败监听器
            cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
            System.out.println("连接已关闭.."); // 关闭完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully().sync(); // 释放线程池资源
        }

    }

    public static  void main(String[] strings) throws Exception {
        new SocketClient().initSocketClient();
    }
}
