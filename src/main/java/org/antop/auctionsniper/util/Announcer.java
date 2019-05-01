package org.antop.auctionsniper.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class Announcer<T extends EventListener> {
    private final T proxy;
    private final List<T> listeners = new ArrayList<>();

    public static <T extends EventListener> Announcer<T> to(Class<? extends T> listenerType) {
        return new Announcer<>(listenerType);
    }

    private Announcer(Class<? extends T> listenerType) {
        final Object proxyInstance = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class<?>[]{listenerType}, new AnnouncerInvocationHandler());
        proxy = listenerType.cast(proxyInstance);
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
    }

    public T announce() {
        return proxy;
    }

    private void announce(Method m, Object[] args) {
        try {
            for (T listener : listeners) {
                m.invoke(listener, args);
            }
        } catch (IllegalAccessException e) { // 엑세스할 수 없을 때
            throw new IllegalArgumentException("could not invoke listener", e);
        } catch (InvocationTargetException e) { // invoke 하는 메서드에서 예외 발생 시
            Throwable cause = e.getCause();

            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new UnsupportedOperationException("listener threw exception", cause);
            }
        }
    }

    class AnnouncerInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Announcer.this.announce(method, args);
            return Announcer.this;
        }
    }

}