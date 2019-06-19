package com.smart.car.base;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

/**
 * SharedPreferences的一个工具类，调用setParam就能保存String, Integer, Boolean, Float, Long类型的参数
 * 同样调用getParam就能获取到保存在手机里面的数据
 * @author WFS
 *
 */
public class SharedPreferencesUtils {
	
	/**
	 * 保存在手机里面的文件名
	 */
	private static final String FILE_NAME = "share_data";
	
	
	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 * @param context
	 * @param key
	 * @param object 
	 */
	public static void setParam(Context context , String key, Object object){
		
		String type = object.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		
		if("String".equals(type)){
			editor.putString(key, (String)object);
		}
		else if("Integer".equals(type)){
			editor.putInt(key, (Integer)object);
		}
		else if("Boolean".equals(type)){
			editor.putBoolean(key, (Boolean)object);
		}
		else if("Float".equals(type)){
			editor.putFloat(type, (Float)object);
		}
		else if("Long".equals(type)){
			editor.putLong(type, (Long)object);
		}else if("ArrayList".equals(type)){
			try {
				putObject(context, key, object);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		editor.commit();
	}
	
	
	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 * @param context
	 * @param key
	 * @param defaultObject
	 * @return
	 */
	public static Object getParam(Context context , String key, Object defaultObject){
		String type = defaultObject.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		
		if("String".equals(type)){
			return sp.getString(key, (String)defaultObject);
		}
		else if("Integer".equals(type)){
			return sp.getInt(key, (Integer)defaultObject);
		}
		else if("Boolean".equals(type)){
			return sp.getBoolean(key, (Boolean)defaultObject);
		}
		else if("Float".equals(type)){
			return sp.getFloat(type, (Float)defaultObject);
		}
		else if("Long".equals(type)){
			return sp.getLong(type, (Long)defaultObject);
		}else if("ArrayList".equals(type)){
			try {
				return getObject(context, key,defaultObject);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void clearData(Context context){
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear().commit();
	}
	 /**存储对象*/  
    private static void putObject(Context context, String key, Object obj)  
            throws IOException  
    {  
        if (obj == null) {//判断对象是否为空  
            return;  
        }  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        ObjectOutputStream    oos  = null;  
        oos = new ObjectOutputStream(baos);  
        oos.writeObject(obj);  
        // 将对象放到OutputStream中  
        // 将对象转换成byte数组，并将其进行base64编码  
        String objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));  
        baos.close();  
        oos.close();  
        setParam(context, key, objectStr);
    }  
    /**获取对象
     * @param defaultObject */  
    private static Object getObject(Context context, String key, Object defaultObject)  
            throws IOException, ClassNotFoundException  
    {  
//        String wordBase64 = getString(context, key); 
        String wordBase64 = (String) getParam(context, key, "");
        // 将base64格式字符串还原成byte数组  
        if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException  
            return defaultObject;  
        }  
        byte[]               objBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);  
        ByteArrayInputStream bais     = new ByteArrayInputStream(objBytes);  
        ObjectInputStream    ois      = new ObjectInputStream(bais);  
        // 将byte数组转换成product对象  
        Object obj = ois.readObject();  
        bais.close();  
        ois.close();  
        return obj;  
    }  
}
