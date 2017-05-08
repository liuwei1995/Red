package com.liuwei1995.red.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/***
 * 
 * @author liuwei
 * 编写时间: 2015年9月7日下午5:05:30
 * 类说明 : 如果里面含有Date格式的属性  一定要加注解   InjectDate
 */
public class UserJSON {
	/***
	 * json 解析成实体
	 * @param cls
	 *            类.class
	 * @param jsonObject
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws JSONException
	 */
	public static final <T> T parseObject(Class<T> clazz,JSONObject jsonObject) {
		T t = instantiationClass(clazz);
		if(jsonObject==null)return t;
		Field[] declaredFields = getDeclaredFields(t, jsonObject);
		t = setObject(jsonObject, t, declaredFields);
		return (T) t;
	  }
	/***
	 * json 解析成实体
	 * @param cls
	 *            类.class
	 * @param jsonObject
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws JSONException
	 */
	public static final <T> T parseObject(Class<T> clazz,JSONObject jsonObject,String key) {
		T t = instantiationClass(clazz);
		if(jsonObject==null)return t;
		try {
			JSONObject json = jsonObject.getJSONObject(key);
			Field[] declaredFields = getDeclaredFields(t, json);
			t = setObject(json, t, declaredFields);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return (T) t;
	}
	
	public static String getString(JSONObject jsonObject,String key) {
		String string = null;
		try {
			string = jsonObject.getString(key);
		} catch (JSONException e) {
			string = "";
			e.printStackTrace();
		}
		return string;
	}
	public static int getInt(JSONObject jsonObject,String key) {
		int i = 0;
		try {
			i = jsonObject.getInt(key);
		} catch (JSONException e) {
			i = 0;
			e.printStackTrace();
		}
		return i;
	}
	public static JSONObject getJSONObject(String s) {
		JSONObject object = null;
		try {
			if(s==null)return new JSONObject();
			object = new JSONObject(s);
		} catch (JSONException e) {
			object = new JSONObject();
			e.printStackTrace();
		}
		return object;
	}
	public static JSONObject getJSONObject(JSONObject jsonObject,String key) {
		JSONObject obj = null;
		try {
			obj = jsonObject.getJSONObject(key);
		} catch (JSONException e) {
			obj = new JSONObject();
			e.printStackTrace();
		}
		return obj;
	}
	public static JSONArray getJSONArray(String s) {
		JSONArray array = null;
		try {
			 array = new JSONArray(s);
		} catch (JSONException e) {
			array = new JSONArray();
			e.printStackTrace();
		}
		return array;
	}
	public static JSONArray getJSONArray(JSONObject jsonObject ,String key) {
		JSONArray array = null;
		try {
			if(jsonObject==null) return new JSONArray();
			return jsonObject.getJSONArray(key);
		} catch (JSONException e) {
			array = new JSONArray();
			e.printStackTrace();
		}
		return array;
	}
	public static JSONArray getJSONArray(JSONArray jsonArray ,int index) {
		JSONArray array = null;
		try {
			if(jsonArray==null) return new JSONArray();
			return jsonArray.getJSONArray(index);
		} catch (JSONException e) {
			array = new JSONArray();
		}
		return array;
	}
	public static JSONObject getJSONObject(JSONArray Array,int index) {
		JSONObject jsonObject = null;
		try {
			jsonObject = Array.getJSONObject(index);
		} catch (JSONException e) {
			jsonObject = new  JSONObject();
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static <T> List<T> parsUser(Class<T> clazz,JSONArray jsonArray){
		List<Object> list = new ArrayList<Object>();
		if(jsonArray == null)return (List<T>) list;
		try {
			for (int j = 0; j < jsonArray.length(); j++) {
				Object object1 = jsonArray.get(j);
				if (object1 instanceof JSONObject) {
					JSONObject jsonObject = (JSONObject) object1;
				Object obj = instantiationClass(clazz);
				Field[] declaredFields = getDeclaredFields(obj,jsonObject);
					obj = setObject(jsonObject, obj, declaredFields);
					list.add(obj);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return  (List<T>) list;
	}
	/**
	 * 过滤没有用的方法
	 * @param <T>
	 * @param obj
	 * @param jsonObject
	 * @return
	 */
	public static <T> Field[] getDeclaredFields(T obj,JSONObject jsonObject) {
		Field[] fields = obj.getClass().getDeclaredFields();
		List<Field> result = new ArrayList<Field>();
		for (Field field : fields) {
			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if ("serialVersionUID".equals(field.getName())) {// ingore
				continue;
			}
			String namename = field.getName();
			// AccessibleTest类中的成员变量为private,故必须进行此操作
			boolean null1 = jsonObject.isNull(namename);
			boolean has = jsonObject.has(namename);
			if (null1) {
				continue;
			}
			if (!has) {
				continue;
			}
			result.add(field);
		}
		return result.toArray(new Field[0]);
	}
	/**
	 * 通过key  获得集合
	 * @param clazz
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	public static <T> List<T> parsUser(Class<T> clazz,JSONObject jsonObject,String key){
		try {
			JSONArray array = jsonObject.getJSONArray(key);
			if (array == null || array.length() == 0) {
				ArrayList<T> list = new ArrayList<T>();
				return (List<T>) list;
			}else {
				return parsUser(clazz, array);
			}
		} catch (JSONException e) {
			ArrayList<T> list = new ArrayList<T>();
			e.printStackTrace();
			return  list;
		}
	}
	/***
	 * 获得分页条数
	 * @param jsonObject
	 * @param page
	 * @return
	 */
	public static Map<String, Object> getPage(JSONObject jsonObject,String page) {
//		JSONArray names = jsonObject.names();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JSONObject object = jsonObject.getJSONObject(page);
			Iterator<String> keys = object.keys();
			while (keys.hasNext()) {
				String next = keys.next();
				map.put(next, object.getString(next));
			}
		} catch (JSONException e) {
			map.put("pageIndex", "1");
			map.put("pageCount", "10");
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * String  转换为实体
	 * @param <T>
	 * @param cls
	 * @param entitystring
	 * @return
	 */
	public static <T> T getEntity(Class<T> clazz, String entitystring) {
		T obj = null;
		try {
			obj = instantiationClass(clazz);
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			int indexOf = entitystring.indexOf("[");
			String indexOfname = entitystring.substring(0, entitystring.indexOf("[")).trim();//
			String string = obj.getClass().getName().toString().trim();
			CharSequence subSequencename = string.subSequence((string.lastIndexOf(".")+1), string.length()).toString().trim();
			if (!indexOfname.equals(subSequencename)) {
				return  obj;
			}
			String data = entitystring.substring(indexOf, entitystring.length()).trim();
			String replace = data.replace("[","").replace("]","");
			String[] split = replace.split(",");
			Map<Object, String> map = new HashMap<Object, String>();
			for (int i = 0; i < split.length; i++) {
				String[] split2 = split[i].split("=");
				String key = split2[0].trim();
				String value = split2[1];
				map.put(key, value);
			}
			
			for (int i = 0; i < declaredFields.length; i++) {
				String namename = declaredFields[i].getName();
				if (!map.containsKey(namename)) {
					continue;
				}
				declaredFields[i].setAccessible(true);
				// 判断类型
				Class<?> type = declaredFields[i].getType();
				// 获取字段类型
				String typeName = type.getName();
				// 对字段进行赋值 第一个参数为对象引用第二个参数为要附的值
				// 如果为字符串类型
				String dataname = map.get(namename);
				setAssignment(obj, declaredFields, i, namename, type,
						dataname);
				declaredFields[i].setAccessible(false);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return  obj;
	}
/**
 * 赋值
 * @param obj
 * @param declaredFields
 * @param i
 * @param namename
 * @param typeName
 * @param dataname
 * @throws IllegalAccessException
 * @throws JSONException
 */
	private static <T> void setAssignment(T obj, Field[] declaredFields, int i,
			String namename, Class<?> type, String dataname)
			throws IllegalAccessException, JSONException {
		if (type == String.class) {
			declaredFields[i].set(obj, dataname);
		} else if (type == int.class || type == Integer.class) {
			declaredFields[i].set(obj,Integer.parseInt(dataname));
		} else if (type == Long.class || type == long.class) {
			declaredFields[i].set(obj,Long.parseLong(dataname));
		} else if (type == double.class || type == Double.class) {
			declaredFields[i].set(obj,Double.parseDouble(dataname));
		}else if (Float.class == type || type == float.class) {
			declaredFields[i].set(obj,Float.parseFloat(dataname));
		} else if (type == Byte.class || type == byte.class) {
			declaredFields[i].set(obj,Byte.parseByte(dataname));
		} else if (Short.class == type || type == short.class) {
			declaredFields[i].set(obj,Short.parseShort(dataname));
		}else if (Boolean.class == type || boolean.class == type) {
			declaredFields[i].set(obj,Boolean.valueOf(namename));
		}else if (java.sql.Date.class == type || Date.class == type) {// 如果为日期类型java.sql.Date
			T setDate = setDate(obj, dataname);
			declaredFields[i].set(obj,setDate);
		}else if (Object.class == type) {
			declaredFields[i].set(obj,dataname);
		}else if (JSONObject.class == type) {
			JSONObject jsonObject = getJSONObject(dataname);
			declaredFields[i].set(obj, jsonObject);
		}else if (JSONArray.class == type) {
			JSONArray jsonArray = getJSONArray(dataname);
			declaredFields[i].set(obj, jsonArray);
		}
	}
	/**
	 * 给Obj赋值
	 * @param <T>
	 * @param jsonObject
	 * @param obj
	 * @param declaredFields
	 * @return
	 */
	private static <T> T setObject(JSONObject jsonObject, T obj,
			Field[] declaredFields){
		try {
			for (int i = 0; i < declaredFields.length; i++) {
				String namename = declaredFields[i].getName();
				declaredFields[i].setAccessible(true);
				// 判断类型
				Class<?> type = declaredFields[i].getType();
				// 获取字段类型
				// 对字段进行赋值 第一个参数为对象引用第二个参数为要附的值
				// 如果为字符串类型
				String dataname = jsonObject.get(namename).toString();
				try {
					setAssignment(obj, declaredFields, i, namename, type, dataname);
				} catch (Exception e) {
					e.printStackTrace();continue;
				}
				declaredFields[i].setAccessible(false);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	/**
	 * 实例化Class 
	 * @param clazz
	 * @return
	 */
	public static <T> T instantiationClass(Class<T> clazz) {
		T obj = null;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}
	/**
	 * 获取Class里面的属性名和属性值
	 * @param entitycls   实体类
	 * 
	 * @return  Map<属性名, 属性值> 
	 * Get Class field and value Map
	 */
	public static Map<String, String> getClassInfo(Object entitycls) {
		Map<String, String> fieldsAndValues = new HashMap<String, String>();
		Field[] fields = entitycls.getClass().getDeclaredFields();
		try {
			for (Field ff : fields) {
			    ff.setAccessible(true);
			    Object object = ff.get(entitycls);
			    if (object != null) { //判断字段是否为空，并且对象属性中的基本都会转为对象类型来判断
//			    	String value = getFieldValue(cls, ff.getName()).toString();
//					fieldsAndValues.put(ff.getName(), value);
					fieldsAndValues.put(ff.getName(), object.toString());
			    }else {
			    	fieldsAndValues.put(ff.getName(), "");
				}
			    ff.setAccessible(false);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return fieldsAndValues;
	}
	@SuppressWarnings("unchecked")
	public static <T> T getT(Class<T> clazz, JSONObject jsonObject,String key) {
		try {
			if ("String".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) "";
				return (T) jsonObject.getString(key);
			}
			else if ("int".equals(clazz.getSimpleName())||"Integer".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) (Integer.valueOf("0"));
				return (T) Integer.valueOf(jsonObject.getString(key));
			}
			else if ("double".equals(clazz.getSimpleName())||"Double".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) (Double.valueOf("0"));
				return (T) Double.valueOf(jsonObject.getString(key));
			}
			else if ("long".equals(clazz.getSimpleName())||"Long".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) (Long.valueOf("0"));
				return (T) Long.valueOf(jsonObject.getString(key));
			}
			else if ("boolean".equals(clazz.getSimpleName())||"Boolean".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) (Boolean.valueOf("false"));
				return (T) Double.valueOf(jsonObject.getString(key));
			}
			else if ("float".equals(clazz.getSimpleName())||"Float".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) (Float.valueOf("0"));
				return (T) Float.valueOf(jsonObject.getString(key));
			}
			else if ("short".equals(clazz.getSimpleName())||"Short".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) (Short.valueOf("0"));
				return (T) Short.valueOf(jsonObject.getString(key));
			}
			else if ("byte".equals(clazz.getSimpleName())||"Byte".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) (Byte.valueOf("0"));
				return (T) Byte.valueOf(jsonObject.getString(key));
			}
			else if ("Object".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) "";
				return (T) jsonObject.getString(key);
			}
			else if ("Date".equals(clazz.getSimpleName())) {
				if(!jsonObject.has(key))return (T) new Date();
				Object object = jsonObject.get(key);
				return (T) setDate(clazz, object.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null ;
	}

	private static <T> T setDate(T clazz, String object) throws JSONException {
		SimpleDateFormat format;
		boolean present = clazz.getClass().isAnnotationPresent(InjectDate.class);
		if (present) {
			InjectDate injectDate = clazz.getClass().getAnnotation(InjectDate.class);
			String inDate = injectDate.inDate();
			 format = new SimpleDateFormat(inDate);
		}else {
			 format = new SimpleDateFormat("yyyy");
		}
		Date parse = null;
		try {
			format.parse(object.toString());
		} catch (ParseException e1) {
			parse = new Date();
			e1.printStackTrace();
		}
		return (T) parse;
	}
	// 表示用在字段上
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	// 表示在生命周期是运行时
	public @interface InjectDate{
		/**yyyy-MM-dd HH:mm:ss*/
		public String inDate() default "";
	}
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface InjectEntity{
		public Class inEntity() default InjectEntity.class;
	}
	/***
	 * 
	 * @param <E>
	 * @param clazz
	 * @param jsonObject
	 * @param is  如果存在集合字段的属性名  true 就一次解析   如果false  就不解析  
	 * @return
	 */
	public static final <T, E> T parseEntity(Class<T> clazz,JSONObject jsonObject,boolean is) {
		T obj = instantiationClass(clazz);
		if(jsonObject==null)return obj;
		Field[] declaredFields = getDeclaredFields(obj, jsonObject);
			for (Field field : declaredFields) {
				String name = field.getName();//totalPrice
				Class<?> type = field.getType();
				field.setAccessible(true);
				try {
					Object object = jsonObject.get(name);
					if (type == String.class) {
						field.set(obj, object.toString());
					} else if (type == int.class || type == Integer.class) {
						field.set(obj,Integer.parseInt(object.toString()));
					} else if (type == Long.class || type == long.class) {
						field.set(obj,Long.parseLong(object.toString()));
					} else if (type == double.class || type == Double.class) {
						field.set(obj,Double.parseDouble(object.toString()));
					}else if (Float.class == type || type == float.class) {
						field.set(obj,Float.parseFloat(object.toString()));
					} else if (type == Byte.class || type == byte.class) {
						field.set(obj,Byte.parseByte(object.toString()));
					} else if (Short.class == type || type == short.class) {
						field.set(obj,Short.parseShort(object.toString()));
					}else if (Boolean.class == type || boolean.class == type) {
						field.set(obj,Boolean.valueOf(object.toString()));
					}else if (type == Date.class || type == java.sql.Date.class) {
						if(object == null || object instanceof java.sql.Date || (object instanceof Date && type == Date.class)){
							field.set(obj,object);
						}else if (object instanceof Date && type == java.sql.Date.class) {
							Date d = (Date) object;
							field.set(obj,new java.sql.Date(d.getTime()));
						}else if(object instanceof String || object instanceof Long){
							Date parse = null;
							try {
								parse = new Date(Long.valueOf(object.toString()));
							} catch (Exception e2) {
								e2.printStackTrace();
								try {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									parse = sdf.parse(object.toString());
								} catch (ParseException e) {
									e.printStackTrace();
									try {
										parse = DateFormat.getInstance().parse(object.toString());
									} catch (ParseException e1) {
										e1.printStackTrace();
									}
								}
							}
							if(parse != null){
								if(type == Date.class){
									field.set(obj,parse);
								}else if (type == java.sql.Date.class) {
									field.set(obj,new java.sql.Date(parse.getTime()));
								}
							}
							
						}
					}
					else if (Object.class == type) {
						field.set(obj,jsonObject.getString(name));
					}else if (JSONObject.class == type) {
						field.set(obj, getJSONObject(jsonObject.getString(name)));
					}else if (JSONArray.class == type) {
						JSONArray jsonArray = getJSONArray(jsonObject.getString(name));
						field.set(obj, jsonArray);
					}else if (is) {
						if(field.isAnnotationPresent(InjectEntity.class)){
							if(type == List.class){//如果是集合类型
								Type genericType = field.getGenericType();
								if(genericType instanceof ParameterizedType){
									ParameterizedType tt = (ParameterizedType) genericType;
									Type[] actualTypeArguments = tt.getActualTypeArguments();
									if(actualTypeArguments == null || actualTypeArguments.length == 0)continue;
									Type type2 = actualTypeArguments[0];
									try {
										if(object instanceof JSONArray){
											JSONArray jsonArray = new JSONArray(object.toString());
											List<?> list = toList(type2, jsonArray);
											if(list != null){
												field.set(obj, list);
											}else {
												String string = type2.toString().replace("class", "").trim();
												Class<?> forName = Class.forName(string);
												field.set(obj, parsUser(forName, jsonArray));
											}
										}else if (object instanceof List) {
											List<?> list = toList(type2, (List)object);
											if(list != null){
												field.set(obj, list);
											}
										}
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
										continue;
									}
								}
							}else if (type == Map.class) {
								
							}else if (type == List[].class) {
								
							}else if (type == Map[].class) {
								
							}else if (type == String[].class) {
								
							}else if (type == Integer[].class) {
								
							}else {
								Class<?> t = field.getType();
								if(object instanceof JSONObject){
									Object parseEntity = parseEntity(t, (JSONObject)object, is);
									field.set(obj, parseEntity);
								}
							}
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();continue;
				} catch (IllegalAccessException e) {
					e.printStackTrace();continue;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();continue;
				} catch (JSONException e) {
					e.printStackTrace();continue;
				}
			}
		return obj;
	  }
	/***
	 * 
	 * @param clazz
	 * @param jsonArray
	 * @param is    如果存在集合字段的属性名  true 就一次解析   如果false  就不解析  
	 * @return
	 */
	public static <T> List<T> parsUser(Class<T> clazz,JSONArray jsonArray,boolean is){
		List<Object> list = new ArrayList<Object>();
		try {
			for (int j = 0; j < jsonArray.length(); j++) {
				Object object1 = jsonArray.get(j);
				if (object1 instanceof JSONObject) {
					JSONObject jsonObject = (JSONObject) object1;
					T parseObject = parseEntity(clazz, jsonObject, is);
					list.add(parseObject);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return  (List<T>) list;
	}
	public static <T> List<T> parsUser(Class<T> clazz,JSONObject jsonObject,String key,boolean is){
		if(jsonObject == null || !jsonObject.has(key))return new ArrayList<T>();
		try {
			Object object = jsonObject.get(key);
			if(object instanceof JSONArray){
				return parsUser(clazz, new JSONArray(object.toString()), is);
			}else if (object instanceof JSONObject) {
				ArrayList<T> list = new ArrayList<T>();
				T parseObject = parseEntity(clazz, new JSONObject(object.toString()), is);
				list.add(parseObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}
	public static List<?> toList(Type type,JSONArray jsonArray) throws JSONException {
		if (type == String.class) {
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Object jsonArrayObject = jsonArray.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(jsonArrayObject.toString());
			}
			return list;
		}else if(type == Integer.class || type == int.class)
		{
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Object jsonArrayObject = jsonArray.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Integer.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (type == Double.class || type == double.class) {
			List<Double> list = new ArrayList<Double>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Object jsonArrayObject = jsonArray.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Double.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (type == Long.class || type == long.class) {
			List<Long> list = new ArrayList<Long>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Object jsonArrayObject = jsonArray.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Long.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (type == Short.class || type == short.class) {
			List<Short> list = new ArrayList<Short>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Object jsonArrayObject = jsonArray.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Short.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (type == Object.class) {
			List<Object> list = new ArrayList<Object>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				list.add(jsonArray.get(i));
			}
			return list;
		}else if (type == byte.class || type == Byte.class) {
			List<Byte> list = new ArrayList<Byte>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Object jsonArrayObject = jsonArray.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Byte.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (Date.class == type || java.sql.Date.class == type) {
			List<Date> list = new ArrayList<Date>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Object jsonArrayObject = jsonArray.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				try {
					Date parse = DateFormat.getInstance().parse(jsonArrayObject.toString());
					list.add(parse);
				} catch (ParseException e) {
					e.printStackTrace();break;
				}
			}
			return list;
		}else if (Boolean.class == type || boolean.class == type) {
			List<Boolean> list = new ArrayList<Boolean>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				Object jsonArrayObject = jsonArray.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Boolean.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}
		return null;
	}
	
	public static List<?> toList(Type type,List<?> list_) throws JSONException {
		if(type == null)return null;
		if (type == String.class) {
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(jsonArrayObject.toString());
			}
			return list;
		}else if(type == Integer.class || type == int.class)
		{
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Integer.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (type == Double.class || type == double.class) {
			List<Double> list = new ArrayList<Double>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Double.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (type == Long.class || type == long.class) {
			List<Long> list = new ArrayList<Long>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Long.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (type == Short.class || type == short.class) {
			List<Short> list = new ArrayList<Short>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Short.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (type == Object.class) {
			List<Object> list = new ArrayList<Object>();
			for (int i = 0; i < list_.size(); i++)
			{
				list.add(list_.get(i));
			}
			return list;
		}else if (type instanceof List || type == List.class) {
			List<List> list = new ArrayList<List>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject) || !(jsonArrayObject instanceof List))break;
				list.add((List)jsonArrayObject);
			}
			return list;
		}else if (type instanceof Map || type == Map.class) {
			List<Map> list = new ArrayList<Map>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject) || !(jsonArrayObject instanceof Map))break;
				list.add((Map)jsonArrayObject);
			}
			return list;
		}else if (type == byte.class || type == Byte.class) {
			List<Byte> list = new ArrayList<Byte>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Byte.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else if (Date.class == type || java.sql.Date.class == type) {
			List<Date> list = new ArrayList<Date>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				try {
					Date parse = DateFormat.getInstance().parse(jsonArrayObject.toString());
					list.add(parse);
				} catch (ParseException e) {
					e.printStackTrace();break;
				}
			}
			return list;
		}else if (Boolean.class == type || boolean.class == type) {
			List<Boolean> list = new ArrayList<Boolean>();
			for (int i = 0; i < list_.size(); i++)
			{
				Object jsonArrayObject = list_.get(i);
				if((jsonArrayObject instanceof JSONArray) || (jsonArrayObject instanceof JSONObject))break;
				list.add(Boolean.valueOf(jsonArrayObject.toString()));
			}
			return list;
		}else {
			Type rawType = getRawType(type);
			if(rawType == null)return null;
			return toList(rawType, list_);
		}
	}
	
	public static Type getRawType(Type genericType) {
//		Type genericType = field.getGenericType();
		if(genericType instanceof ParameterizedType){
			ParameterizedType tt = (ParameterizedType) genericType;
			Type[] actualTypeArguments = tt.getActualTypeArguments();
			if(actualTypeArguments == null || actualTypeArguments.length == 0)return null;
			Type rawType = tt.getRawType();
			return rawType;
		}
		return null;
	}

	
	
	 private static Class<?> getGenericClass(ParameterizedType parameterizedType, int i) {     
	        Object genericClass = parameterizedType.getActualTypeArguments()[i];     
	        if (genericClass instanceof ParameterizedType) { // 处理多级泛型     
	            return (Class<?>) ((ParameterizedType) genericClass).getRawType();     
	        } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型     
	            return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();     
	        } else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象     
	            return (Class<?>) getClass(((TypeVariable<?>) genericClass).getBounds()[0], 0);     
	        } else {     
	            return (Class<?>) genericClass;     
	        }     
	    }
	    private static Class<?> getClass(Type type, int i) {     
	        if (type instanceof ParameterizedType) { // 处理泛型类型     
	            return getGenericClass((ParameterizedType) type, i);     
	        } else if (type instanceof TypeVariable) {     
	            return (Class<?>) getClass(((TypeVariable<?>) type).getBounds()[0], 0); // 处理泛型擦拭对象     
	        } else {// class本身也是type，强制转型     
	            return (Class<?>) type;     
	        }     
	    }
	/**
	 * 从asset路径下读取对应文件转String输出
	 * @param mContext
	 * @return
	 */
	public static String getJson(Context mContext, String fileName) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		AssetManager am = mContext.getAssets();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					am.open(fileName)));
			String next = "";
			while (null != (next = br.readLine())) {
				sb.append(next);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sb.delete(0, sb.length());
		}
		return sb.toString().trim();
	}


}
