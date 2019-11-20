package com.paranoia.rsocket.util;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import reactor.core.Exceptions;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @author ZHANGKAI
 * @date 2019/9/19
 * @description
 */
public class RpcUtils {

    /**
     * 解码Payload对象
     *
     * @param payload 具体的流数据
     * @return <T> 具体对象
     */
    public static Object decodePayload(Payload payload) {
        try {
            ByteBuffer dataBuffer = payload.getData();
            byte[] dataBytes = new byte[dataBuffer.remaining()];
            dataBuffer.get(dataBytes, dataBuffer.position(), dataBuffer.remaining());
            InputStream dataInputStream = new ByteArrayInputStream(dataBytes);
            ObjectInput in = new ObjectInputStream(dataInputStream);
            return in.readObject();
        } catch (Throwable t) {
            throw Exceptions.propagate(t);
        }
    }

    /**
     * 编码Payload对象
     *
     * @param object 目标对象
     * @return payload对象
     */
    public static Payload convertIntoPayload(Object object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            bos.flush();
            bos.close();
            return DefaultPayload.create(bos.toByteArray());
        } catch (Throwable throwable) {
            throw Exceptions.propagate(throwable);
        }
    }
}
