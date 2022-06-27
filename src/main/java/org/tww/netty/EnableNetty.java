package org.tww.netty;

import java.lang.annotation.*;

/**
 * 组合注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableNettyClient
@EnableNettyServer
public @interface EnableNetty {
}
