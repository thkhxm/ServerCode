package com.lkh.server.rpc.coder;

import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.lkh.server.rpc.RPCServer;
import com.lkh.server.rpc.RPCSource;
import com.lkh.util.ByteUtil;

import java.io.UnsupportedEncodingException;

public class RPCCodecDecoder extends CumulativeProtocolDecoder {
	
	private final AttributeKey POLICY = new AttributeKey(getClass(), "policy");
	private final String security = "<policy-file-request/>";
	public static final String gamedate = "<game-data-change/>";	
	public static final String gamestat = "<game-data-stat/>";
	public static final String gamegm   = "<gm/>";
	public static final String httpHeader = "GET / HTTP/1.1";
	private static int MAX_SIZE = 1024*90;
	
	private RuntimeSchema<RPCSource> schema;
	private RuntimeSchema<RPCServer> serverSchema;

	public RPCCodecDecoder(RuntimeSchema<RPCSource> schema,RuntimeSchema<RPCServer> serverSchema){
		this.schema = schema;
		this.serverSchema = serverSchema;
	}

	@SuppressWarnings("deprecation")
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		if(isSecurityRequest(session,in,out)){
			//out.write(security);
			//in.free();
			return true;
		}
		
		if(in.markValue()>0){
			in.reset();
		}
		else{
			in.position(0);
		}
		//针对粘包少包处理
		if(in.remaining()>0){
			
			in.mark();//标记当前位置，以便reset
//			logger.info("有数据进来了数据长度为[{}]",in.remaining());
			if(in.remaining()<5){
				in.reset();
				return false;
			}
			byte [] sizeBytes = new byte[4]; 	
			
			in.get(sizeBytes);
			int size = ByteUtil.byteToInt2(sizeBytes);
//			logger.info("请求进入->大小为["+size+"]");
			if(size>MAX_SIZE || size <=0){
				session.close(true);
				return true;
			}
			if(in.remaining()<size+1){
//				logger.info("还有包没有接收到->大小为["+size+"]");
				in.reset();//字节数组归位
				return false;
			}else{
//				logger.info("数据接收完毕，准备解包->大小为["+in.remaining()+"]");
//				byte [] ridBytes = new byte[4];
//				byte [] codeBytes = new byte[4];
//				in.get(ridBytes);
//				int rid = ByteUtil.byteToInt2(ridBytes);
//				in.get(codeBytes);
//				int code = ByteUtil.byteToInt2(codeBytes);
				byte zip = in.get();
				decodePacket(session,in,out,size,zip);
			}
		}
		in.free();
		return true;
	}
	
	//解包
	private void decodePacket(IoSession session, IoBuffer in,ProtocolDecoderOutput out,int size,byte zip) {
		try {
			byte [] sizeBytes = new byte[size];   
			in.get(sizeBytes);
//			String s = "";
//			for(int i = 0 ; i < sizeBytes.length ; i++){
//				s+=Integer.toHexString(sizeBytes[i])+",";
//			}
//			logger.error("前端发送的字节数据--->"+s);
//			InputStream   is = new ByteArrayInputStream(sizeBytes);
//			data = Mo.MoData.parseFrom(new DataInputStream(is));
			if(zip == 2){//节点注册消息，不走转发逻辑
				RPCServer source = serverSchema.newMessage();

				ProtostuffIOUtil.mergeFrom(sizeBytes, source, serverSchema);
				out.write(source);
			}else{
				RPCSource source = schema.newMessage();
				if(zip==1)
					sizeBytes = ByteUtil.decompress(sizeBytes);
				ProtostuffIOUtil.mergeFrom(sizeBytes, source, schema);
				out.write(source);
			}

//			data = MoPB.MoData.parseFrom(sizeBytes);
//			logger.trace("前端发送的数据--->"+data.getMsg());
//			Request req = new Request(session,data.getMsg(),true,reqId,code);
			
			if(in.remaining()>0){
				in.mark();
				doDecode(session,in,out);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isSecurityRequest(IoSession session, IoBuffer in,ProtocolDecoderOutput out){		
		Boolean policy = (Boolean)session.getAttribute(POLICY);
		//logger.info("=="+session.getRemoteAddress().toString()+"++"+policy);
		if(policy != null){
			return false;
		}
		String request = getRequest(in);
		if(request.length()>2){request = request.trim();}
		boolean result = false;
		if(request != null){
			result = request.startsWith(security);
			if(result)out.write(security);
			
			result = request.startsWith(gamegm);
			if(result)out.write(request);
			
			result = request.startsWith(gamedate);
			if(result)out.write(gamedate);
			
			result = request.startsWith(gamestat);			
			if(result)out.write(gamestat);
			
			result = request.startsWith(httpHeader);
//			if(result)out.write(result);
			in.free();
		}
		session.setAttribute(POLICY,new Boolean(result));
		return result;
	}

	private String getRequest(IoBuffer in){
		byte[] bytes = new byte[in.limit()];
		in.get(bytes);
		String request;
		try {
			request = new String(bytes,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			request = null;
		}  
		return request;
	}

}
