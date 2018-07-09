package com.xio.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;

import org.apache.log4j.Logger;

import com.xio.domain.Room;
import com.xio.socket.PlayerSocket;
import com.xio.socket.RoomSocket;

public class RoomUtil {
	
	private static Logger logger = Logger.getLogger(RoomUtil.class);
	
	private static Map<String, Room> roomMap = new ConcurrentHashMap<String, Room>();

	/**
	 * 
	 * @param sessionId 房间socket的sessionId
	 * @param roomSocket 房间socket
	 */
	public static void createRoom(String sessionId, RoomSocket roomSocket) {
		if (!roomMap.containsKey(sessionId)) {
			Room room = new Room(roomSocket);
			roomMap.put(sessionId, room);
		}
	}
	
	/**
	 * 添加玩家到房间
	 * 
	 * @param sessionId
	 *            房间socket的sessionId
	 * @param playerSocket
	 *            玩家socket
	 */
	public static boolean putPlayer(String sessionId, PlayerSocket playerSocket) {
		if( roomMap.containsKey(sessionId) ){
			Room room = roomMap.get(sessionId);
			if (room.getPlayerMap().size() < 2) { // 限制两名玩家
				room.getPlayerMap().put(playerSocket.getSessionId(), playerSocket);
				roomMap.put(sessionId, room);
				return true;
			}
			return false;
		}else{
			return false;
		}
	}

	/**
	 * 从房间移除玩家
	 * 
	 * @param sessionId
	 *            房间socket的sessionId
	 * @param playerScoketSessionId
	 *            玩家socket的sessionId
	 */
	public static void removePlayer(String sessionId, String playerScoketSessionId) {
		Room room = roomMap.get(sessionId);
		if ( room != null && room.getPlayerMap().containsKey(playerScoketSessionId)) {
			room.getPlayerMap().remove(playerScoketSessionId);
			roomMap.put(sessionId, room);
		}
	}

	/**
	 * 根据房间socket的sessionId获取RoomSocket
	 * 
	 * @param roomSocketSessionId
	 * @return
	 */
	public static RoomSocket getRoomSocket(String roomSocketSessionId) {
		Room room = roomMap.get(roomSocketSessionId);
		if (room != null) {
			return room.getRoomSocket();
		} else {
			return null;
		}
	}

	/**
	 * 根据房间socket的sessionId获取玩家socket
	 * 
	 * @param roomSocketSessionId
	 * @return
	 */
	public static Map<String, PlayerSocket> getPlayerMap(String roomSocketSessionId) {
		Room room = roomMap.get(roomSocketSessionId);
		if (room != null) {
			return room.getPlayerMap();
		} else {
			return null;
		}
	}

	/**
	 * 移除房间
	 * 
	 * @param sessionId
	 *            房间socket的sessionId
	 */
	public static void remove(String roomSocketSessionId) {
		if (roomMap.containsKey(roomSocketSessionId)) {
			roomMap.remove(roomSocketSessionId);
		}
	}

}
