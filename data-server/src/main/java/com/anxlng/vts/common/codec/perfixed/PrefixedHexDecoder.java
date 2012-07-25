/**
 * data-server. 2012-6-20
 */
package com.anxlng.vts.common.codec.perfixed;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class PrefixedHexDecoder extends CumulativeProtocolDecoder{

    private int prefixLength ;
    private int maxDataLength ;
    
    public PrefixedHexDecoder(int pLen, int mdLen) {
    	this.prefixLength = pLen;
    	this.maxDataLength = mdLen;
    }
	
	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.CumulativeProtocolDecoder#doDecode(org.apache.mina.core.session.IoSession, org.apache.mina.core.buffer.IoBuffer, org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if (in.prefixedDataAvailable(prefixLength, maxDataLength)) {
			
			int length = 0;
			
			switch (prefixLength) {
			case 1: length = in.getUnsigned();
					break;
			case 2: length = in.getUnsignedShort();
					break;
			case 4: length = in.getInt();
					break;
			}
			
			if (length == 0) {
				return true;
			}
			
			byte[] data = new byte[length];
			in.get(data);
			
			out.write(data);
			return true;
		}
		
		return false;
	}

}
