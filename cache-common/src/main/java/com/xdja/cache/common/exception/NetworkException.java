package com.xdja.cache.common.exception;

/**
 */
public class NetworkException extends Exception{

    public NetworkException(){
        super("网络错误");
    }

    public NetworkException(String msg){
        super(msg);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }


}
