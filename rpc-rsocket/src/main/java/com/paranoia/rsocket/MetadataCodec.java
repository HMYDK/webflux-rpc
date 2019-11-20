package com.paranoia.rsocket;

import com.alibaba.fastjson.JSON;
import com.paranoia.common.InvokeMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MetadataCodec {

    public static InvokeMessage decodeMetadata(byte[] bytes) throws IOException {
        return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), InvokeMessage.class);
    }

    public static byte[] encodeMetadata(InvokeMessage metadata) throws IOException {
        String jsonStr = JSON.toJSONString(metadata);
        return jsonStr.getBytes(StandardCharsets.UTF_8);
    }

}
