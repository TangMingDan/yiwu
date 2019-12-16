package com.example.yiwu.util.callback;

import androidx.annotation.Nullable;

public interface IGlobalCallback<T> {
    void executeCallback(@Nullable T args);
}
