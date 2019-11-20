package com.paranoia.api.service;

import com.paranoia.common.bo.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author ZHANGKAI
 * @date 2019/9/18
 * @description
 */
public interface HelloService {

    String sayHi(String name, int age);

    Mono<String> sayHiReactive(String name, int age);

    Flux<Integer> fluxRequest(int num);

    Mono<Person> getPersonInfo(Person person);

    Flux<Person> getPersonInfos(Person person);
}
