package com.lkh.server.http.coder;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.lkh.server.http.HttpResponseMessage;

public class HttpServerProtocolCodecFactory extends  
        DemuxingProtocolCodecFactory {
    public HttpServerProtocolCodecFactory() {  
        super.addMessageDecoder(HttpRequestDecoder.class);  
        super.addMessageEncoder(HttpResponseMessage.class,  
                HttpResponseEncoder.class);  
    }
  
}  