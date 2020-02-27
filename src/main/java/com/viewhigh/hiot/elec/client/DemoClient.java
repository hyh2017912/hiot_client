package com.viewhigh.hiot.elec.client;

import java.util.Date;
import java.util.Random;

import com.viewhigh.hiot.elec.client.support.utils.ByteET;
import com.viewhigh.hiot.publicTools.CRC16;

public class DemoClient {

	public static final int PROTOCOL_LEN = 14;
	public static final int PROTOCOL_ALL_LEN = 16;

	public static final String code = "10001";
	public static final String authToken = "12345678901234567890123456789012";
	public static final String deviceId = "000456789012345678901234567890";

	public static final byte[] deviceB = ByteET.string2Bytes(deviceId);

	/*
	 * 0x00 心跳 0x01 身份鉴权 0x10 频发数据 0x11 设备状态更新 0x12 意外断电数据 0x13 电流超限 0x14 电压超限 0x15
	 * 统计数据 0x16 配置数据
	 * 
	 */

	public static byte[] createMsg(byte msgId) {
		int n = 0;
		switch (msgId) {
		case 0x00:
			return encode(ByteET.long2Bytes(new Date().getTime()), msgId, null);
		case 0x01:
			// 0 接入代码 string 5| 10001
			// 5 鉴权码 string 32 | 202CB962AC59075B964B07152D234B70
//			byte[] data = ByteET.string2Bytes(code +  " " + authToken);
			byte[] data = (code  + authToken).getBytes();
			return encode(ByteET.long2Bytes(new Date().getTime()), msgId, data);
		case 0x10:
			/*
			 * 0 设备编号 string 30 | 123456789012345678901234567890 30 时间戳 long 8 38 电流 short 2
			 * 40 电压 short 2 42 功率 short 2 44 电流超限 byte 1 45 电压超限 byte 1
			 */
			data = new byte[46];
			n = 0;
			for (int i = 0; i < deviceB.length; i++) {
				data[n++] = deviceB[i];
			}
			byte[] b = ByteET.long2Bytes(new Date().getTime());
			for (int i = 0; i < b.length; i++) {
				data[n++] = b[i];
			}
			byte[] c = ByteET.short2Bytes((short) 2100);
			data[n++] = c[0];
			data[n++] = c[1];
			byte[] d = ByteET.short2Bytes((short) 2210);
			data[n++] = d[0];
			data[n++] = d[1];
			byte[] e = ByteET.short2Bytes((short) 45000);
			data[n++] = e[0];
			data[n++] = e[1];

			data[n++] = 0x00;
			data[n++] = 0x00;

			return encode(ByteET.long2Bytes(new Date().getTime()), msgId, data);
		case 0x11:
			/*
			 * 0 设备编号 string 30 设备编号，不足30位左侧补充0x00（注：字符”0”=0x30） 30 时间戳 long 8 采集数据时间戳 38 状态
			 * byte 1 发送更新后的状态。 0x01:关机；0x02：待机；0x03：工作 0xFF表示无效；0xFE表示错误。
			 */
			data = new byte[39];
			n = 0;
			for (int i = 0; i < deviceB.length; i++) {
				data[n++] = deviceB[i];
			}
			b = ByteET.long2Bytes(new Date().getTime());
			for (int i = 0; i < b.length; i++) {
				data[n++] = b[i];
			}

			data[n++] = 0x03;
			return encode(ByteET.long2Bytes(new Date().getTime()), msgId, data);
		case 0x12:
			/*
			 * 0 设备编号 string 30 30 时间戳 long 8 38 累计意外断电次数 short 2
			 */

			data = new byte[40];
			n = 0;
			for (int i = 0; i < deviceB.length; i++) {
				data[n++] = deviceB[i];
			}
			b = ByteET.long2Bytes(new Date().getTime());
			for (int i = 0; i < b.length; i++) {
				data[n++] = b[i];
			}

			data[n++] = 0x00;
			data[n++] = 0x10;
			return encode(ByteET.long2Bytes(new Date().getTime()), msgId, data);
		case 0x13:
			break;
		case 0x14:
			break;
		case 0x15:
			// 统计数据
			data = new byte[78];
			n = 0;
			for (int i = 0; i < deviceB.length; i++) {
				data[n++] = deviceB[i];
			}
			b = ByteET.long2Bytes(new Date().getTime());
			for (int i = 0; i < b.length; i++) {
				data[n++] = b[i];
			}

			data[n++] = 0x00;
			data[n++] = 0x10;

			data[n++] = 0x00;
			data[n++] = 0x03;

			for (int i = 0; i < 3; i++) {
				b = ByteET.long2Bytes(new Date().getTime());
				for (int j = 0; j < b.length; j++) {
					data[n++] = b[j];
				}

				if (i == 0){
					data[n++] = 0x01;
					data[n++] = 0x02;
					data[n++] = 0x03;
					data[n++] = 0x04;
				}else if (i == 1){
					data[n++] = 0x11;
					data[n++] = 0x04;
					data[n++] = 0x03;
					data[n++] = 0x14;
				}
				else {
					data[n++] = 0x11;
					data[n++] = 0x10;
					data[n++] = 0x03;
					data[n++] = 0x14;
				}
			}
			return encode(ByteET.long2Bytes(new Date().getTime()), msgId, data);
		case 0x16:
			//配置数据
			data = new byte[50];
			n = 0;
			for (int i = 0; i < deviceB.length; i++) {
				data[n++] = deviceB[i];
			}
			b = ByteET.long2Bytes(new Date().getTime());
			for (int i = 0; i < b.length; i++) {
				data[n++] = b[i];
			}

			data[n++] = 0x00;
			data[n++] = 0x10;

			data[n++] = 0x00;
			data[n++] = 0x11;

			data[n++] = 0x00;
			data[n++] = 0x12;

			data[n++] = 0x00;
			data[n++] = 0x13;

			data[n++] = 0x00;
			data[n++] = 0x00;
			data[n++] = 0x00;
			data[n++] = 0x17;
			return encode(ByteET.long2Bytes(new Date().getTime()), msgId, data);
		}
		
		return encode(ByteET.long2Bytes(new Date().getTime()), (byte) 0x00, null);

	}

	/**
	 * 消息打包
	 * 
	 * @param sn
	 * @param msgId
	 * @param data
	 * @return
	 */
	public static byte[] encode(byte[] sn, byte msgId, byte[] data) {

		short len = null == data ? 0 : (short) data.length;
		byte[] msg = new byte[len + PROTOCOL_ALL_LEN];

		// start
		msg[0] = 0x23;
		msg[1] = 0x23;

		// sn
		msg[2] = sn[0];
		msg[3] = sn[1];
		msg[4] = sn[2];
		msg[5] = sn[3];
		msg[6] = sn[4];
		msg[7] = sn[5];
		msg[8] = sn[6];
		msg[9] = sn[7];

		// 加密：不加密
		msg[10] = 0x00;

		// 消息标识
		msg[11] = msgId;

		// 数据长度
		byte[] lenb = ByteET.short2Bytes(len);
		msg[12] = lenb[0];
		msg[13] = lenb[1];

		// 数据区域
		for (short i = 0; i < len; i++) {
			msg[14 + i] = data[i];
		}

		// 验证码
		short crc = CRC16.getCRC16(ByteET.getByteArr(msg, 0, PROTOCOL_LEN + len));
		byte[] crcb = ByteET.short2Bytes(crc);
		msg[PROTOCOL_LEN + len] = crcb[0];
		msg[PROTOCOL_LEN + len + 1] = crcb[1];

		return msg;
	}
}
