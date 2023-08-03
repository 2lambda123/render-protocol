package hiforce.render.protocol.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * @author Rocky Yu
 * @since 2019/8/9
 */
public class ClassUtils extends org.apache.commons.lang3.ClassUtils {

	/**
	 * LOGGER.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

	public static boolean isSubClassOf(Class<?> subClass, Class targetSuperClass) {
		return isSubClassOf(subClass, targetSuperClass, Lists.newArrayList());
	}

	public static boolean isSubClassOf(Class<?> subClass, Class targetSuperClass, List<Class> excludedSuperClass) {
		if (null == targetSuperClass)
			return false;
		try {
			if (subClass.isInterface()) {
				//接口要单独判断
				List<Class<?>> interfaceClasses = ClassUtils.getAllInterfaces(subClass);
				return interfaceClasses.stream().anyMatch(p -> p.equals(targetSuperClass));
			} else {
				if (targetSuperClass.isAssignableFrom(subClass)) {
					return true;
				}
				Class<?> superClass = subClass.getSuperclass();
				while (null != superClass) {
					if (!excludedSuperClass.contains(superClass)) {
						if (superClass.equals(Object.class)) {
							return false;
						}
						if (superClass.equals(targetSuperClass)) {
							return true;
						}
					}
					superClass = superClass.getSuperclass();
				}
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
		}
		return false;
	}

	public static Class getReadMethodGenericReturnType(PropertyDescriptor propertyDescriptor) {
		if (null == propertyDescriptor)
			return null;
		Method getMethod = propertyDescriptor.getReadMethod();
		ParameterizedType pt = (ParameterizedType) getMethod.getGenericReturnType();
		return (Class<?>) pt.getActualTypeArguments()[0];
	}

	/**
	 * 获取类clazz的所有Field，包括其父类的Field，如果重名，以子类Field为准。
	 *
	 * @param clazz
	 * @return Field数组
	 */
	public static Field[] getAllField(Class<?> clazz) {
		ArrayList<Field> fieldList = new ArrayList<>();
		Field[] dFields = clazz.getDeclaredFields();
		if (null != dFields && dFields.length > 0) {
			fieldList.addAll(Arrays.asList(dFields));
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass != Object.class) {
			Field[] superFields = getAllField(superClass);
			if (null != superFields && superFields.length > 0) {
				List<Field> superClassField = Lists.newArrayList();
				for (Field field : superFields) {
					if (!isContain(fieldList, field)) {
						superClassField.add(field);
					}
				}
				fieldList.addAll(0, superClassField);
			}
		}
		Field[] result = new Field[fieldList.size()];
		fieldList.toArray(result);
		return result;
	}

