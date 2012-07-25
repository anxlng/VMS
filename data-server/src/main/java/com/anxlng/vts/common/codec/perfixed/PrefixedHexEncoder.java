/**
 * data-server. 2012-6-20
 */
package com.anxlng.vts.common.codec.perfixed;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class PrefixedHexEncoder implements ProtocolEncoder {

    private int prefixLength ;
    private int maxDataLength ;
	
	public PrefixedHexEncoder(int pLen, int mdLen) {
    	this.prefixLength = pLen;
    	this.maxDataLength = mdLen;
    }
    
	
	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core.session.IoSession, java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		
		if (message instanceof byte[]) {
			byte[] data = (byte[])message;
			
			int len = data.length;
			byte[] prefix = new byte[prefixLength];
			
			switch (prefixLength) {
			case 1: 
			}
			
		}

	}

	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#dispose(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

}
