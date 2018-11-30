package com.chailease.tw.app.android.json.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class GsonTypeAdapter<T> extends TypeAdapter<T> {

    protected Map<String, String> renames;	// = new HashMap<String, String>();
    protected Map<String, Field> serializeNames;	// = new HashMap<String, Field>();
    protected Map<String, Field> deserializeNames;	// = new HashMap<String, Field>();
    protected Set<String> disabledFields;

	@Override
	public T read(JsonReader in) throws IOException {
		T inst = newTypeInstance();
		if (null == deserializeNames) {
			deserializeNames = loadFieldsMap(LOAD_MODE.DESERIALIZE, inst.getClass());
		}

        Gson gson = new Gson();
		in.beginObject();
		while (in.hasNext()) {
			JsonToken jt = in.peek();
			//System.out.println("JsonToken for name >>" + jt);
			String field = in.nextName();
			//System.out.println("for name >>" + field);
			if (deserializeNames.containsKey(field)) {
				Field f = deserializeNames.get(field);
				Object v = readValue(in);
				try {
					if (null != v) {
						if (!f.isAccessible()) f.setAccessible(true);
						if (v instanceof String) {
							f.set(inst, v);
						} else if (v instanceof Integer) {
							f.setInt(inst, ((Integer)v).intValue());
						} else if (v instanceof Boolean) {
							f.setBoolean(inst, ((Boolean)v).booleanValue());
						} else if (v instanceof Double) {
							f.setDouble(inst, ((Double)v).doubleValue());
						} else if (v instanceof Long) {
							f.setLong(inst, ((Long)v).longValue());
						} else {
							f.set(inst, gson.fromJson("", f.getType()));
						}
					} else {
						v = readObject(inst.getClass(), field, in);
						if (!f.isAccessible()) f.setAccessible(true);
						f.set(inst, v);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		in.endObject();
		return inst;
	}

	@Override
	public void write(JsonWriter out, T value) throws IOException {
		out.beginObject();
		if (null == serializeNames) {
	        serializeNames = loadFieldsMap(LOAD_MODE.SERIALIZE, value.getClass());
		}

		//System.out.println("serializeNames >> \n" + serializeNames);
        String[] serializes = serializeNames.keySet().toArray(new String[serializeNames.size()]);
        GsonUtils gson = GsonUtils.genGsonUtils(GsonUtils.EXPOSE_STRATEGY_SERIALIZE, null);
        for (String serialize : serializes) {
        	Field f = serializeNames.get(serialize);
        	if (!f.isAccessible()) f.setAccessible(true);
        	@SuppressWarnings("rawtypes")
			Class fc = f.getType();
        	try {
            	if (String.class == fc) {
    				out.name(serialize).value((String)f.get(value));
            	} else if (Integer.class == fc) {
                	out.name(serialize).value(f.getInt(value));
            	} else if (Boolean.class == fc) {
                	out.name(serialize).value(f.getBoolean(value));
            	} else if (Double.class == fc) {
                	out.name(serialize).value(f.getDouble(value));
            	} else if (Long.class == fc) {
                	out.name(serialize).value(f.getLong(value));
            	} else {
            		Object v = f.get(value);
            		if (null == v) {
            			out.name(serialize).nullValue();
            		} else {
                		out.name(serialize).jsonValue(gson.toJsonString(v));
            		}
            	}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
        }
		out.endObject();	
	}

	enum LOAD_MODE {
		SERIALIZE, DESERIALIZE
	}
	boolean excludeConts = false;

	protected Map<String, Field> loadFieldsMap(LOAD_MODE mode, @SuppressWarnings("rawtypes") Class tClass) {
        if (null == renames) {
            renames = new HashMap<String, String>();
            disabledFields = new HashSet<String>();
    		Annotation[] tAnns = tClass.getAnnotations();
    		if (null != tAnns && tAnns.length > 0) {
    			for (Annotation tAnn : tAnns) {
    				if (tAnn instanceof OverwriteSuper) {
    					OverwriteSuper os = (OverwriteSuper) tAnn;
    					String[] renameFields = os.renameField();
    					String[] renameTo = os.renameTo();
    					String[] disabled = os.disableSerialize();
					    excludeConts = os.excludeConts();
    					if (null != renameFields && null != renameTo 
    							&& renameFields.length == renameTo.length) {
    						for (int i=0; i<renameFields.length; i++) {
    							if (!renames.containsKey(renameFields[i]))
    								renames.put(renameFields[i], renameTo[i]);
    						}
    					}
    					if (null != disabled && disabled.length > 0) {
    						for (String df : disabled) {
    							disabledFields.add(df);
    						}
    					}
    					break;
    				}
    			}
    		}
        }
        HashMap<String, Field> serializeNames = new HashMap<String, Field>();
        //Field[] fields = tClass.getFields();
        Field[] fields = loadFields(tClass);
        for (Field f : fields) {
        	if (disabledFields.contains(f.getName())) continue;
	        if (excludeConts && Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())) continue;
			Annotation[] anns = f.getAnnotations();
        	boolean skip = false;
        	String serializeName = f.getName();
        	String[] dns = null;
			if (null != anns && anns.length>0) {
				ANNS:
				for (Annotation ann : anns) {
					if (ann instanceof Expose) {
						Expose e = (Expose) ann;
						switch (mode) {
						case SERIALIZE:
							if(!e.serialize()) {
								skip = true;
								break ANNS;
							}
							break;
						case DESERIALIZE:
							if(!e.deserialize()) {
								skip = true;
								break ANNS;
							}
							break;
						}
					} else if (ann instanceof SerializedName) {
						SerializedName rn = (SerializedName) ann;
						dns = rn.alternate();
						String serializedName = rn.value();
						if (null != serializedName && !"".equals(serializedName.trim())) {
							serializeName = serializedName.trim();
						}
					}
				}
				if (skip) continue;
			}
			serializeNames.put(serializeName, f);
			if (null != dns && dns.length > 0) {
				for (String dn : dns)
					if (!serializeNames.containsKey(dn))
						serializeNames.put(dn, f);
			}
        }
        if (renames.size() > 0) {
        	String[] res = renames.keySet().toArray(new String[renames.size()]);
        	for (String re : res) {
        		String ren = renames.get(re);
        		if(serializeNames.containsKey(re)) {
        			Field f = serializeNames.remove(re);
        			serializeNames.put(ren, f);
        		}
        	}
        }
		return serializeNames;
	}

	protected Object readValue(JsonReader in) {
		try {
			JsonToken jt = in.peek();
			//System.out.println("JsonToken for value >>" + jt);
			switch (jt) {
			case NULL:
				try {
					in.nextNull();
					return null;
				} catch (IOException e) {}
				break;
			case NUMBER:
				try {
					return in.nextInt();
				} catch (IOException e) {}
				try {
					return in.nextLong();
				} catch (IOException e) {}
				try {
					return in.nextDouble();
				} catch (IOException e) {}
				break;
			case STRING:
				try {
					return in.nextString();
				} catch (IOException e) {}
				break;
			case BOOLEAN:
				try {
					return in.nextBoolean();
				} catch (IOException e) {}
				break;
			default:
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	abstract protected Object readObject(Class tClass, String fieldName, JsonReader in);
	protected <X> X readObject(Class<X> tClass, JsonReader in) throws IOException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException {
		X inst = tClass.newInstance();
		in.beginObject();
		while (in.hasNext()) {
			JsonToken jt = in.peek();
//			System.out.println("JsonToken for name >>" + jt);
			String field = in.nextName();
//			System.out.println("for name >>" + field);
			Field f = tClass.getField(field);
			Object v = readValue(in);
			try {
				if (null != v) {
					if (!f.isAccessible()) f.setAccessible(true);
					if (v instanceof String) {
						f.set(inst, v);
					} else if (v instanceof Integer) {
						f.setInt(inst, ((Integer)v).intValue());
					} else if (v instanceof Boolean) {
						f.setBoolean(inst, ((Boolean)v).booleanValue());
					} else if (v instanceof Double) {
						f.setDouble(inst, ((Double)v).doubleValue());
					} else if (v instanceof Long) {
						f.setLong(inst, ((Long)v).longValue());
					}
				} else {
					v = readObject(tClass, field, in);
					if (!f.isAccessible()) f.setAccessible(true);
					f.set(inst, v);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		in.endObject();
		return inst;
	}

	@SuppressWarnings("rawtypes")
	protected Field[] loadFields(Class tClass) {
		//System.out.println("loadFields for Class>>" + tClass.getName());
		ArrayList<Field> fields = new ArrayList<Field>();
		Class superClass = tClass.getSuperclass();
		if (Object.class != superClass) {
			Collections.addAll(fields, loadFields(superClass));
			//System.out.println("loadFields for Class>>" + tClass.getName() + ":: fields>>" + fields);
		}
		Collections.addAll(fields, tClass.getDeclaredFields());
		//System.out.println("loadFields for Class>>" + tClass.getName() + ": field size>>" + fields.size());
		return fields.toArray(new Field[fields.size()]);
	}

	abstract protected T newTypeInstance();

	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface OverwriteSuper {
		String[] renameField() default {};
		String[] renameTo() default {};
		String[] disableSerialize() default {};
		boolean excludeConts() default true;
	}

}