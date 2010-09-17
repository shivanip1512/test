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
package net.sf.jsonOLD.converter;

/**
 * @author Andres Almiray
 */
public class ByteConverter extends AbstractIntegerConverter
{
   private byte defaultValue;

   public ByteConverter()
   {
      super();
   }

   public ByteConverter( byte defaultValue )
   {
      super( true );
      this.defaultValue = defaultValue;
   }

   public byte convert( Object value )
   {
      if( value == null ){
         if( isUseDefault() ){
            return defaultValue;
         }else{
            throw new ConversionException( "value is null" );
         }
      }

      if( value instanceof Number ){
         return ((Number) value).byteValue();
      }else{
         byte i = 0;
         try{
            i = Byte.parseByte( getIntegerValue( String.valueOf( value ) ) );
            return i;
         }
         catch( NumberFormatException nfe ){
            if( isUseDefault() ){
               return defaultValue;
            }else{
               throw new ConversionException( nfe );
            }
         }
      }
   }
}