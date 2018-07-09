package com.xio.socket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xio.util.MD5Util;
import com.xio.util.RoomUtil;

@ServerEndpoint("/player/{sessionId}/{name}")
public class PlayerSocket extends SupportScoket{

	private static Logger logger = Logger.getLogger(PlayerSocket.class);
	
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "sessionId") String roomSocketSessionId,
			@PathParam(value = "name") String playName) {
		this.session = session;
		if( StringUtils.isBlank(roomSocketSessionId) ){
			sendMessage(ERROR,"房间id为空");
			return;
		}
		if( StringUtils.isBlank(playName) ){
			sendMessage(ERROR,"玩家名为空");
			return;
		}
		this.name = playName;
		this.sessionId = MD5Util.md5Str(session.getId());
		boolean flag = RoomUtil.putPlayer(roomSocketSessionId, this);
		if( flag == false ){
			sendMessage(ERROR,"加入房间失败，房间不存在或者人数已满");
			return;
		}
		
		sendMessage(PLAY_JOIN,RoomUtil.getRoomSocket(roomSocketSessionId).getName()+":"+sessionId);	//发送给手机
		sendMessageToRoom(PLAY_JOIN,roomSocketSessionId);	//发送加入房间消息给房间
		logger.info("玩家 [" + name + "] ["+sessionId+"] 加入房间 "+roomSocketSessionId);
	}

	@OnClose
	public void onClose(@PathParam(value = "sessionId") String roomSocketSessionId) {
		RoomUtil.removePlayer(roomSocketSessionId, sessionId);
		sendMessageToRoom(PLAY_EXIT,roomSocketSessionId);	//玩家退出房间
		logger.info("玩家  [" + name + "] ["+sessionId+"] 退出房间 " +roomSocketSessionId);
	}

	@OnMessage
	public void onMessage(String message, Session session,@PathParam(value = "sessionId") String roomSocketSessionId) {
		sendMessageToRoom(PLAY_SHAKE,roomSocketSessionId);	//发送摇动消息给房间
	}

	@OnError
	public void onError(Session session, Throwable error) {
		logger.error("玩家  [" + name + "] ["+sessionId+"] 出错");
		error.printStackTrace();
	}
	
	//给房间发送消息
	public void sendMessageToRoom(String code,String roomSocketSessionId){
		//发送给房间
		RoomSocket roomSocket = RoomUtil.getRoomSocket(roomSocketSessionId);
		if( roomSocket != null ){
			roomSocket.sendMessage(code,name+":"+sessionId);
			logger.info("玩家  [" + name + "] ["+sessionId+"] 发送消息给  [" + roomSocket.getName() + "] ["+roomSocketSessionId+"]" );
		}
	}
	
}
