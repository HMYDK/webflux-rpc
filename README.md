## Architecture



[RsocketProtocol](https://github.com/rsocket/rsocket/blob/master/Protocol.md)


## TODO


- [x] 本地注册中心
- [x] 服务注册本地时，扫描包路径的管理
- [x] 服务的调用支持注解
- [x] 支持对象类型的传递，解决socket双向序列化对象字节丢失的问题，只支持默认的Java序列化方式
- [x] 异常传递，组件本身不对异常做任何处理
- [x] 抽象TCP客户端/服务端的创建以及端口的管理
- [ ] 调用超时处理  DefaultPayload.create可以传递元信息
> Metadata Length: (24 bits = max value 16,777,215) Unsigned 24-bit integer representing the length of Metadata in bytes. Excluding Metadata Length field.
- [ ] ~~server端服务调用时，实例的查找与创建的优化（@Service）~~
- [ ] server端需要支持 rpc server class 实例的二次创建并缓存 : System.exit necessary ?
- [x] pom exclude spring context
- [x] TcpUtils -> check host and port effective
- [ ] 服务链路中filter热插拔，用以支持性能监控、业务定制等
- [ ] 补充Rsocket的健康检查（RSocket.availability）
- [ ] 性能测试





## HIT POINT

- channel链接超时时间3秒
- Frame => Stream ID: 0 <br/>
            Type: SETUP <br/>
            Version: 1.0 <br/>
            keep-alive interval: 20000 <br/> 
            max lifetime: 90000  <br/>
            metadata mime type: application/binary  <br/>
            data mime type: application/binary  <br/>
            Payload:  <br/>