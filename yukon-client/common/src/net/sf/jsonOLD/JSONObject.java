/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jsonOLD;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jsonOLD.converter.Converter;
import net.sf.jsonOLD.converter.ConverterRegistry;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JSONObject is an unordered collection of name/value pairs. Its external
 * form is a string wrapped in curly braces with colons between the names and
 * values, and commas between the values and names. The internal form is an
 * object having <code>get</code> and <code>opt</code> methods for accessing
 * the values by name, and <code>put</code> methods for adding or replacing
 * values by name. The values can be any of these types: <code>Boolean</code>,
 * <code>JSONArray</code>, <code>JSONObject</code>, <code>Number</code>,
 * <code>String</code>, or the <code>JSONObject.NULL</code> object. A
 * JSONObject constructor can be used to convert an external form JSON text into
 * an internal form whose values can be retrieved with the <code>get</code>
 * and <code>opt</code> methods, or to convert values into a JSON text using
 * the <code>put</code> and <code>toString</code> methods. A
 * <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and
 * type coersion for you.
 * <p>
 * The <code>put</code> methods adds values to an object. For example,
 *
 * <pre>
 *     myString = new JSONObject().put("JSON", "Hello, World!").toString();</pre>
 *
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * the JSON sysntax rules. The constructors are more forgiving in the texts they
 * will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a
 * quote or single quote, and if they do not contain leading or trailing spaces,
 * and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>,
 * or <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=></code> as well as
 * by <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small>
 * as well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0-</code> <small>(octal)</small> or
 * <code>0x-</code> <small>(hex)</small> prefix.</li>
 * <li>Comments written in the slashshlash, slashstar, and hash conventions
 * will be ignored.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 3
 */
public class JSONObject
{
   private static final Log log = LogFactory.getLog( JSONObject.class );

   /**
    * Creates a JSONObject from a POJO.<br>
    * Supports nested maps, POJOs, and arrays/collections.
    *
    * @param bean An object with POJO conventions
    */
   public static JSONObject fromBean( Object bean )
   {
      if( bean == null ){
         return new JSONObject( true );
      }
      JSONObject jsonObject = new JSONObject();
      if( bean != null ){
         if( JSONUtils.isArray( bean ) ){
            throw new IllegalArgumentException( "'bean' is an array. Use JSONArray instead" );
         }
         if( bean instanceof Map ){
            jsonObject = fromMap( (Map) bean );
         }else{
            try{
               PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors( bean );
               for( int i = 0; i < pds.length; i++ ){
                  String key = pds[i].getName();
                  if( "class".equals( key ) ){
                     continue;
                  }

                  Class type = pds[i].getPropertyType();
                  Object value = PropertyUtils.getProperty( bean, key );
                  if( String.class.isAssignableFrom( type ) ){
                     jsonObject.put( key, (value == null) ? "" : value );
                  }else if( JSONUtils.isArray( value ) ){
                     jsonObject.put( key, JSONArray.fromObject( value ) );
                  }else if( JSONUtils.isFunction( value ) ){
                     jsonObject.put( key, value );
                  }else if( JSONUtils.isObject( value ) ){
                     jsonObject.put( key, fromObject( value ) );
                  }else{
                     jsonObject.put( key, value );
                  }
               }
            }
            catch( Exception e ){
               log.warn( e );
            }
         }
      }
      return jsonObject;
   }

   /**
    * Constructs a JSONObject from a JSONTokener.
    *
    * @param tokener
    */
   public static JSONObject fromJSONTokener( JSONTokener tokener )
   {
      return new JSONObject( tokener );
   }

   /**
    * Creates a JSONObject from a map.<br>
    * The key names will become the object's attributes. Supports nested maps,
    * POJOs, and arrays/collections.
    *
    * @param map
    */
   public static JSONObject fromMap( Map map )
   {
      return new JSONObject( map );
   }

   /**
    * Creates a JSONObject.<br>
    * Inspects the object type to call the correct JSONObject factory method.
    *
    * @param object
    */
   public static JSONObject fromObject( Object object )
   {
      if( JSONUtils.isNull( object ) ){
         return new JSONObject( true );
      }
      if( object instanceof JSONTokener ){
         return fromJSONTokener( (JSONTokener) object );
      }else if( object instanceof Map ){
         return fromMap( (Map) object );
      }else if( object instanceof String ){
         return fromString( (String) object );
      }else{
         return fromBean( object );
      }
   }

