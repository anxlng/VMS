/*
 *  2010-11-09.
 */
package com.anxlng.vts.common.codec.ht;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 对接收的数据按照头尾结构进行解包，只有带头带尾的完整数据才是合法的数据
 * @author 唐吉星<anluln@163.com>
 */
public class HeadToTailDecoder implements ProtocolDecoder {

    public Logger LOG = Logger.getLogger(getClass());
    /**
     * 用来定义上下文未完包数据的缓存，如：完整的数据有头有尾，如果只发送了一部分数据，
     * 则缓存，等待接收到完整数据
     */
    private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
    private final Charset charset;
//    private final String head;    //协议头字符,长度不超过10
//    private final String tail;    //协议尾字符,长度不超过10
    private ByteBuffer headB;
    private ByteBuffer tailB;
    private int maxDataLength = 2048;//完整数据包最大长度
    

    /**
     * 默认末尾回车
     */
    public HeadToTailDecoder() {
        this(null);
    }

    /**
     * 构造方法，构造一个头尾协议的编码实例，以设定的编码进行解析
     */
    public HeadToTailDecoder(Charset charset) {
        this(charset, null, "\r\n");
    }

    public HeadToTailDecoder(String head, String tail) {
        this(null, head, tail);
    }

    public HeadToTailDecoder(Charset charset, String head, String tail) {
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        if (tail == null || tail.equals("")) {
            throw new NullPointerException("tail is null or empty");
        }
        if (head != null && head.length() > 10) {
            throw new RuntimeException("head is too lang!head's length must less than 10");
        }
        if (tail != null && tail.length() > 10) {
            throw new RuntimeException("tail is too lang!tail's length must less than 10");
        }

        this.charset = charset;
        CharsetEncoder encoder = charset.newEncoder();
        try {
        	if (head != null && headB == null) {
            	CharBuffer cb = CharBuffer.wrap(head);
            	headB = encoder.encode(cb);
            }
            if (tailB == null) {
            	CharBuffer cb = CharBuffer.wrap(tail);
            	tailB = encoder.encode(cb);
            }
        } catch (CharacterCodingException ex) {
        	throw new RuntimeException("head or tail coding exception！！please check");
        }
        
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
    public void decode(IoSession session, IoBuffer buff,
            ProtocolDecoderOutput out) throws Exception {
        // 如果状态为对所有端口数据进行解码 或者连接端口等于相应的端口则进行解码

        if (headB == null || !headB.hasRemaining()) {
            decodeTail(session, buff, out);
            return;
        }
        
        decodePacket(session, buff, out);
    }
    
    private void decodePacket(IoSession session, IoBuffer buff,
            ProtocolDecoderOutput out) throws Exception {
        
        Context ctx = getContext(session);//获取上次结余内容
        

        int headCount = ctx.getHeadCount();
        int tailCount = ctx.getTailCount();

        int oldPos = buff.position();
        int oldLim = buff.limit();

        while (buff.hasRemaining()) {
            byte b = buff.get();

            //报文头是否已完全找到
            if (headB.limit() == headCount) {
                //查找报文尾
                if (tailB.get(tailCount) == b) {
                    tailCount++;
                    if (tailB.limit() == tailCount) {//如果报文尾完全找到

                        int nowPos = buff.position();
                        buff.limit(nowPos);
                        buff.position(oldPos);
                        ctx.append(buff);//读取内容

                        buff.limit(oldLim);
                        buff.position(nowPos);

                        //无溢出，数据长度不超过最大长度
                        if (ctx.getOverflowPosition() == 0) {
                            IoBuffer data = ctx.getBuffer();
                            data.flip();
                            data.limit(data.limit() - tailCount);//去掉尾巴部分
                            data.position(headCount);//去掉头部分
                            try {
                                out.write(data.getString(ctx.getDecoder()));
                            } finally {
                                data.clear();
                                data.free();
                            }
                        } else {
                            int overflowPosition = ctx.getOverflowPosition();
                            ctx.reset();
                            throw new RuntimeException("data is too lang:" + overflowPosition);
                        }
                        //从当前位置继续开始解析
                        oldPos = nowPos;
                        headCount = 0;
                        tailCount = 0;
                    }
                } else {
                    tailCount = 0;
                }
            } else {  //查找报文头
                if (headB.get(headCount) == b) {
                    headCount++;
                    //如果找到头了，则开始录入数据部分，数据才开始
                    if (headB.limit() == headCount) {
                        ctx.getBuffer().clear();
                    }
                } else {  //如果不等于，则初始数据位置后移
                    oldPos = buff.position();
                }
                tailCount = 0;
            }
        }

        buff.position(oldPos);
        ctx.append(buff);

        ctx.setHeadCount(headCount);
        ctx.setTailCount(tailCount);
    }

    /**
     * 按照尾定界符对数据进行分包，
     * {@link #decodeHead 按照头定界符对数据进行分包}
     */
    private void decodeTail(IoSession session, IoBuffer in,
            ProtocolDecoderOutput out) throws Exception {
        Context ctx = getContext(session);//获取上次结余内容
        int matchCount = ctx.getTailCount();

        // Try to find a match
        int oldPos = in.position();
        int oldLimit = in.limit();
        while (in.hasRemaining()) {
            byte b = in.get();
            if (tailB.get(matchCount) == b) {
                matchCount++;
                if (matchCount == tailB.limit()) {
                    // Found a match.
                    int pos = in.position();
                    in.limit(pos);
                    in.position(oldPos);
                    ctx.append(in);
                    in.limit(oldLimit);
                    in.position(pos);
                    if (ctx.getOverflowPosition() == 0) {
                        IoBuffer buf = ctx.getBuffer();
                        
                        buf.flip();
                        buf.limit(buf.limit() - matchCount);
                        try {
                            out.write(buf.getString(ctx.getDecoder()));
                        } finally {
                            buf.clear();
                        }
                    } else {
                        int overflowPosition = ctx.getOverflowPosition();
                        ctx.reset();
                        throw new Exception(
                                "Line is too long: " + overflowPosition);
                    }
                    oldPos = pos;
                    matchCount = 0;
                }
            } else {
                // fix for DIRMINA-506 & DIRMINA-536
                in.position(Math.max(0, in.position() - matchCount));
                matchCount = 0;
            }
        }
        // Put remainder to buf.
        in.position(oldPos);
        ctx.append(in);
        ctx.setTailCount(matchCount);
    }

    /**
     * 对数据无法解析的处理
     * @param is
     * @param out
     * @throws Exception
     */
    @Override
    public void finishDecode(IoSession is, ProtocolDecoderOutput out) throws Exception {
        //忽略ignore
    }

    /**
     * 服务器关闭时需要进行的处理
     */
    @Override
    public void dispose(IoSession session) throws Exception {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx != null) {
            session.removeAttribute(CONTEXT);
        }
    }

