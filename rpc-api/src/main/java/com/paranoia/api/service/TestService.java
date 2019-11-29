package com.paranoia.api.service;

import reactor.core.publisher.Mono;

/**
 * @author ZHANGKAI
 * @date 2019/11/29
 * @description
 */
public interface TestService {

     Mono<String> test(String str);
}
