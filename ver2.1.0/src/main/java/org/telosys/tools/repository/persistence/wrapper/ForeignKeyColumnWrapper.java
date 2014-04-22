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
package org.telosys.tools.repository.persistence.wrapper;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.repository.model.ForeignKeyColumn;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ForeignKeyColumnWrapper {

	public ForeignKeyColumn getForeignKeyColumn(final Element xmlElement) {
		
		final ForeignKeyColumn foreignKeyColumn = new ForeignKeyColumn();
		
		foreignKeyColumn.setTableName(xmlElement.getAttribute(RepositoryConst.FKCOL_TABLENAME));
		foreignKeyColumn.setColumnName(xmlElement.getAttribute(RepositoryConst.FKCOL_COLNAME) );
		
		foreignKeyColumn.setSequence( StrUtil.getInt(xmlElement.getAttribute(RepositoryConst.FKCOL_SEQUENCE)) );
		
		foreignKeyColumn.setTableRef(xmlElement.getAttribute(RepositoryConst.FKCOL_TABLEREF));
		foreignKeyColumn.setColumnRef(xmlElement.getAttribute(RepositoryConst.FKCOL_COLREF));
		
//		foreignKeyColumn.setDeferrable(xmlElement.getAttribute(RepositoryConst.FKCOL_DEFERRABLE));
//		foreignKeyColumn.setUpdateRule(xmlElement.getAttribute(RepositoryConst.FKCOL_UPDATERULE));
//		foreignKeyColumn.setDeleteRule(xmlElement.getAttribute(RepositoryConst.FKCOL_DELETERULE));
		// v 2.0.7
		foreignKeyColumn.setDeferrableCode(StrUtil.getInt(xmlElement.getAttribute(RepositoryConst.FKCOL_DEFERRABLE)));
		foreignKeyColumn.setUpdateRuleCode(StrUtil.getInt(xmlElement.getAttribute(RepositoryConst.FKCOL_UPDATERULE)));
		foreignKeyColumn.setDeleteRuleCode(StrUtil.getInt(xmlElement.getAttribute(RepositoryConst.FKCOL_DELETERULE)));
		
		return foreignKeyColumn;
	}

	public Element getXmlDesc(final ForeignKeyColumn foreignKeyColumn, final Document doc) {
		
		final Element xmlElement = doc.createElement(RepositoryConst.FKCOL);
		
		xmlElement.setAttribute(RepositoryConst.FKCOL_TABLENAME, foreignKeyColumn.getTableName());
		xmlElement.setAttribute(RepositoryConst.FKCOL_COLNAME, foreignKeyColumn.getColumnName());
		
		xmlElement.setAttribute(RepositoryConst.FKCOL_SEQUENCE, Integer.toString(foreignKeyColumn.getSequence()) );

		xmlElement.setAttribute(RepositoryConst.FKCOL_TABLEREF, foreignKeyColumn.getTableRef());
		xmlElement.setAttribute(RepositoryConst.FKCOL_COLREF, foreignKeyColumn.getColumnRef());
		
		// v 2.0.7 (String -> int )
		xmlElement.setAttribute(RepositoryConst.FKCOL_DEFERRABLE, Integer.toString(foreignKeyColumn.getDeferrableCode()));
		xmlElement.setAttribute(RepositoryConst.FKCOL_UPDATERULE, Integer.toString(foreignKeyColumn.getUpdateRuleCode()));
		xmlElement.setAttribute(RepositoryConst.FKCOL_DELETERULE, Integer.toString(foreignKeyColumn.getDeleteRuleCode()));
		
		return xmlElement;
	}
}
