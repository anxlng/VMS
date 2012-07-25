/**
 * 
 */
package com.anxlng.vts.common.codec.ht;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 该协议解码编码器，可以解码数据报文并根据字符编码格式编码成字符报文
 * 对于有结束标记的报文进行分包
 * 对连续的数据报文进行分包，分成一条条完整的协议信息包；
 * 
 * 该解码编码器可以指定只处理对应端口的数据
 * @author tangjixing <anxlng@sina.com>
 */
public class HeadToTailCodecFactory implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;
    
    public HeadToTailCodecFactory(){
        this(Charset.defaultCharset(), null,"\r\n");
    }
    
    public HeadToTailCodecFactory(Charset charset){
        this(charset, null,"\r\n");
    }


    public HeadToTailCodecFactory(String head,String tail){
        this(Charset.defaultCharset(), head, tail);
    }
    
    public HeadToTailCodecFactory(String charsetName, String head, String tail){
        this(Charset.forName(charsetName), head, tail);
    }

    public HeadToTailCodecFactory(Charset charset, String head, String tail){
        encoder = new HeadToTailEncoder(charset, head, tail);
        decoder = new HeadToTailDecoder(charset, head, tail);
    }

    public HeadToTailCodecFactory(Charset charset, String deHead, String deTail,
                                      String enHead, String enTail){
        
        decoder = new HeadToTailDecoder(charset, deHead, deTail);
        encoder = new HeadToTailEncoder(charset, enHead, enTail);
    }


    @Override
    public ProtocolEncoder getEncoder(IoSession is) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession is) throws Exception {
        return decoder;
    }
}
