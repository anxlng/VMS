/*
 *  2010-11-09.
 */

package com.anxlng.vts.common.codec.ht;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 对接收的数据按照头尾结构进行组包发出，只有带头带尾的完整数据才是合法的数据
 * 
 * @author 唐吉星<anluln@163.com>
 */
public class HeadToTailEncoder implements ProtocolEncoder {

	private final AttributeKey ENCODER = new AttributeKey(getClass(), "encoder");
	private final Charset charset;
	private final String head;
	private final String tail;

	private int maxDataLength = 2048;

	/**
	 * 默认末尾回车,采用平台默认编码器，对所有端口的数据进行编码
	 */
	public HeadToTailEncoder() {
		this(null);
	}

	/**
	 * 构造方法，构造一个头尾协议的编码实例，以设定的编码进行解析
	 */
	public HeadToTailEncoder(Charset charset) {
		this(charset, null, "\r\n");
	}

	public HeadToTailEncoder(String head, String tail) {
		this(null, head, tail);
	}

	public HeadToTailEncoder(Charset charset, String head, String tail) {
		if (charset == null) {
			charset = Charset.defaultCharset();
		}
		if (tail == null || tail.equals("")) {
			throw new NullPointerException("tail is null or empty");
		}
		if (head != null && head.length() > 10) {
			throw new RuntimeException("head is too lang");
		}
		if (tail != null && tail.length() > 10) {
			throw new RuntimeException("tail is too lang");
		}

		this.charset = charset;
		this.head = head;
		this.tail = tail;

	}

	public int getMaxDataLength() {
		return maxDataLength;
	}

	public void setMaxDataLength(int maxDataLength) {
		if (maxDataLength <= 0) {
			throw new IllegalArgumentException("maxLineLength: "
					+ maxDataLength);
		}
		this.maxDataLength = maxDataLength;
	}

	@Override
	public void encode(IoSession session, Object msg, ProtocolEncoderOutput out)
			throws Exception {

		// 如果状态为对所有端口数据进行编码 或者连接端口等于相应的端口则进行编码
		CharsetEncoder encoder = (CharsetEncoder) session.getAttribute(ENCODER);
		if (encoder == null) {
			encoder = charset.newEncoder();
			session.setAttribute(ENCODER, encoder);
		}

		String value = msg.toString();

		if (value.length() > maxDataLength) {
			throw new IllegalArgumentException("Line length: " + value.length());
		}

		if (this.head != null && !this.head.equals("")) {
			value = this.head + value;
		}
		value += this.tail;
		CharBuffer cb = CharBuffer.wrap(value);
		ByteBuffer nioBuffer = encoder.encode(cb);
		IoBuffer buf = IoBuffer.wrap(nioBuffer);
		out.write(buf);
		return;
	}

	@Override
	public void dispose(IoSession session) throws Exception {

	}

}
