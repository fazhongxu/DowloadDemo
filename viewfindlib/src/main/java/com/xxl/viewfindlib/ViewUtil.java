package com.xxl.viewfindlib;

import android.app.Activity;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v4.net.ConnectivityManagerCompat;
import android.telecom.ConnectionService;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xxl on 2017/10/12.
 */

public class ViewUtil {
    public static void inject(Activity activity) {
        inject(new ViewFinder(activity), activity);
    }

    public static void inject(View view) {
        inject(new ViewFinder(view), view);
    }

    public static void inject(View view, Object object) {
        inject(new ViewFinder(view), object);
    }

    private static void inject(ViewFinder finder, Object object) {
        injectView(finder, object);
        injectEvent(finder, object);
    }

    /**
     * View注入
     *
     * @param finder
     * @param object
     */
    private static void injectView(ViewFinder finder, Object object) {
        //1.反射获取类
        Class<?> clazz = object.getClass();
        //2.获取所有成员变量
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //3.获取成员变量上的注解
            ViewById viewById = field.getAnnotation(ViewById.class);
            //4.获取注解上的属性值
            if (viewById != null) {
                int value = viewById.value();
                //5.通过属性调用finder.findViewById获取View
                View view = finder.findViewById(value);
                if (view != null) {
                    try {
                        //设置私有成员可访问
                        field.setAccessible(true);
                        //6.通过反射注入到类中
                        field.set(object, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * View 事件注入
     *
     * @param finder
     * @param object
     */
    private static void injectEvent(ViewFinder finder, Object object) {
        //1.通过反射获取类
        Class<?> clazz = object.getClass();
        //2.获取所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            //3.获取所有注解
            OnClick onClick = method.getAnnotation(OnClick.class);

            //获取检测网络的注解
            boolean haveNet = method.getAnnotation(NetCheck.class) != null;
            if (onClick != null) {
                //获取注解上的属性值
                int[] values = onClick.value();
                for (int value : values) {
                    View view = finder.findViewById(value);
                    if (view != null) {
                        //设置点击事件
                        view.setOnClickListener(new Listener(method, object,haveNet));
                    }
                }
            }
        }
    }

    private static class Listener implements View.OnClickListener {
        private Method mMethod;
        private Object mObj;

        public Listener(Method method, Object object,boolean havaNetWork) {
            this.mMethod = method;
            this.mObj = object;
        }

        @Override
        public void onClick(View view) {
            if (!netIsAvailable(view.getContext())) {
                Toast.makeText(view.getContext(), "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                return;
            }
            //设置所有方法都能执行，包括公有方法和私有方法
            mMethod.setAccessible(true);
            //反射执行方法
            try {
                mMethod.invoke(mObj, view);//OnClick方法必须携带参数
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                try {
                    mMethod.invoke(mObj, null);//OnClick方法可以不用携带参数
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否有网络
     */
    private static boolean netIsAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }else {
            return false;
        }
    }
}
