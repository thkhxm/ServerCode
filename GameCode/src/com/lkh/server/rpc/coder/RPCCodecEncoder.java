package com.lkh.server.rpc.coder;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.lkh.server.rpc.RPCServer;
import com.lkh.server.rpc.RPCSource;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.ByteUtil;



public class RPCCodecEncoder implements ProtocolEncoder {

	private RuntimeSchema<RPCSource> schema;
	private RuntimeSchema<RPCServer> serverSchema;

	public RPCCodecEncoder(RuntimeSchema<RPCSource> schema,RuntimeSchema<RPCServer> serverSchema){
		this.schema = schema;
		this.serverSchema = serverSchema;
	}

	public void encode(IoSession session, Object message,ProtocolEncoderOutput out) throws Exception {		
		//synchronized(session){
		IoBuffer buffer;
		if(message instanceof String){
			byte[] bytes = ((String)message).getBytes("UTF-8");
			buffer = IoBuffer.allocate(bytes.length);
			buffer.put(bytes);
			buffer.flip();
//			log.info(">>bytes[{}]",buffer.array());
			out.write(buffer);
			buffer.free();
		}else if(message instanceof RPCSource){
			RPCSource res = (RPCSource)message;
			byte bytes[] = ProtostuffIOUtil.toByteArray(res, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));	
			byte zip = 0;
			if(bytes.length>1024) {
				bytes = ByteUtil.compress(bytes);
				zip = 1; 
			}
			int size = bytes.length;
			buffer = IoBuffer.allocate(size + 5,false);
			buffer.putInt(size);//4

			buffer.put(zip);//1
			buffer.put(bytes);
			buffer.flip();
//			log.info(">>bytes[{}]",buffer.array());
			out.write(buffer);
			buffer.free();
			LoggerManager.debug(">>ReqId["+res.getReqId()+"],ServiceCode["+res.getMethodCode()+"],ByteSize["+size+"]");
		}else if(message instanceof RPCServer){
			RPCServer res = (RPCServer)message;
			byte bytes[] = ProtostuffIOUtil.toByteArray(res, serverSchema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));	
			byte zip = 2;
//			if(bytes.length>1024) {
//				bytes = ByteUtil.compress(bytes);
//				zip = 1; 
//			}
			int size = bytes.length;
			buffer = IoBuffer.allocate(size + 5,false);
			buffer.putInt(size);//4

			buffer.put(zip);//1
			buffer.put(bytes);
			buffer.flip();
//			log.info(">>bytes[{}]",buffer.array());
			out.write(buffer);
			buffer.free();
			LoggerManager.debug("向服务器注册自己的节点信息------------->"+res.toString());
//			log.debug(">>ReqId["+res.getReqId()+"],ServiceCode["+res.getMethodCode()+"],ByteSize["+size+"]");
		}
		//}		
	}

	public void dispose(IoSession session) throws Exception {}

	
	/*
	public static byte[] compress(byte[] data){

		byte[] temp = new byte[data.length];
		Deflater compresser = new Deflater();
		compresser.setInput(data);
		compresser.finish();
		int n = compresser.deflate(temp);
		if(!compresser.finished()){
			logger.error("----");
		}
		return Arrays.copyOf(temp, n);		
	}
	*/
}
