/**
 * data-server. 2012-6-23
 */
package com.anxlng.vts.common;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * session配置生成以及数据处理接口
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public abstract class AbstractSessionIoHandler implements IoHandler {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	public static final String SESSION = "com.anxlng.vts.common.Session";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionCreated(IoSession ioSession) throws Exception {
		Session session = createSession(ioSession);
		attachSession(ioSession, session);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionOpened(IoSession ioSession) throws Exception {
		logger.info("new session from {} has been opened", ioSession.getRemoteAddress());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionClosed(IoSession ioSession) throws Exception {
		getSession(ioSession).close(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionIdle(IoSession ioSession, IdleStatus status)
			throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(IoSession ioSession, Throwable cause)
			throws Exception {
		

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageReceived(IoSession ioSession, Object message)
			throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageSent(IoSession ioSession, Object message) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public void attachSession(IoSession ioSession, Session session) {
		ioSession.setAttribute(SESSION, session);
	}
	
	public Session getSession(IoSession ioSession) {
		return (Session)ioSession.getAttribute(SESSION);		
	}
	
	protected abstract Session createSession(IoSession ioSession) throws Exception;

}
