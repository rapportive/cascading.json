/*
 * Copyright 2009, Grégoire Marabout. All rights reserved.
 */
package cascading.json.operation;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import cascading.flow.FlowProcess;
import cascading.operation.Filter;
import cascading.operation.FilterCall;
import cascading.tuple.Fields;

import cascading.json.JSONUtils;

/**
 * A filter class that works on JSON objects to filter tuple that does not
 * respect the configuration specified.
 *
 * @author <a href="mailto:gmarabout@gmail.com">Grégoire Marabout</a>
 * @see cascading.json.operation.JSONFilterConfig
 */
public class JSONFilter extends JSONOperation implements Filter {

  private JSONFilterConfig configuration;

  /**
   * Factory method to create a filter that will exclude tuples
   * matching the specified specification.
   *
   * @param path  a path
   * @param value a value
   * @return a new filter.
   */
  public static JSONFilter mustDiffer(String path, Object value){
    return new JSONFilter( new JSONFilterConfig.MustDiffer( new String[]{ path }, new Object[]{ value } ) );
  }

  /**
   * Factory method to create a filter that will exclude tuples
   * matching the specified specification.
   *
   * @param paths  an array of paths
   * @param values an array of values
   * @return a new filter.
   */
  public static JSONFilter mustDiffer(String[] paths, Object[] values){
    return new JSONFilter( new JSONFilterConfig.MustDiffer( paths, values ) );
  }

  /**
   * Factory method to create a filter that will exclude tuples
   * <i>not</i> matching the specified specification.
   *
   * @param path  a path
   * @param value a value
   * @return a new filter.
   */
  public static JSONFilter mustEqual(String path, Object value){
    return new JSONFilter( new JSONFilterConfig.MustEqual( new String[]{ path }, new Object[]{ value } ) );
  }

  /**
   * Factory method to create a filter that will exclude tuples
   * <i>not</i> matching the specified specification.
   *
   * @param paths  an array of paths
   * @param values an array of values
   * @return a new filter.
   */
  public static JSONFilter mustEqual(String[] paths, Object[] values){
    return new JSONFilter( new JSONFilterConfig.MustEqual( paths, values ) );
  }

  public JSONFilter(JSONFilterConfig configuration){
    super( Fields.ALL );
    this.configuration = configuration;
  }


  @Override
  public boolean isRemove(FlowProcess flowProcess, FilterCall filterCall){
    Object input = filterCall.getArguments().get(0);

    JSON json = JSONUtils.getJSON(input);
    JSONObject jsonObj = (JSONObject) json; // yes, might fail

    return isRemove( jsonObj );
  }

  public boolean isRemove(JSONObject jsonObj){
    return !configuration.respect( jsonObj, this.getPathResolver() );
  }
}
