/**
 * data-server. 2012-6-20
 */
package com.anxlng.vts.common.codec.perfixed;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class PerfixedHexCodecFactory implements ProtocolCodecFactory {
	
	private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;
    
    public static final int DEFAULT_PREFIX_LENGTH = 2;
    public static final int DEFAULT_MAX_DATA_LENGTH = 2048;
    
    
    public PerfixedHexCodecFactory() {
    	this.decoder = new PrefixedHexDecoder(DEFAULT_PREFIX_LENGTH, DEFAULT_MAX_DATA_LENGTH);
    	this.encoder = new PrefixedHexEncoder(DEFAULT_PREFIX_LENGTH, DEFAULT_MAX_DATA_LENGTH);
    }
    
    public PerfixedHexCodecFactory(int ePrefixLenth, int eMaxDataLength, 
    		int dPrefixLenth, int dMaxDataLength) {
    	this.decoder = new PrefixedHexDecoder(dPrefixLenth, dMaxDataLength);
    	this.encoder = new PrefixedHexEncoder(ePrefixLenth, eMaxDataLength);
    }
    
	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

}
