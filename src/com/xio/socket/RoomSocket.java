package com.xio.socket;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import com.xio.util.MD5Util;
import com.xio.util.RoomUtil;

@ServerEndpoint("/room/{name}")
public class RoomSocket extends SupportScoket{

	private static Logger logger = Logger.getLogger(RoomSocket.class);
	
	@OnOpen
	public void onOpen(Session session,@PathParam(value = "name") String roomName){
		this.name = roomName;
		this.session = session;
		this.sessionId = MD5Util.md5Str(session.getId());
		RoomUtil.createRoom(sessionId,this);
		sendMessage(CREATE_ROOM,sessionId);
		logger.info("创建房间 [" + name + "] ["+sessionId+"]");
	}

	@OnClose
	public void onClose() {
		logger.info("房间  [" + name + "] ["+sessionId+"] 解散");
		// 提示玩家 房间解散
		sendMessageToPlay(ROOM_EXIT,"房间  [" + name + "] ["+sessionId+"] 解散");
		// 移除房间
		RoomUtil.remove(sessionId);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		logger.info("房间 [" + name + "] ["+sessionId+"] 发来消息" + message);
		String msg[] = message.split(":");
		String code = msg[0];
		String result = msg[1]; 
		if( code.equals(PLAY_START) ){	//通知玩家开始游戏
			sendMessageToPlay(PLAY_START,"游戏开始");
		}else if( code.equals(PLAY_RESULTS) ){	//通知玩家结果
			sendMessageToPlay(PLAY_RESULTS,result);
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		logger.error("房间 [" + name + "] ["+sessionId+"] 出错");
		error.printStackTrace();
	}
	
	public void sendMessageToPlay(String code,String message){
		//发送给房间
		Map<String, PlayerSocket> playSocketMap = RoomUtil.getPlayerMap(sessionId);
		Iterator<Entry<String, PlayerSocket>> iterator = playSocketMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, PlayerSocket> entry = (Entry<String, PlayerSocket>) iterator.next();
			PlayerSocket playSocket = (PlayerSocket) entry.getValue();
			playSocket.sendMessage(code,message);
			logger.info("房间  [" + name + "] ["+sessionId+"] 给 玩家 ["+playSocket.getName()+"] ["+playSocket.getSessionId()+"] 发送 " + message);
		}
	}
}
