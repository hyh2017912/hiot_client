package com.viewhigh.hiot.elec.client.support.utils;

import java.util.Date;

/**
 * 电流监测设备协议处理
 * @author sunhl
 *
 */
public class ProtocolElec {
	
	/* ********************************************************
	   	协议结构
	 	*******
	  	起始位置	说明	数值类型	字节长度	备注
		0	起始符	byte	2	固定字符0x2323( ASCII字符“##”)
		2	流水号	long	8	无符号数。每天由0开始计数，每条消息+1，重发消息保留原数值，达最大值后从0循环。
		10	加密类型	byte	1	加密仅针对数据区域	0x00：不加密
		11	消息标识	byte	1	详见“消息标识”定义
		12	数据长度	short	2	数据区域长度n（加密后长度）
		14	数据区	byte[n]	n	数据内容，详见各接口“数据区域”定义
		14+n	校验位	word	2	crc16校验码，用于数据一致验证。
		
		****************************************************************
		
		0x00	心跳	上行	若无数据时，保持每10s发一次心跳。3个心跳周期没有响应，则断开链接。
		0x01	身份鉴权	上行	发送身份信息，鉴权通过后该链路数据才会被服务端接收。
		0x10	采集频发数据	上行	电压、电流、功率等秒级采集上报的数据
		0x11	设备状态更新	上行	设备状态变更时触发
		0x12	意外断电数据	上行	发生意外断电后，上报意外断电次数。
		0x13	电流超限	上行	发送超限电流及电流波形数据
		0x14	电压超限	上行	发送超限电压及电压波形数据
		0x15	统计数据	上行	每天一次，累计电量、累计使用时长及明细
		0x16	配置数据	上行	设备启动时发送，超限配额、基站标识
					
		0x80	通用应答	下行	无数据区，仅作为应答判断。
		使用请求消息的流水号
		0x81	身份鉴权结果	下行	身份鉴权结果。鉴权成功后才接受其他数据消息。
		使用请求消息的流水号。

	 **********************************************************/

	
	public static final int PROTOCOL_LEN = 14;
	public static final int PROTOCOL_ALL_LEN = 16;
	
	/**
	 * 消息打包
	 * @param sn
	 * @param msgId
	 * @param data
	 * @return
	 */
	public static byte[] encode(byte[] sn, byte msgId, byte[] data) {
		short len = (short) data.length;
		byte[] msg = new byte[len+PROTOCOL_ALL_LEN];
		
		//start
		msg[0] = 0x23;
		msg[1] = 0x23;
		
		//sn
		msg[2] = sn[0];
		msg[3] = sn[1];
		msg[4] = sn[2];
		msg[5] = sn[3];
		msg[6] = sn[4];
		msg[7] = sn[5];
		msg[8] = sn[6];
		msg[9] = sn[7];
		
		//加密：不加密
		msg[10] = 0x00;
		
		//消息标识
		msg[11] = msgId;
		
		//数据长度
		byte[] lenb = ByteET.short2Bytes(len);
		msg[12] = lenb[0];
		msg[13] = lenb[1];
		
		//数据区域
		for(short i=0;i<len;i++) {
			msg[14+i] = data[i];
		}
		
		//验证码
		short crc = CRC16ET.getCRC16(ByteET.getByteArr(msg,0,PROTOCOL_LEN+len));
		byte[] crcb = ByteET.short2Bytes(crc);
		msg[PROTOCOL_LEN+len] = crcb[0];
		msg[PROTOCOL_LEN+len+1] = crcb[1];
		
		return msg;
	}
	
