/**
 * data-server. 2012-6-20
 */
package com.anxlng.util;

/**
 * 关于bytes字节操作的一系列工具函数
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class ByteFunc {

	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String printHex(byte[] array) {
		return printHex(array, 0, array.length);
	}

	/**
	 * 把字节数组转成用于打印的16进制字符串形式，每个字节(16进制表示形式)空格间隔
	 * 使用时注意，程序检测offset+len是否超过数组的容量；
	 * 如果超过，则只转换 offset 到数组结尾的字节数组
	 * @param array
	 * @param offset
	 * @param len
	 * @return
	 */
	public static String printHex(byte[] array, int offset, int len) {
//		if (array.length < offset + len) len = array.length - offset;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			byte b = array[i + offset];
			if (sb.length() > 0) { // 第一位不用加
				sb.append(' ');
			}
			sb.append(digits[(b >> 4) & 0x0f]); // 高字节
			sb.append(digits[b & 0x0f]); // 低字节
		}

		return sb.toString();
	}

	public static String toHex(byte[] array) {
		return toHex(array, 0, array.length);
	}

	/**
	 * 把字节数组转成16进制字符串形式
	 * 使用时注意，程序检测offset+len是否超过数组的容量；
	 * 如果超过，则只转换 offset 到数组结尾的字节数组
	 * @param array
	 * @param offset
	 * @param len
	 * @return
	 */
	public static String toHex(byte[] array, int offset, int len) {
//		if (array.length < offset + len) len = array.length - offset;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			byte b = array[i + offset];
			sb.append(digits[(b >> 4) & 0x0f]); // 高字节
			sb.append(digits[b & 0x0f]); // 低字节
		}
		return sb.toString();
	}

	public static boolean arrayEquals(byte[] a1, int a1Offset, byte[] a2,
			int a2Offset, int length) {
		if (a1.length < a1Offset + length || a2.length < a2Offset + length) {
			return false;
		}
		while (length-- > 0) {
			if (a1[a1Offset++] != a2[a2Offset++]) {
				return false;
			}
		}
		return true;
	}
	
	public static int toInt2(byte[] array) {
		return toInt2(array, 0);
	}
	
	/**
	 * 取两位字节转成int值，
	 * 使用时注意，程序没有检测offset，是否超过数组的容量；如果超过，会出现数组空指针异常
	 * @param array
	 * @param offset
	 * @return
	 */
	public static int toInt2(byte[] array, int offset) {
		return ((array[offset++] << 8) & 0xff00) | (array[offset] & 0xff);
	}
	
	public static int toInt4(byte[] array) {
		return toInt4(array, 0);
	}
	
	/**
	 * 取四位字节转成int值，
	 * 使用时注意，程序没有检测offset，是否超过数组的容量；如果超过，会出现数组空指针异常
	 * @param array
	 * @param offset
	 * @return
	 */
	public static int toInt4(byte[] array, int offset) {
		return ((array[offset++] << 24) & 0xff000000) 
				| ((array[offset++] << 16) & 0xff0000) 
				| ((array[offset++] << 8) & 0xff00) 
				| (array[offset] & 0xff);
	}
	
	/**
	 * 取len位字节转成int值，
	 * 使用时注意，程序没有检测offset，是否超过数组的容量；如果超过，会出现数组空指针异常
	 * @param array
	 * @param offset
	 * @param len 要取得的位数，len不能大于4，大于4按4位进行计算
	 * @return
	 */
	public static int toInt(byte[] array, int offset, int len) {
		int result;
		switch (len) {
		case 1: result = array[offset] & 0xff; break;
		case 2: result = toInt2(array, offset); break;
		case 3: result = ((array[offset++] << 16) & 0xff0000) 
						| ((array[offset++] << 8) & 0xff00) 
						| (array[offset] & 0xff); break;
		default: result = toInt4(array, offset); break;
		}
		return result;
	}
	
	
	public static String toBinary(byte[] array) {
		return toBinary(array, 0, array.length);
	}

	/**
	 * 把字节数组转成二进制数据字符串
	 * 使用时注意，程序检测offset+len是否超过数组的容量；
	 * 如果超过，则只转换 offset 到数组结尾的字节数组
	 * @param array
	 * @param offset
	 * @param len
	 * @return 
	 */
	public static String toBinary(byte[] array, int offset, int len) {
//		if (array.length < offset + len) len = array.length - offset;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			byte b = array[i + offset];

			for (int n = 7; n >= 0; n--) {
				sb.append(digits[(b >>> n) & 0x01]);
			}
		}
		return sb.toString();
	}

}