	/**
	 * 检测Field List中是否已经包含了目标field
	 *
	 * @param fieldList
	 * @param field     带检测field
	 * @return
	 */
	public static boolean isContain(ArrayList<Field> fieldList, Field field) {
		for (Field temp : fieldList) {
			if (temp.getName().equals(field.getName())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Object> T convertValue(PropertyDescriptor propertyDescriptor, Object value) {
		if (propertyDescriptor.getPropertyType().equals(String.class))
			return (T) (null == value ? null : String.valueOf(value));
		if (propertyDescriptor.getPropertyType().equals(Integer.class)
			|| propertyDescriptor.getPropertyType().equals(int.class)) {
			return (T) (Objects.isNull(value)  ? new Integer(0) : Integer.valueOf(value.toString()));
		}
		if (propertyDescriptor.getPropertyType().equals(Long.class)
			|| propertyDescriptor.getPropertyType().equals(long.class)) {
			return (T) (Objects.isNull(value) ? new Long(0) : Long.valueOf(value.toString()));
		}
		if (propertyDescriptor.getPropertyType().equals(Boolean.class)
			|| propertyDescriptor.getPropertyType().equals(boolean.class)) {
			return (T) (Objects.isNull(value) ? Boolean.FALSE : Boolean.valueOf(String.valueOf(value)));
		}
		if (null != value && propertyDescriptor.getPropertyType().isEnum()) {
			value = resolveEnumEnumValue(String.valueOf(value), propertyDescriptor.getPropertyType());
		}
		return (T) value;
	}

	@SuppressWarnings("unchecked")
	private static <T> T resolveEnumEnumValue(String value, Class type) {
		for (Object constant : type.getEnumConstants()) {
			if (constant.toString().equalsIgnoreCase(value)) {
				return (T) constant;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void setPropertyValue(Object target, PropertyDescriptor propertyDescriptor, Object value) {
		try {
			if (isCollectionProperty(propertyDescriptor)) {
				Collection collection = (Collection) propertyDescriptor.getReadMethod().invoke(target);
				collection.add(value);
				return;
			}
			if (null != propertyDescriptor.getWriteMethod()) {
				value = convertValue(propertyDescriptor, value);
				propertyDescriptor.getWriteMethod().invoke(target, value);
			} else {
				Method method = target.getClass()
					.getMethod(String.format("set%s", StringUtils.capitalize(propertyDescriptor.getName())),
						propertyDescriptor.getPropertyType());
				if (null != method) {
					value = convertValue(propertyDescriptor, value);
					method.invoke(target, value);
				}
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
	}

	private static boolean isCollectionProperty(PropertyDescriptor propertyDescriptor) {
		return isSubClassOf(propertyDescriptor.getPropertyType(), Collection.class);
	}

	public static List getSuperclasses(Class clazz) {
		return clazz == null ? null : getTypeInfo(clazz).getSuperclasses();
	}

	private static Map TYPE_MAP = Collections.synchronizedMap(new WeakHashMap());

	protected static TypeInfo getTypeInfo(Class type) {
		if (type == null) {
			throw new IllegalArgumentException("Parameter clazz should not be null");
		} else {
			Map var2 = TYPE_MAP;
			synchronized (TYPE_MAP) {
				TypeInfo classInfo = (TypeInfo) TYPE_MAP.get(type);
				if (classInfo == null) {
					classInfo = new TypeInfo(type);
					TYPE_MAP.put(type, classInfo);
				}

				return classInfo;
			}
		}
	}

	public static List getInterfaces(Class clazz) {
		return clazz == null ? null : getTypeInfo(clazz).getInterfaces();
	}

	public static Class getArrayClass(Class componentType, int dimension) {
		if (dimension <= 0) {
			return componentType;
		} else {
			return componentType == null ? null : Array.newInstance(componentType, new int[dimension]).getClass();
		}
	}

	protected static class TypeInfo {
		private Class type;
		private Class componentType;
		private int dimension;
		private List superclasses;
		private List interfaces;

		private TypeInfo(Class type) {
			this.superclasses = new ArrayList(2);
			this.interfaces = new ArrayList(2);
			this.type = type;
			Class componentType = null;
			if (type.isArray()) {
				componentType = type;

				do {
					componentType = componentType.getComponentType();
					++this.dimension;
				} while (componentType.isArray());
			}

			this.componentType = componentType;
			Class superComponentType;
			Class componentInterface;
			if (this.dimension > 0) {
				componentType = this.getNonPrimitiveType(componentType);
				superComponentType = componentType.getSuperclass();
				if (superComponentType == null && !Object.class.equals(componentType)) {
					superComponentType = Object.class;
				}

				if (superComponentType != null) {
					componentInterface = getArrayClass(superComponentType, this.dimension);
					this.superclasses.add(componentInterface);
					this.superclasses.addAll(getTypeInfo(componentInterface).superclasses);
				} else {
					for (int i = this.dimension - 1; i >= 0; --i) {
						this.superclasses.add(getArrayClass(Object.class, i));
					}
				}
			} else {
				type = this.getNonPrimitiveType(type);
				superComponentType = type.getSuperclass();
				if (superComponentType != null) {
					this.superclasses.add(superComponentType);
					this.superclasses.addAll(getTypeInfo(superComponentType).superclasses);
				}
			}

			if (this.dimension == 0) {
				Class[] typeInterfaces = type.getInterfaces();
				List set = new ArrayList();

				Class interfaceClass;
				for (int i = 0; i < typeInterfaces.length; ++i) {
					interfaceClass = typeInterfaces[i];
					set.add(interfaceClass);
					set.addAll(getTypeInfo(interfaceClass).interfaces);
				}

				Iterator i = this.superclasses.iterator();

				while (i.hasNext()) {
					interfaceClass = (Class) i.next();
					set.addAll(getTypeInfo(interfaceClass).interfaces);
				}

				i = set.iterator();

				while (i.hasNext()) {
					interfaceClass = (Class) i.next();
					if (!this.interfaces.contains(interfaceClass)) {
						this.interfaces.add(interfaceClass);
					}
				}
			} else {
				Iterator i = getTypeInfo(componentType).interfaces.iterator();

				while (i.hasNext()) {
					componentInterface = (Class) i.next();
					this.interfaces.add(getArrayClass(componentInterface, this.dimension));
				}
			}

		}

		private Class getNonPrimitiveType(Class type) {
			if (type.isPrimitive()) {
				if (Integer.TYPE.equals(type)) {
					type = Integer.class;
				} else if (Long.TYPE.equals(type)) {
					type = Long.class;
				} else if (Short.TYPE.equals(type)) {
					type = Short.class;
				} else if (Byte.TYPE.equals(type)) {
					type = Byte.class;
				} else if (Float.TYPE.equals(type)) {
					type = Float.class;
				} else if (Double.TYPE.equals(type)) {
					type = Double.class;
				} else if (Boolean.TYPE.equals(type)) {
					type = Boolean.class;
				} else if (Character.TYPE.equals(type)) {
					type = Character.class;
				}
			}

			return type;
		}

		public Class getType() {
			return this.type;
		}

		public Class getArrayComponentType() {
			return this.componentType;
		}

		public int getArrayDimension() {
			return this.dimension;
		}

		public List getSuperclasses() {
			return Collections.unmodifiableList(this.superclasses);
		}

		public List getInterfaces() {
			return Collections.unmodifiableList(this.interfaces);
		}
	}
}