	//TODO：移到消息接收部分执行
	/**
	 * 消息校验
	 * @param msg
	 * @return
	 */
	public static byte decode(byte[] msg) {
		/*
		 * 校验结果说明
		 * 
		 *  0x00：正常
			0x01：消息结构错误（响应流水号为0xFFFFFFFFFFFFFFFF）
			0x02：未鉴权	//根据链路id查缓存/内存状态。
			0x03：消息标识错误
			0x04：数据长度错误
			0x05：校验码错误
			0x06：数据错误
		 */
		byte result = 0x00;
		
		//0	起始符	byte	2	固定字符0x2323( ASCII字符“##”)
		byte[] start = new byte[2];
		start[0] = msg[0];
		start[1] = msg[1];
		if(start[0]!=0x23 || start[1]!=0x23) {
			result = 0x01;
			return result;
		}
		
		//2	流水号	long	8	无符号数。每天由0开始计数，每条消息+1，重发消息保留原数值，达最大值后从0循环。
		byte[] snb = ByteET.getByteArr(msg,2,8);
		long sn = ByteET.bytes2Long(snb);
		
		//10	加密类型	byte	1	加密仅针对数据区域 0x00：不加密
		//TODO: 后期再处理		
		if(msg[10]!=0x00) {
			result = 0x06; return result;
		}
		 
		
		//11	消息标识	byte	1	详见“消息标识”定义
		switch (msg[11]) {
			case 0x00: break;
			case 0x01: break;
			case 0x10: break;
			case 0x11: break;
			case 0x12: break;
			case 0x13: break;
			case 0x14: break;
			case 0x15: break;
			case 0x16: break;
			
			default:
				result=0x05;
				return result;// todo  因该为0x03？
		}
		
		//12	数据长度	short	2	数据区域长度n（加密后长度）
		short len = 0;
		byte[] lenb = ByteET.getByteArr(msg,12,2);
		len = ByteET.bytes2Short(lenb);
		len = 2;  // todo  删除
		if(msg.length != len + 16) {
			result = 0x04;
			return result;
		}
		//14	数据区	byte[n]	n	数据内容，详见各接口“数据区域”定义
		
		//14+n	校验位	word	2	crc16校验码，用于数据一致验证。
		short crc1 = CRC16ET.getCRC16(ByteET.getByteArr(msg,0,PROTOCOL_LEN+len));
		short crc_msg = ByteET.bytes2Short(ByteET.getByteArr(msg, PROTOCOL_LEN+len, 2));
		
		if(crc1-crc_msg!=0) {
			result = 0x05;
			return result;
		}
						
		return result;
	}
	
	
	//TODO： 消息处理，后期迁移
	public static byte[] msgDeal(byte[] msg) {
		
		byte[] rep = new byte[1];
		rep[0] = 0x00;
		
		//判断消息长度，防越界
		if(msg.length<16) {
			rep[0] = 0x01;
			return encode(ByteET.getByteArr(msg,2,10), (byte) 0x80, rep);
		}
		
		//消息id
		byte msgId = msg[11];
		//处理心跳
		if(msgId == 0x00) {
			rep[0] = 0x00;
			return encode(ByteET.getByteArr(msg,2,10), (byte) 0x80, rep);
		}
		
		//鉴权
		if(msgId == 0x01) {
			//TODO: 查缓存/查库表验证身份。
			//通过后更新缓存状态返回成功应答消息。
			//缓存： protocol.links:链路id:接入代码。
			//失败后返回鉴权失败应答消息，不处理缓存。
		}
		
		//TODO: 校验链路鉴权关系，通过缓存状态判断。
		// 缓存： protocol.links:链路id:接入代码。
		// 如果接入代码为空则表示没有鉴权或没有通过。
		
		//消息校验
		rep[0] = decode(msg);
		if(rep[0]==0x00) {
			//验证通过，处理业务数据
			switch (msg[11]) {
				case 0x10: break;
				case 0x11: break;
				case 0x12: break;
				case 0x13: break;
				case 0x14: break;
				case 0x15: break;
				case 0x16: break;			
			}
		}
		
		return encode(ByteET.getByteArr(msg,2,10), (byte) 0x80, rep);
				
	}
	
	/**
	 * 处理频发数据
	 * @param len
	 * @param data
	 */
//	public static void Decoder4common(byte[] len, byte[] data) {
//
//		if(ByteET.bytes2Short(len)-46!=0) {
//			//TODO: 保存错误日志。
//			return;
//		}
//
//		DataCommon common = new DataCommon();
//
//		// 0	设备编号	string	30	设备编号，不足30位左侧补充0x00（注：字符”0”=0x30）
//		byte[] deviceb = ByteET.getByteArr(data, 0, 30);
//		common.setDevice(ByteET.bytes2String(ByteET.leftReplenishClean(deviceb,(byte) 0x00)));
//
//		//30	时间戳	long	8	采集数据时间戳
//		long time = ByteET.bytes2Long(ByteET.getByteArr(data, 30, 8));
//		common.setTime(new Date(time));
//
//		//38	电流	short	2	精度1，单位mA  0xFFFF表示无效；0xFFFE表示错误。
//		if((data[39]!=0xFF && data[39]!=0xFE) || data[38]!=0xFF) {
//			common.setCur(ByteET.bytes2Short(ByteET.getByteArr(data, 38, 2)));
//		}else {
//			common.setCur(-1);
//		}
//
//		//40	电压	short	2	精度0.1，单位V，电压=实际电压*100xFFFF表示无效；0xFFFE表示错误。
//		if((data[39]!=0xFF && data[39]!=0xFE) || data[38]!=0xFF) {
//			common.setVol(10* ByteET.bytes2Short(ByteET.getByteArr(data, 38, 2)));
//		}else {
//			common.setVol(-1);
//		}
//
//		//42	功率	short	2	精度0.1，单位w，功率=实际功率*10 0xFFFF表示无效；0xFFFE表示错误。
//		if((data[39]!=0xFF && data[39]!=0xFE) || data[38]!=0xFF) {
//			common.setPwr(10 * ByteET.bytes2Short(ByteET.getByteArr(data, 38, 2)));
//		}else {
//			common.setPwr(-1);
//		}
//
//		//44	电流超限	byte	1	0x00：正常；0x01：低于最低电流；0x11：高于最高电流。
//		common.setCurAlert(data[44]);
//
//		//45	电压超限	byte	1	0x00：正常；0x01：低于最低电压；0x11：高于最高电压。
//		common.setVolAlert(data[45]);
//	}
	
	
	
}
