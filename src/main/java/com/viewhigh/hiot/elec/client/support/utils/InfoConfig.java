package com.viewhigh.hiot.elec.client.support.utils;

/**
 * @EnumName InfoConfig
 * Description 定义服务端配置信息
 * @Author huyang
 * Date 2019/5/16 14:09
 **/
public enum InfoConfig {
    SERVER_PORT(55884), //"服务端端口号"
    HEART_TIME_READ(500), //"服务端心跳时间间隔"
    HEART_TIME_WRITE(0), //"服务端心跳时间间隔"
    HEART_TIME_ALL(0), //"服务端心跳时间间隔"
    HEART_FREQUENCY(3), //"心跳间隔次数"
    SO_BACKLOG(32), //"设置tcp缓冲区"
    SO_SNDBUF(128), //"设置发送缓冲区"
    SO_RCVBUF(256), //"设置接收缓冲区"
    // LengthFieldBasedFrameDecoder编码解码的配置
    DECODE_MFL(1024), // maxFrameLength
    DECODE_LFO(0), // lengthFieldOffset
    DECODE_LFL(4), // lengthFieldLength
    DECODE_LAJ(0), // lengthAdjustment
    DECODE_IBT(4); // initialBytesToStrip

    public int getValue() {
        return value;
    }

    public void setValue(int config) {
        this.value = value;
    }

    int value;
    InfoConfig(int value){
        this.value = value;
    }
}
