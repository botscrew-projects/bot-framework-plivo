package com.botscrew.botframework.plivo.config.property;

/**
 * Abstraction for executor properties
 * Describes 4 properties:
 *  - core pool size
 *  - max pool size
 *  - queue capacity
 *  - keep alive in seconds
 *
 *  @see HandlerTaskExecutorProperties
 *  @see SenderTaskExecutorProperties
 */
public interface ExecutorProperties {

    Integer getCorePoolSize();

    Integer getMaxPoolSize();

    Integer getQueueCapacity();

    Integer getKeepAliveSeconds();
}
