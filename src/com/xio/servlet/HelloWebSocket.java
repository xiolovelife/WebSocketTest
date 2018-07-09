package com.xio.servlet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

public class HelloWebSocket extends WebSocketServlet{

	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class MyMessageInbound extends MessageInbound {

		@Override
		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void onTextMessage(CharBuffer arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
