package com.viewhigh.hiot.elec.client.client;

import com.viewhigh.hiot.elec.client.DemoClient;
import com.viewhigh.hiot.elec.client.support.utils.ByteET;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SocketClientHandler extends ChannelInboundHandlerAdapter{
    private int readerIdleTime = 1;
    private int writerIdleTime = 1;
    private int allIdleTime = 1;
    private SocketClient socketClient = new SocketClient();
    private static Scanner inputScanner = new Scanner(System.in);
    private static int num = 0;
    private static byte[] bytes = {0x10,0x11,0x12,0x13,0x14,0x15,0x16};
    /**
     * 向服务端发送数据，发送string
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端与服务端通道-开启：" + ctx.channel().localAddress() + "channelActive");
        System.out.println("服务端连接成功..."); // 连接完成
        byte[] msg = DemoClient.createMsg((byte)0x01); // 可以在这里进行鉴权，连接简历后，会回调这个方法，自动方法鉴权数据

        System.out.println(ByteET.bytes2HexString(msg));
        ctx.writeAndFlush(Unpooled.copiedBuffer(msg));
    }

    /**
     * channelInactive
     * channel 通道 Inactive 不活跃的
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     *
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端与服务端通道-关闭：" + ctx.channel().localAddress() + "channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("读取通道信息.."+ ctx.channel().id());
        byte[] byteArr;
        ByteBuf byteBuf = (ByteBuf)msg;
        byteArr = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(byteArr);

        System.out.println("客户端接收到服务端信息：" + ByteET.bytes2HexString(byteArr));// 这个转换 可以看到返回的可读信息，但会部分出现乱码

        if (num >= bytes.length - 1){
           num = 0;
        }
        System.out.println(bytes[num]);
        byte[]  b = DemoClient.createMsg(bytes[num]);
        num ++;
        System.out.println(num);

        System.out.println(ByteET.bytes2HexString(b));
        ctx.writeAndFlush(Unpooled.copiedBuffer(b));
        Thread.sleep(5000); // 我用来模拟服务端心跳使用
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 如果不在这个方法里实现具体的异常日志打印，就不要实现这个方法，否则会覆盖详细的堆栈异常信息
        cause.printStackTrace();
        ctx.close();
        System.out.println("异常退出:" + cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String  id = "";
        if (evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    System.out.println("通道0x" + id + "读空闲第" + readerIdleTime ++ + "心跳");
                    break;
                case WRITER_IDLE:
                    System.out.println("通道0x" + id + "写空闲第" + writerIdleTime ++ + "心跳");
                    break;
                case ALL_IDLE:
                    System.out.println("通道0x" + id + "读写空闲第" + allIdleTime ++ + "心跳");
                    break;
                default:
                    throw new IOException("未知的IdleStateEvent状态");
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
        if (readerIdleTime > 3){
            System.out.println(" [client]读空闲超过3次，关闭连接：" + id);
            ctx.close();
        }
        if (readerIdleTime > 3){
            System.out.println(" [client]写空闲超过3次，关闭连接：" + id);
            ctx.close();
        }
        if (readerIdleTime > 3){
            System.out.println(" [client]读写空闲超过3次，关闭连接：" + id);
            ctx.close();
        }
    }
}
