package io.github.maseev.alpaca.api.streaming.listener;

import io.github.maseev.alpaca.api.streaming.entity.AccountUpdate;

@FunctionalInterface
public interface AccountUpdateListener extends EventListener<AccountUpdate> {
}
