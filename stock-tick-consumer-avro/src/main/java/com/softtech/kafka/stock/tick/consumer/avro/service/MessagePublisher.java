package com.softtech.kafka.stock.tick.consumer.avro.service;

public interface MessagePublisher {
    void publish(final String message);
}