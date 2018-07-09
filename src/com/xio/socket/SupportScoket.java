package com.xio.socket;

import java.io.IOException;

import javax.websocket.Session;

public class SupportScoket {

	protected final static String CREATE_ROOM = "101";	//创建房间
	protected final static String PLAY_JOIN = "102";	//玩家加入房间
	protected final static String PLAY_SHAKE = "103";	//发送摇动消息
	protected final static String PLAY_EXIT = "104";	//玩家退出房间
	protected final static String CHANGE_ROOM_NAME = "105";	//修改房间名字
	protected final static String ROOM_EXIT = "106";	//房间解散
	protected final static String PLAY_START = "107";	//通知玩家开始游戏
	protected final static String PLAY_RESULTS = "108";	//通知玩家结果
	protected final static String ERROR = "500";	//错误
	
	protected String name;	//房间名或玩家手机名
	protected Session session;
	protected String sessionId;
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	} 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void sendMessage(String code,String message) {
		try {
			this.session.getBasicRemote().sendText(code+":"+message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
