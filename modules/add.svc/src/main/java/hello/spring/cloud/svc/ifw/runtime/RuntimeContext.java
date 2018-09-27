package hello.spring.cloud.svc.ifw.runtime;

import java.util.HashMap;

public class RuntimeContext {
    private Object[] inputs;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    private Object result;

    public Object[] getInputs() {
        return inputs;
    }

    public void setInputs(Object[] inputs) {
        this.inputs = inputs;
    }

    private HashMap<String, Object> ctxSlots = new HashMap<String, Object>();

    public void putContext(String key, Object ctx) {
        this.ctxSlots.put(key, ctx);
    }

    public Object getContext(String key) {
        return ctxSlots.get(key);
    }

    public ServiceRuntimeException getFault() {
        return fault;
    }

    public void setFault(ServiceRuntimeException fault) {
        this.fault = fault;
    }

    private ServiceRuntimeException fault;

    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    private Object targetObject;



    public RuntimeContext() {
    }
}
