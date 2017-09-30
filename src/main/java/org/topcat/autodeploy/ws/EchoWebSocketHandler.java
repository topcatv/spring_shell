package org.topcat.autodeploy.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Echo messages by implementing a Spring {@link WebSocketHandler} abstraction.
 */
public class EchoWebSocketHandler extends TextWebSocketHandler {

	private static Logger logger = LoggerFactory.getLogger(EchoWebSocketHandler.class);

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		logger.info("Opened new session in instance " + this);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws Exception {
		System.out.println("from clinet message start [" + message + "]");
		String[] commandArray = {"/bin/sh", "-c", message.getPayload()};
		Process process = Runtime.getRuntime().exec(commandArray, null);
		StreamOutput outGobbler = new StreamOutput(process.getOutputStream(), "OUTPUT", message.getPayload());
		outGobbler.start();

		StreamGobbler infoGobbler = new StreamGobbler(process.getInputStream(), "INPUT", session);
		infoGobbler.start();

		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", session);
		errorGobbler.start();
		System.out.println("from clinet message end...[" + message + "]");
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception)
			throws Exception {
		session.close(CloseStatus.SERVER_ERROR);
	}

}