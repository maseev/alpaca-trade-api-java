package io.github.maseev.alpaca.v1.streaming.listener;

import io.github.maseev.alpaca.v1.streaming.entity.AccountUpdate;

@FunctionalInterface
public interface AccountUpdateListener extends EventListener<AccountUpdate> {
}
