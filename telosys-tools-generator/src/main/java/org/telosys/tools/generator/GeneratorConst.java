/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.generator;

/**
 * Generator constants definition
 * 
 * @author Laurent Guerin
 *  
 */
public class GeneratorConst {

	public final static String BEAN_CLASS_CONTEXT_NAME        = "beanClass" ;

	public final static String SELECTED_ENTITIES_CONTEXT_NAME = "selectedEntities" ;
	
	public final static String CURRENT_CLASS_CONTEXT_NAME     = "class" ;

	
	public final static int JAVA_CLASS_TYPE_UNDEFINED = 0 ;
	public final static int JAVA_CLASS_TYPE_BEAN      = 1 ;
	public final static int JAVA_CLASS_TYPE_LIST      = 2 ;
	public final static int JAVA_CLASS_TYPE_DAO       = 3 ;
	public final static int JAVA_CLASS_TYPE_CONVERTER = 4 ;
	
}