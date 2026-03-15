package com.myapp.backend.service;

import java.util.function.Consumer;

public interface PubSubService {

    void publish(String channel, String message);

    void subscribe(String channel, Consumer<String> listener);

    void unsubscribeAll();
}
