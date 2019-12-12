package com.lkh.server.rpc.coder;

import io.protostuff.runtime.RuntimeSchema;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.lkh.server.rpc.RPCServer;
import com.lkh.server.rpc.RPCSource;

public class RPCCodecFactory implements ProtocolCodecFactory {
	
	private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;
    
	public RPCCodecFactory() {
		RuntimeSchema<RPCSource> sourceSchema = RuntimeSchema.createFrom(RPCSource.class);
		RuntimeSchema<RPCServer> serverSchema = RuntimeSchema.createFrom(RPCServer.class);
		encoder = new RPCCodecEncoder(sourceSchema,serverSchema);
        decoder = new RPCCodecDecoder(sourceSchema,serverSchema);
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}


}
