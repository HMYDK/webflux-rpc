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
        InputStream dataInputStream = null;
        ObjectInput in = null;
        try {
            ByteBuffer dataBuffer = payload.getData();
            byte[] dataBytes = new byte[dataBuffer.remaining()];
            dataBuffer.get(dataBytes, dataBuffer.position(), dataBuffer.remaining());
            dataInputStream = new ByteArrayInputStream(dataBytes);
            in = new ObjectInputStream(dataInputStream);
            return in.readObject();
        } catch (Throwable t) {
            throw Exceptions.propagate(t);
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 编码Payload对象
     *
     * @param object 目标对象
     * @return payload对象
     */
    public static Payload convertIntoPayload(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            bos.flush();
            return DefaultPayload.create(bos.toByteArray());
        } catch (Throwable throwable) {
            throw Exceptions.propagate(throwable);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
