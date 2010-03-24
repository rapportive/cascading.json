/*
 * Copyright 2009, Grégoire Marabout. All rights reserved. 
 */
package cascading.json.operation;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cascading.json.JSONWritable;
import cascading.operation.BaseOperation;
import cascading.tuple.Fields;

/**
 * @author <a href="mailto:gmarabout@gmail.com">Grégoire Marabout</a>
 */
public class JSONOperation extends BaseOperation {
	private JSONPathResolver resolver;

	protected JSONOperation(Fields fieldDeclaration) {
		this(fieldDeclaration, new DefaultJSONPathResolver(":"));
	}

	protected JSONOperation(Fields fieldDeclaration, JSONPathResolver resolver) {
		super(fieldDeclaration);
		this.resolver = resolver;
	}

	protected Comparable getValue(JSON jsonObject, String path) {
		Object value = resolver.resolve(jsonObject, path);
		if (value instanceof Comparable)
			return (Comparable) value;
		return null;
	}

	protected JSONPathResolver getPathResolver() {
		return resolver;
	}

	/**
	 * Default JSON path resolver
	 */
	public static class DefaultJSONPathResolver implements JSONPathResolver {
		private String pathSeparator;

		public DefaultJSONPathResolver(String pathSeparator) {
			this.pathSeparator = pathSeparator;
		}

		public Object resolve(JSON object, String path) {
			if (path == null || path.equals("")) return object;
			int index = path.lastIndexOf(pathSeparator);
			Object value = get(object, path);
			if (value == null && index > 0) {
				String subpath = path.substring(0, index);
				String key = path.substring(index + 1);
				value = get(resolve(object, subpath), key);
			}
			return value;
		}

		private Object get(Object json, String key) {
			if (json instanceof JSONObject) {
				JSONObject obj = (JSONObject) json;
				if (obj.containsKey(key)) return obj.get(key);
			}

			if (json instanceof JSONArray) {
				JSONArray arr = (JSONArray) json;
				try {
					int index = Integer.parseInt(key);
					if (index >= 0 && index < arr.size()) return arr.get(index);
				} catch (NumberFormatException e) {}
			}

			return null;
		}
	}

	public JSON getJSON(Object source) {
		if (source instanceof String) {
			return JSONObject.fromObject((String) source);
		} else if (source instanceof JSONWritable) {
			return ((JSONWritable) source).get();
		} else {
			throw new RuntimeException(
					"The JSON Splitter does not know how to handle an object of type: "
							+ source.getClass().getName());
		}
	}

}