    private Context getContext(IoSession session) {
        Context ctx;
        ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx == null) {
            ctx = new Context();
            session.setAttribute(CONTEXT, ctx);
        }
        return ctx;
    }

    private class Context {

        private final CharsetDecoder decoder;
        private final IoBuffer buf;
        private int headCount = 0;
        private int tailCount = 0;
        private int overflowPosition = 0;

        private Context() {
            decoder = charset.newDecoder();
            //分配缓存自己组是一个包最大长度
            buf = IoBuffer.allocate(80).setAutoExpand(true);
        }

        public CharsetDecoder getDecoder() {
            return decoder;
        }

        public IoBuffer getBuffer() {
            return buf;
        }

        public int getOverflowPosition() {
            return overflowPosition;
        }

        public int getHeadCount() {
            return headCount;
        }

        public void setHeadCount(int headCount) {
            this.headCount = headCount;
        }

        public int getTailCount() {
            return tailCount;
        }

        public void setTailCount(int tailCount) {
            this.tailCount = tailCount;
        }

        public void reset() {
            overflowPosition = 0;
            headCount = 0;
            tailCount = 0;
            decoder.reset();
        }

        public void append(IoBuffer in) {
            if (overflowPosition != 0) {
                discard(in);
            } else if (buf.position() > maxDataLength - in.remaining()) {
                overflowPosition = buf.position();
                buf.clear();
                buf.free();
                discard(in);
            } else {
                getBuffer().put(in);
            }
        }

        private void discard(IoBuffer in) {
            if (Integer.MAX_VALUE - in.remaining() < overflowPosition) {
                overflowPosition = Integer.MAX_VALUE;
            } else {
                overflowPosition += in.remaining();
            }
            in.position(in.limit());
        }
    }
}
