package retrofit2;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.Request;

/**
 * Created by ldy on 2017/9/4.
 *
 * 通过反射hook{@link OkHttpCall}
 */

public class HookOkHttpCall {
    private final OkHttpCall okHttpCall;
    private final ServiceMethod serviceMethod;
    private final Object[] args;
    private final Class<? extends OkHttpCall> okHttpCallClass;

    public HookOkHttpCall(OkHttpCall okHttpCall) throws Exception {
        this.okHttpCall = okHttpCall;

        okHttpCallClass = okHttpCall.getClass();
        Field serviceMethodFiled = okHttpCallClass.getDeclaredField("serviceMethod");
        serviceMethod = (ServiceMethod) serviceMethodFiled.get(okHttpCall);

        Field argsFiled = okHttpCallClass.getDeclaredField("args");
        args = (Object[]) argsFiled.get(okHttpCall);
    }

    /**
     * 获取原始的request请求，参考{@link OkHttpCall#createRawCall()}
     */
    @Nullable
    public Request getRequest() {
        try {
            return serviceMethod.toRequest(args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 根据传入的request设置{@link OkHttpCall#rawCall}字段，参考{@link OkHttpCall#createRawCall()}
     * @throws ReflectiveOperationException 反射调用异常
     */
    public void hookRequest(Request request) throws ReflectiveOperationException {
        if (request==null){
            throw new NullPointerException("request must be not null");
        }

        okhttp3.Call call = serviceMethod.callFactory.newCall(request);
        if (call == null) {
            throw new NullPointerException("Call.Factory returned null.");
        }
        Field rawCallField = okHttpCallClass.getDeclaredField("rawCall");
        rawCallField.set(okHttpCall,call);
    }
}
