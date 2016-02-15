package org.mokey.stormv.das.client;

import org.mokey.stormv.das.models.DalModels.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * @author wcyuan
 *
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response>{
	
	public Map<Integer, Queue<Response>> responses = new HashMap<>();

	private static final long DEFAULT_TIMEOUT = 2 * 60 * 1000;

	@Override
	protected void messageReceived(ChannelHandlerContext ctx,
			Response msg) throws Exception {
		if(msg != null){
			if(!responses.containsKey(msg.getRequestId()))
				responses.put(msg.getRequestId(), new LinkedBlockingQueue<Response>());
			responses.get(msg.getRequestId()).add(msg);
		}
	}

	/**
	 * Fetch the next response from server
	 * @param requestId the request id
	 * @return response from server
	 * @throws Exception
	 */
	public Response fetchResponse(int requestId) throws Exception{
		Response response = null;
		if(responses.containsKey(requestId)) {
			response = responses.get(requestId).poll();
			if(response != null && response.getException() != null && !response.getException().isEmpty()){
				remove(requestId);
				throw new Exception(response.getException());
			}
		}
		return response;
	}

	/**
	 * Clean the request cache
	 * @param requestId
	 */
	public void remove(int requestId){
		responses.remove(requestId);
	}

	/**
	 * Wait the only one response with specified timeout and poll interval
	 * @param requestId the request id
	 * @param timeOut timeout(ms)
	 * @return Response instance from remote server
	 * @throws Exception
	 */
	public Response waitResponse(int requestId, long timeOut) throws Exception{
		long start = System.currentTimeMillis();
		Response response = null;
		while (true){
			response = fetchResponse(requestId);
			if(response != null || System.currentTimeMillis() - start >= timeOut){
				break;
			}
		}
		remove(requestId);
		return response;
	}

	/**
	 * Wait the only one response with default timeout(1000ms) and poll interval(10ms)
	 * @param requestId the request id
	 * @return Response instance from remote server
	 * @throws Exception
	 */
	public Response waitResponse(int requestId) throws Exception{
		return waitResponse(requestId, DEFAULT_TIMEOUT);
	}
}
