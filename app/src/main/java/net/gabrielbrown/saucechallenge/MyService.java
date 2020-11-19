package net.gabrielbrown.saucechallenge;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import java.lang.reflect.*;

import androidx.annotation.Nullable;

public class MyService extends Service {
    //gets Class object given the class name
    private Class getClass(String className){
        Class localClass = null;
        try {
            localClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Log.i("SauceChallenge","forName failed");
        }
        return localClass;
    }

    private Method getMethodFromClass(String methodName, Class containerClass){
        Method method = null;
        try {
            method = containerClass.getMethod(methodName, null);
        } catch (NoSuchMethodException e) {
            Log.i("SauceChallenge", "getMethod failed "+methodName);
        }
        return method;
    }

    //lists the services running on the device at a given time
    private void listServices(){
        Class localClass = getClass("android.os.ServiceManager");
        Method listServices = getMethodFromClass("listServices", localClass);
        if(listServices != null) {
            Object result = null;
            try {
                result = listServices.invoke(localClass, null);
            } catch (Exception e) {
                Log.i("SauceChallenge", "invocation of listServices failed");
            }
            if(result != null) {
                Log.i("SauceChallenge", "Listing running services:");
                String[] binder = (String[]) result;
                for(String s: binder){
                    Log.i("SauceChallenge", s);
                }
            }
        }
        //Log.i("SauceChallenge", "List Service Called...");
    }

    //returns the interface associated with the batterystats service
    private Object getBatteryStatsInterface() throws NoSuchMethodException {
        //Log.i("SauceChallenge", "Entering invokeCharging...");
        Class serviceManager = getClass("android.os.ServiceManager");
        Method getService = serviceManager.getMethod("getService", new Class[] {String.class});
        //Method getService = getMethodFromClass("getService", serviceManager);
        IBinder batterystats = null;
        if(getService != null) {
            try {
                batterystats = (IBinder) getService.invoke(serviceManager, "batterystats");
            } catch (Exception e) {
                Log.i("SauceChallenge", "invocation of getService failed");
            }
        }
        Class stub = getClass("com.android.internal.app.IBatteryStats$Stub");
        Method asInterface = stub.getMethod("asInterface", new Class[] {IBinder.class});
        //Method asInterface = getMethodFromClass("asInterface", batteryStats);
        Object bstatsInterface = null;
        if(batterystats != null){
            try {
                bstatsInterface = asInterface.invoke(stub, batterystats);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return bstatsInterface;
    }

    //takes batterystats interface and logs output of isCharging function
    private void isBatteryConnected(Object batteryStats) throws InvocationTargetException, IllegalAccessException {
        Class c = batteryStats.getClass();
        Method isCharging = getMethodFromClass("isCharging", c);
        if (isCharging != null ){
            boolean charging = (boolean) isCharging.invoke(batteryStats, null);
            Log.i("SauceChallenge", "isCharging = "+charging);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SauceChallenge", "Starting service...");
        listServices();
        Object batteryStatsInterface = null;
        try {
            batteryStatsInterface = getBatteryStatsInterface();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            isBatteryConnected(batteryStatsInterface);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        stopSelf();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("SauceChallenge", "Stopping Service...");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}