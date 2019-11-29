package com.paranoia.rpc.service;

import com.paranoia.api.service.TestService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author ZHANGKAI
 * @date 2019/11/29
 * @description
 */
@Service
public class TestServiceImpl implements TestService {
    @Override
    public Mono<String> test(String str) {
        return Mono.just("test : " + str);
    }
}