   /**
    * Constructs a JSONObject from a string in JSON format.
    *
    * @param str A string in JSON format
    */
   public static JSONObject fromString( String str )
   {
      if( str == null || "null".compareToIgnoreCase( str ) == 0 ){
         return new JSONObject( true );
      }
      return new JSONObject( str );
   }

   /**
    * Creates a bean from a JSONObject, with a specific target class.<br>
    */
   public static Object toBean( JSONObject jsonObject, Class beanClass )
   {
      if( jsonObject.isNullObject() ){
         return null;
      }

      Object bean = null;
      try{
         bean = beanClass.newInstance();
         Map properties = JSONUtils.getProperties( jsonObject );
         for( Iterator entries = properties.entrySet()
               .iterator(); entries.hasNext(); ){
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String) entry.getKey();
            Class type = (Class) entry.getValue();
            Object value = jsonObject.get( key );
            PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor( bean, key );

            if( value instanceof JSONArray ){
               if( List.class.isAssignableFrom( pd.getPropertyType() ) ){
                  List list = JSONArray.toList( (JSONArray) value, beanClass );
                  PropertyUtils.setProperty( bean, key, list );
               }else{
                  Object array = JSONArray.toArray( (JSONArray) value, beanClass );
                  Class innerType = JSONUtils.getInnerComponentType( pd.getPropertyType() );
                  if( innerType.isPrimitive() || Number.class.isAssignableFrom( innerType )
                        || Boolean.class.isAssignableFrom( innerType )
                        || Character.class.isAssignableFrom( innerType ) ){
                     Converter converter = ConverterRegistry.getConverterFor( Array.newInstance(
                           innerType, 0 )
                           .getClass() );
                     array = MethodUtils.invokeMethod( converter, "convert", array );
                  }
                  PropertyUtils.setProperty( bean, key, array );
               }
            }else if( String.class.isAssignableFrom( type )
                  || Boolean.class.isAssignableFrom( type ) || Byte.class.isAssignableFrom( type )
                  || Short.class.isAssignableFrom( type ) || Integer.class.isAssignableFrom( type )
                  || Long.class.isAssignableFrom( type ) || Float.class.isAssignableFrom( type )
                  || Double.class.isAssignableFrom( type )
                  || Character.class.isAssignableFrom( type )
                  || JSONFunction.class.isAssignableFrom( type ) ){
               PropertyUtils.setProperty( bean, key, value );
            }else{
               PropertyUtils.setProperty( bean, key, toBean( (JSONObject) value,
                     pd.getPropertyType() ) );
            }
         }
      }
      catch( Exception e ){
         throw new JSONException( e );
      }

