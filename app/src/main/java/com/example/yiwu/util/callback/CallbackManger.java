package com.example.yiwu.util.callback;

import java.util.WeakHashMap;

public class CallbackManger {
    private static final WeakHashMap<Object,IGlobalCallback> CALLBACKS = new WeakHashMap<>();

    private static class Holder{
        private static final CallbackManger INSTANCE = new CallbackManger();
    }

    public static CallbackManger getInstance(){
        return Holder.INSTANCE;
    }

    public CallbackManger addCallback(Object tag, IGlobalCallback callback){
        CALLBACKS.put(tag,callback);
        return this;
    }

    public IGlobalCallback getCallback(Object tag){
        return CALLBACKS.get(tag);
    }
}