      return bean;
   }

   /**
    * Creates a JSONDynaBean from a JSONObject.
    */
   public static Object toBean( JSONObject jsonObject )
   {
      if( jsonObject.isNullObject() ){
         return null;
      }

      JSONDynaBean dynaBean = null;
      try{
         Map properties = JSONUtils.getProperties( jsonObject );
         JSONDynaClass dynaClass = new JSONDynaClass( "JSON", JSONDynaBean.class, properties );
         dynaBean = (JSONDynaBean) dynaClass.newInstance();
         dynaBean.setDynamicFormClass( dynaClass );
         for( Iterator entries = properties.entrySet()
               .iterator(); entries.hasNext(); ){
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String) entry.getKey();
            Class type = (Class) entry.getValue();
            Object value = jsonObject.get( key );
            if( value instanceof JSONArray ){
               PropertyUtils.setProperty( dynaBean, key, JSONArray.toList( (JSONArray) value ) );
            }else if( String.class.isAssignableFrom( type )
                  || Boolean.class.isAssignableFrom( type ) || Byte.class.isAssignableFrom( type )
                  || Short.class.isAssignableFrom( type ) || Integer.class.isAssignableFrom( type )
                  || Long.class.isAssignableFrom( type ) || Float.class.isAssignableFrom( type )
                  || Double.class.isAssignableFrom( type )
                  || Character.class.isAssignableFrom( type )
                  || JSONFunction.class.isAssignableFrom( type ) ){
               PropertyUtils.setProperty( dynaBean, key, value );
            }else{
               PropertyUtils.setProperty( dynaBean, key, toBean( (JSONObject) value ) );
            }
         }
      }
      catch( Exception e ){
         throw new JSONException( e );
      }

      return dynaBean;
   }

   // ------------------------------------------------------

   /**
    * The hash map where the JSONObject's properties are kept.
    */
   private HashMap myHashMap;

   /** identifies this object as null */
   private boolean nullObject;

   /**
    * Construct an empty JSONObject.
    */
   public JSONObject()
   {
      this.myHashMap = new HashMap();
   }

   /**
    * Creates a JSONObject that is null.
    */
   public JSONObject( boolean isNull )
   {
      this();
      this.nullObject = isNull;
   }

   /**
    * Construct a JSONObject from a subset of another JSONObject. An array of
    * strings is used to identify the keys that should be copied. Missing keys
    * are ignored.
    *
    * @param jo A JSONObject.
    * @param sa An array of strings.
    * @exception JSONException If a value is a non-finite number.
    */
   public JSONObject( JSONObject jo, String[] sa )
   {
      this();
      for( int i = 0; i < sa.length; i += 1 ){
         putOpt( sa[i], jo.opt( sa[i] ) );
      }
   }

   /**
    * Construct a JSONObject from a JSONTokener.
    *
    * @param x A JSONTokener object containing the source string.
    * @throws JSONException If there is a syntax error in the source string.
    */
   public JSONObject( JSONTokener x )
   {
      this();
      char c;
      String key;

      if( x.matches( "null.*" ) ){
         this.nullObject = true;
         return;
      }

      if( x.nextClean() != '{' ){
         throw x.syntaxError( "A JSONObject text must begin with '{'" );
      }
      for( ;; ){
         c = x.nextClean();
         switch( c )
         {
            case 0:
               throw x.syntaxError( "A JSONObject text must end with '}'" );
            case '}':
               return;
            default:
               x.back();
               key = x.nextValue()
                     .toString();
         }

         /*
          * The key is followed by ':'. We will also tolerate '=' or '=>'.
          */

         c = x.nextClean();
         if( c == '=' ){
            if( x.next() != '>' ){
               x.back();
            }
         }else if( c != ':' ){
            throw x.syntaxError( "Expected a ':' after a key" );
         }
         Object v = x.nextValue();
         if( !JSONUtils.isFunctionHeader( v ) ){
            this.myHashMap.put( key, v );
         }else{
            // read params if any
            String params = JSONUtils.getFunctionParams( (String) v );
            // read function text
            int i = 0;
            StringBuffer sb = new StringBuffer();
            for( ;; ){
               char ch = x.next();
               if( ch == 0 ){
                  break;
               }
               if( ch == '{' ){
                  i++;
               }
               if( ch == '}' ){
                  i--;
               }
               sb.append( ch );
               if( i == 0 ){
                  break;
               }
            }
            if( i != 0 ){
               throw x.syntaxError( "Unbalanced '{' or '}' on prop: " + v );
            }
            // trim '{' at start and '}' at end
            String text = sb.toString();
            text = text.substring( 1, text.length() - 1 )
                  .trim();
            this.myHashMap.put( key, new JSONFunction( (params != null) ? StringUtils.split(params, ',' )
                  : null, text ) );
         }

         /*
          * Pairs are separated by ','. We will also tolerate ';'.
          */

         switch( x.nextClean() )
         {
            case ';':
            case ',':
               if( x.nextClean() == '}' ){
                  return;
               }
               x.back();
               break;
            case '}':
               return;
            default:
               throw x.syntaxError( "Expected a ',' or '}'" );
         }
      }
   }

   /**
    * Construct a JSONObject from a Map.<br>
    * Assumes the object hierarchy is acyclical.
    *
    * @param map A map object that can be used to initialize the contents of the
    *        JSONObject.
    */
   public JSONObject( Map map )
   {
      if( map == null ){
         this.nullObject = true;
         return;
      }

      this.myHashMap = new HashMap();
      if( map != null ){
         for( Iterator entries = map.entrySet()
               .iterator(); entries.hasNext(); ){
            Map.Entry entry = (Map.Entry) entries.next();
            Object k = entry.getKey();
            String key = (k instanceof String) ? (String) k : String.valueOf( k );
            Object value = entry.getValue();

            if( JSONUtils.isArray( value ) ){
               this.put( key, JSONArray.fromObject( value ) );
            }else if( JSONUtils.isFunction( value ) ){
               this.put( key, value );
            }else if( JSONUtils.isObject( value ) ){
               this.put( key, fromObject( value ) );
            }else if( JSONUtils.isString( value ) ){
               this.put( key, (value == null) ? "" : value );
            }else{
               this.put( key, value );
            }
         }
      }
   }

   /**
    * Construct a JSONObject from an Object, using reflection to find the public
    * members. The resulting JSONObject's keys will be the strings from the
    * names array, and the values will be the field values associated with those
    * keys in the object. If a key is not found or not visible, then it will not
    * be copied into the new JSONObject.<br>
    * Assumes the object hierarchy is acyclical.
    *
    * @param object An object that has fields that should be used to make a
    *        JSONObject.
    * @param names An array of strings, the names of the fields to be used from
    *        the object.
    */
   public JSONObject( Object object, String names[] )
   {
      this();
      if( object == null ){
         this.nullObject = true;
         return;
      }

      try{
         PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors( object );
         for( int i = 0; i < pds.length; i++ ){
            String key = pds[i].getName();
            if( "class".equals( key ) || !ArrayUtils.contains( names, key ) ){
               continue;
            }

            Class type = pds[i].getPropertyType();
            Object value = PropertyUtils.getProperty( object, key );
            if( String.class.isAssignableFrom( type ) ){
               this.put( key, (value == null) ? "" : value );
            }else if( JSONUtils.isFunction( value ) ){
               this.put( key, value );
            }else if( JSONUtils.isArray( value ) ){
               this.put( key, JSONArray.fromObject( value ) );
            }else if( JSONUtils.isObject( value ) ){
               this.put( key, fromObject( value ) );
            }else{
               this.put( key, value );
            }
         }
      }
      catch( Exception e ){
         log.warn( e );
      }
   }

   /**
    * Construct a JSONObject from a string. This is the most commonly used
    * JSONObject constructor.
    *
    * @param string A string beginning with <code>{</code>&nbsp;<small>(left
    *        brace)</small> and ending with <code>}</code>&nbsp;<small>(right
    *        brace)</small>.
    * @exception JSONException If there is a syntax error in the source string.
    */
   public JSONObject( String string )
   {
      this( new JSONTokener( string ) );
   }

   /**
    * Accumulate values under a key. It is similar to the put method except that
    * if there is already an object stored under the key then a JSONArray is
    * stored under the key to hold all of the accumulated values. If there is
    * already a JSONArray, then the new value is appended to it. In contrast,
    * the put method replaces the previous value.
    *
    * @param key A key string.
    * @param value An object to be accumulated under the key.
    * @return this.
    * @throws JSONException If the value is an invalid number or if the key is
    *         null.
    */
   public JSONObject accumulate( String key, Object value )
   {
      if( isNullObject() ){
         throw new JSONException( "Can't accumulate on null object" );
      }

      JSONUtils.testValidity( value );
      Object o = opt( key );
      if( o == null ){
         put( key, value );
      }else if( o instanceof JSONArray ){
         ((JSONArray) o).put( value );
      }else{
         put( key, new JSONArray().put( o )
               .put( value ) );
      }

      return this;
   }

   /**
    * Append values to the array under a key. If the key does not exist in the
    * JSONObject, then the key is put in the JSONObject with its value being a
    * JSONArray containing the value parameter. If the key was already
    * associated with a JSONArray, then the value parameter is appended to it.
    *
    * @param key A key string.
    * @param value An object to be accumulated under the key.
    * @return this.
    * @throws JSONException If the key is null or if the current value
    *         associated with the key is not a JSONArray.
    */
   public JSONObject append( String key, Object value )
   {
      if( isNullObject() ){
         throw new JSONException( "Can't append on null object" );
      }
      JSONUtils.testValidity( value );
      Object o = opt( key );
      if( o == null ){
         put( key, new JSONArray().put( value ) );
      }else if( o instanceof JSONArray ){
         throw new JSONException( "JSONObject[" + key + "] is not a JSONArray." );
      }else{
         put( key, new JSONArray().put( o )
               .put( value ) );
      }

      return this;
   }

   /**
    * Get the value object associated with a key.
    *
    * @param key A key string.
    * @return The object associated with the key.
    * @throws JSONException if the key is not found.
    */
   public Object get( String key )
   {
      verifyIsNull();
      Object o = opt( key );
      if( o == null ){
         throw new JSONException( "JSONObject[" + JSONUtils.quote( key ) + "] not found." );
      }
      return o;
   }

   /**
    * Get the boolean value associated with a key.
    *
    * @param key A key string.
    * @return The truth.
    * @throws JSONException if the value is not a Boolean or the String "true"
    *         or "false".
    */
   public boolean getBoolean( String key )
   {
      verifyIsNull();
      Object o = get( key );
      if( o.equals( Boolean.FALSE )
            || (o instanceof String && ((String) o).equalsIgnoreCase( "false" )) ){
         return false;
      }else if( o.equals( Boolean.TRUE )
            || (o instanceof String && ((String) o).equalsIgnoreCase( "true" )) ){
         return true;
      }
      throw new JSONException( "JSONObject[" + JSONUtils.quote( key ) + "] is not a Boolean." );
   }

   /**
    * Get the double value associated with a key.
    *
    * @param key A key string.
    * @return The numeric value.
    * @throws JSONException if the key is not found or if the value is not a
    *         Number object and cannot be converted to a number.
    */
   public double getDouble( String key )
   {
      verifyIsNull();
      Object o = get( key );
      try{
         return o instanceof Number ? ((Number) o).doubleValue() : Double.parseDouble( (String) o );
      }
      catch( Exception e ){
         throw new JSONException( "JSONObject[" + JSONUtils.quote( key ) + "] is not a number." );
      }
   }

   /**
    * Get the int value associated with a key. If the number value is too large
    * for an int, it will be clipped.
    *
    * @param key A key string.
    * @return The integer value.
    * @throws JSONException if the key is not found or if the value cannot be
    *         converted to an integer.
    */
   public int getInt( String key )
   {
      verifyIsNull();
      Object o = get( key );
      return o instanceof Number ? ((Number) o).intValue() : (int) getDouble( key );
   }

   /**
    * Get the JSONArray value associated with a key.
    *
    * @param key A key string.
    * @return A JSONArray which is the value.
    * @throws JSONException if the key is not found or if the value is not a
    *         JSONArray.
    */
   public JSONArray getJSONArray( String key )
   {
      verifyIsNull();
      Object o = get( key );
      if( o instanceof JSONArray ){
         return (JSONArray) o;
      }
      throw new JSONException( "JSONObject[" + JSONUtils.quote( key ) + "] is not a JSONArray." );
   }

   /**
    * Get the JSONObject value associated with a key.
    *
    * @param key A key string.
    * @return A JSONObject which is the value.
    * @throws JSONException if the key is not found or if the value is not a
    *         JSONObject.
    */
   public JSONObject getJSONObject( String key )
   {
      verifyIsNull();
      Object o = get( key );
      if( o instanceof JSONObject ){
         return (JSONObject) o;
      }
      throw new JSONException( "JSONObject[" + JSONUtils.quote( key ) + "] is not a JSONObject." );
   }

   /**
    * Get the long value associated with a key. If the number value is too long
    * for a long, it will be clipped.
    *
    * @param key A key string.
    * @return The long value.
    * @throws JSONException if the key is not found or if the value cannot be
    *         converted to a long.
    */
   public long getLong( String key )
   {
      verifyIsNull();
      Object o = get( key );
      return o instanceof Number ? ((Number) o).longValue() : (long) getDouble( key );
   }

   /**
    * Get the string associated with a key.
    *
    * @param key A key string.
    * @return A string which is the value.
    * @throws JSONException if the key is not found.
    */
   public String getString( String key )
   {
      verifyIsNull();
      return get( key ).toString();
   }

   /**
    * Determine if the JSONObject contains a specific key.
    *
    * @param key A key string.
    * @return true if the key exists in the JSONObject.
    */
   public boolean has( String key )
   {
      verifyIsNull();
      return this.myHashMap.containsKey( key );
   }

   /**
    * Returs if this object is a null JSONObject.
    */
   public boolean isNullObject()
   {
      return nullObject;
   }

   /**
    * Get an enumeration of the keys of the JSONObject.
    *
    * @return An iterator of the keys.
    */
   public Iterator keys()
   {
      verifyIsNull();
      return this.myHashMap.keySet()
            .iterator();
   }

   /**
    * Get the number of keys stored in the JSONObject.
    *
    * @return The number of keys in the JSONObject.
    */
   public int length()
   {
      verifyIsNull();
      return this.myHashMap.size();
   }

   /**
    * Produce a JSONArray containing the names of the elements of this
    * JSONObject.
    *
    * @return A JSONArray containing the key strings, or null if the JSONObject
    *         is empty.
    */
   public JSONArray names()
   {
      verifyIsNull();
      JSONArray ja = new JSONArray();
      Iterator keys = keys();
      while( keys.hasNext() ){
         ja.put( keys.next() );
      }
      return ja.length() == 0 ? null : ja;
   }

   /**
    * Get an optional value associated with a key.
    *
    * @param key A key string.
    * @return An object which is the value, or null if there is no value.
    */
   public Object opt( String key )
   {
      verifyIsNull();
      return key == null ? null : this.myHashMap.get( key );
   }

   /**
    * Get an optional boolean associated with a key. It returns false if there
    * is no such key, or if the value is not Boolean.TRUE or the String "true".
    *
    * @param key A key string.
    * @return The truth.
    */
   public boolean optBoolean( String key )
   {
      verifyIsNull();
      return optBoolean( key, false );
   }

   /**
    * Get an optional boolean associated with a key. It returns the defaultValue
    * if there is no such key, or if it is not a Boolean or the String "true" or
    * "false" (case insensitive).
    *
    * @param key A key string.
    * @param defaultValue The default.
    * @return The truth.
    */
   public boolean optBoolean( String key, boolean defaultValue )
   {
      verifyIsNull();
      try{
         return getBoolean( key );
      }
      catch( Exception e ){
         return defaultValue;
      }
   }

   /**
    * Get an optional double associated with a key, or NaN if there is no such
    * key or if its value is not a number. If the value is a string, an attempt
    * will be made to evaluate it as a number.
    *
    * @param key A string which is the key.
    * @return An object which is the value.
    */
   public double optDouble( String key )
   {
      verifyIsNull();
      return optDouble( key, Double.NaN );
   }

   /**
    * Get an optional double associated with a key, or the defaultValue if there
    * is no such key or if its value is not a number. If the value is a string,
    * an attempt will be made to evaluate it as a number.
    *
    * @param key A key string.
    * @param defaultValue The default.
    * @return An object which is the value.
    */
   public double optDouble( String key, double defaultValue )
   {
      verifyIsNull();
      try{
         Object o = opt( key );
         return o instanceof Number ? ((Number) o).doubleValue()
               : new Double( (String) o ).doubleValue();
      }
      catch( Exception e ){
         return defaultValue;
      }
   }

   /**
    * Get an optional int value associated with a key, or zero if there is no
    * such key or if the value is not a number. If the value is a string, an
    * attempt will be made to evaluate it as a number.
    *
    * @param key A key string.
    * @return An object which is the value.
    */
   public int optInt( String key )
   {
      verifyIsNull();
      return optInt( key, 0 );
   }

   /**
    * Get an optional int value associated with a key, or the default if there
    * is no such key or if the value is not a number. If the value is a string,
    * an attempt will be made to evaluate it as a number.
    *
    * @param key A key string.
    * @param defaultValue The default.
    * @return An object which is the value.
    */
   public int optInt( String key, int defaultValue )
   {
      verifyIsNull();
      try{
         return getInt( key );
      }
      catch( Exception e ){
         return defaultValue;
      }
   }

   /**
    * Get an optional JSONArray associated with a key. It returns null if there
    * is no such key, or if its value is not a JSONArray.
    *
    * @param key A key string.
    * @return A JSONArray which is the value.
    */
   public JSONArray optJSONArray( String key )
   {
      verifyIsNull();
      Object o = opt( key );
      return o instanceof JSONArray ? (JSONArray) o : null;
   }

   /**
    * Get an optional JSONObject associated with a key. It returns null if there
    * is no such key, or if its value is not a JSONObject.
    *
    * @param key A key string.
    * @return A JSONObject which is the value.
    */
   public JSONObject optJSONObject( String key )
   {
      verifyIsNull();
      Object o = opt( key );
      return o instanceof JSONObject ? (JSONObject) o : null;
   }

   /**
    * Get an optional long value associated with a key, or zero if there is no
    * such key or if the value is not a number. If the value is a string, an
    * attempt will be made to evaluate it as a number.
    *
    * @param key A key string.
    * @return An object which is the value.
    */
   public long optLong( String key )
   {
      verifyIsNull();
      return optLong( key, 0 );
   }

   /**
    * Get an optional long value associated with a key, or the default if there
    * is no such key or if the value is not a number. If the value is a string,
    * an attempt will be made to evaluate it as a number.
    *
    * @param key A key string.
    * @param defaultValue The default.
    * @return An object which is the value.
    */
   public long optLong( String key, long defaultValue )
   {
      verifyIsNull();
      try{
         return getLong( key );
      }
      catch( Exception e ){
         return defaultValue;
      }
   }

   /**
    * Get an optional string associated with a key. It returns an empty string
    * if there is no such key. If the value is not a string and is not null,
    * then it is coverted to a string.
    *
    * @param key A key string.
    * @return A string which is the value.
    */
   public String optString( String key )
   {
      verifyIsNull();
      return optString( key, "" );
   }

   /**
    * Get an optional string associated with a key. It returns the defaultValue
    * if there is no such key.
    *
    * @param key A key string.
    * @param defaultValue The default.
    * @return A string which is the value.
    */
   public String optString( String key, String defaultValue )
   {
      verifyIsNull();
      Object o = opt( key );
      return o != null ? o.toString() : defaultValue;
   }

   /**
    * Put a key/boolean pair in the JSONObject.
    *
    * @param key A key string.
    * @param value A boolean which is the value.
    * @return this.
    * @throws JSONException If the key is null.
    */
   public JSONObject put( String key, boolean value )
   {
      verifyIsNull();
      put( key, value ? Boolean.TRUE : Boolean.FALSE );
      return this;
   }

   /**
    * Put a key/value pair in the JSONObject, where the value will be a
    * JSONArray which is produced from a Collection.
    *
    * @param key A key string.
    * @param value A Collection value.
    * @return this.
    * @throws JSONException
    */
   public JSONObject put( String key, Collection value )
   {
      verifyIsNull();
      put( key, new JSONArray( value ) );
      return this;
   }

   /**
    * Put a key/double pair in the JSONObject.
    *
    * @param key A key string.
    * @param value A double which is the value.
    * @return this.
    * @throws JSONException If the key is null or if the number is invalid.
    */
   public JSONObject put( String key, double value )
   {
      verifyIsNull();
      put( key, new Double( value ) );
      return this;
   }

   /**
    * Put a key/int pair in the JSONObject.
    *
    * @param key A key string.
    * @param value An int which is the value.
    * @return this.
    * @throws JSONException If the key is null.
    */
   public JSONObject put( String key, int value )
   {
      verifyIsNull();
      put( key, new Integer( value ) );
      return this;
   }

   /**
    * Put a key/long pair in the JSONObject.
    *
    * @param key A key string.
    * @param value A long which is the value.
    * @return this.
    * @throws JSONException If the key is null.
    */
   public JSONObject put( String key, long value )
   {
      verifyIsNull();
      put( key, new Long( value ) );
      return this;
   }

   /**
    * Put a key/value pair in the JSONObject, where the value will be a
    * JSONObject which is produced from a Map.
    *
    * @param key A key string.
    * @param value A Map value.
    * @return this.
    * @throws JSONException
    */
   public JSONObject put( String key, Map value )
   {
      verifyIsNull();
      put( key, new JSONObject( value ) );
      return this;
   }

   /**
    * Put a key/value pair in the JSONObject. If the value is null, then the key
    * will be removed from the JSONObject if it is present.
    *
    * @param key A key string.
    * @param value An object which is the value. It should be of one of these
    *        types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
    *        String, or the JSONObject.NULL object.
    * @return this.
    * @throws JSONException If the value is non-finite number or if the key is
    *         null.
    */
   public JSONObject put( String key, Object value )
   {
      verifyIsNull();
      if( key == null ){
         throw new JSONException( "Null key." );
      }
      if( value != null ){
         JSONUtils.testValidity( value );
         this.myHashMap.put( key, value );
      }else{
         remove( key );
      }
      return this;
   }

   /**
    * Put a key/value pair in the JSONObject, but only if the key and the value
    * are both non-null.
    *
    * @param key A key string.
    * @param value An object which is the value. It should be of one of these
    *        types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
    *        String, or the JSONObject.NULL object.
    * @return this.
    * @throws JSONException If the value is a non-finite number.
    */
   public JSONObject putOpt( String key, Object value )
   {
      verifyIsNull();
      if( key != null && value != null ){
         put( key, value );
      }
      return this;
   }

   /**
    * Remove a name and its value, if present.
    *
    * @param key The name to be removed.
    * @return The value that was associated with the name, or null if there was
    *         no value.
    */
   public Object remove( String key )
   {
      verifyIsNull();
      return this.myHashMap.remove( key );
   }

   /**
    * Produce a JSONArray containing the values of the members of this
    * JSONObject.
    *
    * @param names A JSONArray containing a list of key strings. This determines
    *        the sequence of the values in the result.
    * @return A JSONArray of values.
    * @throws JSONException If any of the values are non-finite numbers.
    */
   public JSONArray toJSONArray( JSONArray names )
   {
      verifyIsNull();
      if( names == null || names.length() == 0 ){
         return null;
      }
      JSONArray ja = new JSONArray();
      for( int i = 0; i < names.length(); i += 1 ){
         ja.put( this.opt( names.getString( i ) ) );
      }
      return ja;
   }

   /**
    * Make a JSON text of this JSONObject. For compactness, no whitespace is
    * added. If this would not result in a syntactically correct JSON text, then
    * null will be returned instead.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @return a printable, displayable, portable, transmittable representation
    *         of the object, beginning with <code>{</code>&nbsp;<small>(left
    *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
    *         brace)</small>.
    */
   public String toString()
   {
      if( isNullObject() ){
         return JSONNull.getInstance()
               .toString();
      }
      try{
         Iterator keys = keys();
         StringBuffer sb = new StringBuffer( "{" );

         while( keys.hasNext() ){
            if( sb.length() > 1 ){
               sb.append( ',' );
            }
            Object o = keys.next();
            sb.append( JSONUtils.quote( o.toString() ) );
            sb.append( ':' );
            sb.append( JSONUtils.valueToString( this.myHashMap.get( o ) ) );
         }
         sb.append( '}' );
         return sb.toString();
      }
      catch( Exception e ){
         return null;
      }
   }

   /**
    * Make a prettyprinted JSON text of this JSONObject.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @param indentFactor The number of spaces to add to each level of
    *        indentation.
    * @return a printable, displayable, portable, transmittable representation
    *         of the object, beginning with <code>{</code>&nbsp;<small>(left
    *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
    *         brace)</small>.
    * @throws JSONException If the object contains an invalid number.
    */
   public String toString( int indentFactor )
   {
      if( isNullObject() ){
         return JSONNull.getInstance()
               .toString();
      }
      return toString( indentFactor, 0 );
   }

   /**
    * Make a prettyprinted JSON text of this JSONObject.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @param indentFactor The number of spaces to add to each level of
    *        indentation.
    * @param indent The indentation of the top level.
    * @return a printable, displayable, transmittable representation of the
    *         object, beginning with <code>{</code>&nbsp;<small>(left brace)</small>
    *         and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
    * @throws JSONException If the object contains an invalid number.
    */
   public String toString( int indentFactor, int indent )
   {
      if( isNullObject() ){
         return JSONNull.getInstance()
               .toString();
      }
      int i;
      int n = length();
      if( n == 0 ){
         return "{}";
      }
      Iterator keys = keys();
      StringBuffer sb = new StringBuffer( "{" );
      int newindent = indent + indentFactor;
      Object o;
      if( n == 1 ){
         o = keys.next();
         sb.append( JSONUtils.quote( o.toString() ) );
         sb.append( ": " );
         sb.append( JSONUtils.valueToString( this.myHashMap.get( o ), indentFactor, indent ) );
      }else{
         while( keys.hasNext() ){
            o = keys.next();
            if( sb.length() > 1 ){
               sb.append( ",\n" );
            }else{
               sb.append( '\n' );
            }
            for( i = 0; i < newindent; i += 1 ){
               sb.append( ' ' );
            }
            sb.append( JSONUtils.quote( o.toString() ) );
            sb.append( ": " );
            sb.append( JSONUtils.valueToString( this.myHashMap.get( o ), indentFactor, newindent ) );
         }
         if( sb.length() > 1 ){
            sb.append( '\n' );
            for( i = 0; i < indent; i += 1 ){
               sb.append( ' ' );
            }
         }
      }
      sb.append( '}' );
      return sb.toString();
   }

   /**
    * Write the contents of the JSONObject as JSON text to a writer. For
    * compactness, no whitespace is added.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @return The writer.
    * @throws JSONException
    */
   public Writer write( Writer writer )
   {
      try{
         if( isNullObject() ){
            writer.write( JSONNull.getInstance()
                  .toString() );
            return writer;
         }

         boolean b = false;
         Iterator keys = keys();
         writer.write( '{' );

         while( keys.hasNext() ){
            if( b ){
               writer.write( ',' );
            }
            Object k = keys.next();
            writer.write( JSONUtils.quote( k.toString() ) );
            writer.write( ':' );
            Object v = this.myHashMap.get( k );
            if( v instanceof JSONObject ){
               ((JSONObject) v).write( writer );
            }else if( v instanceof JSONArray ){
               ((JSONArray) v).write( writer );
            }else{
               writer.write( JSONUtils.valueToString( v ) );
            }
            b = true;
         }
         writer.write( '}' );
         return writer;
      }
      catch( IOException e ){
         throw new JSONException( e );
      }
   }

   private void verifyIsNull()
   {
      if( isNullObject() ){
         throw new JSONException( "null object" );
      }
   }
}