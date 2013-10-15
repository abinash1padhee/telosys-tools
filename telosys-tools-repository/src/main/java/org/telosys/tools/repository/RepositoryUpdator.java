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
package org.telosys.tools.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.config.ClassNameProvider;
import org.telosys.tools.db.model.DatabaseColumn;
import org.telosys.tools.db.model.DatabaseForeignKey;
import org.telosys.tools.db.model.DatabaseModelManager;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.db.model.DatabaseTables;
import org.telosys.tools.repository.config.InitializerChecker;
import org.telosys.tools.repository.model.Column;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.ForeignKey;
import org.telosys.tools.repository.model.RepositoryModel;

/**
 * @author Laurent GUERIN, Eric LEMELIN
 * 
 */

public class RepositoryUpdator extends RepositoryManager
{
	private UpdateLogWriter _updateLogger = null;

	/**
	 * Constructor
	 * @param inichk
	 * @param classNameProvider
	 * @param logger
	 * @param updateLogger
	 */
	public RepositoryUpdator(InitializerChecker inichk, ClassNameProvider classNameProvider, 
			TelosysToolsLogger logger, UpdateLogWriter updateLogger) 
	{
		super(inichk, classNameProvider, logger);
		_updateLogger = updateLogger;
	}

	private Column addEntityAttribute(Entity entity, DatabaseColumn dbColumn ) 
	{
		Column column = buildColumn( dbColumn ) ;
		
		//--- Add the "column" to the "entity"
		entity.storeColumn(column);
		return column;
	}

	private int updateEntityAttribute(Column column, DatabaseColumn col) 
	{
		int r = 0;

		//--- Update the column 
		r = r + updateDbType(column, col.getDbTypeName()); // Database native type		
		r = r + updateTypeCode(column, col.getJdbcTypeCode()); // JDBC type code 
		r = r + updateNotNull(column, col.getNotNullAsString()); // Not null
		r = r + updateSize(column, col.getSize()); // Size

//		// --- If this column is in the Table Primary Key
		r = r + updatePrimaryKey(column, col.isInPrimaryKey()); // Column in Primary Key
		
		// TODO 
		// . default value
		// . comment
		// . auto incremented
		// . in foreign key

		return r;
	}

	private int updateTypeCode( Column column, int iDbTypeCode) 
	{
		int r = 0;
		int i = column.getJdbcTypeCode();
		if ( i != iDbTypeCode ) {
			_updateLogger.println(" . Column '" + column.getDatabaseName() + "' : JDBC type code changed to " + iDbTypeCode);
			column.setJdbcTypeCode(iDbTypeCode);
			r++;
		}
		return r;
	}

	private int updateDbType( Column column, String sDbType ) 
	{
		int r = 0;
		String s = column.getDatabaseTypeName();
		if ( ! s.equals(sDbType) ) {
			_updateLogger.println(" . Column '" + column.getDatabaseName() + "' : Database type changed to " + sDbType);
			column.setDatabaseTypeName(sDbType);
			r++;
		}
		return r;
	}

	private int updateNotNull( Column column, String sNotNull) 
	{
		int r = 0;
		String s = column.getDatabaseNotNullAsString();
		if ( ! s.equals(sNotNull) ) {
			_updateLogger.println(" . Column '" + column.getDatabaseName() + "' : NotNull changed to " + sNotNull);
			column.setDatabaseNotNull(sNotNull);
			r++;
		}
		return r;
	}

	private int updateSize( Column column, int iSize) 
	{
		int r = 0;
		if ( column.getDatabaseSize() != iSize ) 
		{
			_updateLogger.println(" . Column '" + column.getDatabaseName() + "' : Size changed to " + iSize);
			column.setDatabaseSize(iSize);
			r++;
		}
		return r;
	}

	private int updatePrimaryKey( Column column, boolean isPrimaryKey) 
	{
		int r = 0;
		if ( column.isPrimaryKey() != isPrimaryKey )
		{
			_updateLogger.println(" . Column '" + column.getDatabaseName() + "' : Primary Key flag changed to " + isPrimaryKey);
			column.setPrimaryKey(isPrimaryKey);
			r++;
		}
		return r;
	}

	// -----------------------------------------------------------------------------------------------------
	// UPDATE REPOSITORY
	// -----------------------------------------------------------------------------------------------------
	/**
	 * Updates the given repository with the database metadata.
	 * 
	 * @param con
	 * @param repositoryModel
	 * @param sCatalog
	 * @param sSchema
	 * @param sTableNamePattern
	 * @param arrayTableTypes
	 * @return the number of changes
	 * @throws TelosysToolsException
	 */
	public int updateRepository(Connection con, RepositoryModel repositoryModel, String sCatalog,
			String sSchema, String sTableNamePattern, String[] arrayTableTypes) throws TelosysToolsException 
	{
		int changesCount = 0 ;
		//_logger.log("--> Update repository ( file = '" + file.getFullPath() + "' )");

		Date now = new Date();

		try {
			_logger.log(" . get meta-data ");

			try {

				_logger.log(" . update repository from database tables");
				_updateLogger.println("Update date : " + now);
				
				//--- Load the Database Model
				DatabaseModelManager manager = new DatabaseModelManager( this.getLogger() );
				DatabaseTables dbTables = manager.getDatabaseTables(con, sCatalog, sSchema, sTableNamePattern, arrayTableTypes);

				//updateRepository(repositoryModel, dbmd, sCatalog, sSchema, sTableNamePattern, arrayTableTypes);
				changesCount = updateRepository(repositoryModel, dbTables);

			} catch (SQLException e) {
				throw new TelosysToolsException("SQLException", e);
			} catch (Throwable t) {
				throw new TelosysToolsException("Exception", t);
			}
		}
		finally {
			_updateLogger.close();
		}
		return changesCount ;
	}

	private int updateRepository(RepositoryModel repositoryModel, DatabaseTables dbTables ) throws SQLException 
	{
//		if (sTableNamePattern == null) {
//			sTableNamePattern = "%";
//		}
		int changesCount = 0 ;
		
		LinkedList<String> databaseTables = new LinkedList<String>();

		//-----------------------------------------------------------------------
		// STEP 1 : Update existing tables and Create new ones
		//-----------------------------------------------------------------------
		//--- For each table in the database ...
		int iTablesCount = 0;
		for ( DatabaseTable dbTable : dbTables.getTables() ) {
			iTablesCount++;
			
			_logger.log("   --------------------------------------------------------------");
			_logger.log("   Table '" + dbTable.getTableName() 
					+ "' ( catalog = '" + dbTable.getCatalogName() 
					+ "', schema = '"+ dbTable.getSchemaName() + "' )");

			String sTableName = dbTable.getTableName();

			// --- Store the table name in the list used to delete entities
			databaseTables.add(sTableName);

			_updateLogger.println(" ");
			Entity entity = repositoryModel.getEntityByName(sTableName);
			
			if ( entity != null ) 
			{
				// --- The <table ..> exists in the repository => update it
				_updateLogger.println(" Table '" + sTableName + "' found in repository");
				//int r = updateEntity(repositoryModel, dbmd, sCurrentCatalog, sCurrentSchema, sTableName, entity);
				int entityChanges = updateEntity(repositoryModel, dbTable, entity);
				if (entityChanges > 0) {
					_updateLogger.println(" (*) table '" + sTableName + "' updated : " + entityChanges + " change(s)");
				} else {
					_updateLogger.println(" (=) table '" + sTableName + "' unchanged");
				}
				changesCount = changesCount + entityChanges ;
			} else {
				// --- The TABLE doesn't exist in the repository => add it
				_updateLogger.println(" Table '" + sTableName + "' not found in repository");
				addEntity(repositoryModel, dbTable) ;
				_updateLogger.println(" (+) table '" + sTableName + "' added");
				changesCount++;
			}
		}

		//-----------------------------------------------------------------------
		// STEP 2 : Remove tables that no longer exist in the database
		//-----------------------------------------------------------------------
		//--- For each table in the repository ...
		String[] tableNames = repositoryModel.getEntitiesNames();
		for (int i = 0; i < tableNames.length; i++) 
		{
			String sTableName = tableNames[i];
			if (checkTableExistsInDatabase(sTableName, databaseTables) != true) {
				//--- This table in the repository no longer exists in the database
				_updateLogger.println(" ");
				_updateLogger.println(" Table '" + sTableName + "' no longer exists in database");
				//--- => Remove it
				repositoryModel.removeEntity(sTableName);
				_updateLogger.println(" (-) table '" + sTableName + "' removed");
				changesCount++;
			}
		}
		
		return changesCount ;
	}
	
	private int updateEntity( RepositoryModel repositoryModel, DatabaseTable dbTable, Entity entity) throws SQLException 
	{
		int changeCount = 0;
		//--------------------------------------------------------------------------------
		// 0) added in ver 2.0.7 
		//--------------------------------------------------------------------------------
		//--- Set or update TABLE TYPE ( "TABLE", "VIEW", ... )
		String tableType = dbTable.getTableType() ;
		if ( tableType != null ) {
			if ( StrUtil.nullOrVoid(entity.getDatabaseType()) ) {
				// Not set yet => Set type
				entity.setDatabaseType(tableType);
			}
			else {
				String originalType = entity.getDatabaseType() ;
				if ( tableType.equals(originalType) == false ) {
					// The type has changed => Update type
					entity.setDatabaseType(tableType);
					changeCount++;
					_updateLogger.println(" . Type has changed '" + originalType + "' --> '" + tableType + "'");
				}
			}
		}
		
		//--------------------------------------------------------------------------------
		// 1) remove the colums that doesn't exist in the Database 
		//--------------------------------------------------------------------------------
		//--- For each column in the repository ...
		Column[] columns = entity.getColumns();
		for (int i = 0; i < columns.length; i++) 
		{
			Column e = columns[i];
			String sColumnName = e.getDatabaseName();
			// Does it still exist in the DATABASE ?
			if ( null == dbTable.getColumnByName(sColumnName) )
			{
				//--- This column doesn't exist in the DB => remove it from the repo
				entity.removeColumn(e);
				changeCount++;
				_updateLogger.println(" . Column '" + sColumnName + "' removed");
			}
		}

		//--------------------------------------------------------------------------------
		// 2) remove the foreign keys that doesn't exist in the Database 
		//--------------------------------------------------------------------------------
		//--- For each fk in the repository ...
		ForeignKey[] fkeys = entity.getForeignKeys();
		for (int i = 0; i < fkeys.length; i++) 
		{
			ForeignKey fk = fkeys[i];
			String sFkName = fk.getName();
			// Does it still exist in the DATABASE ?
			if ( null == dbTable.getForeignKeyByName(sFkName) )
			{
				//--- This FK doesn't exist in the DB => remove it from the repo
				entity.removeForeignKey(fk);
				changeCount++;
				_updateLogger.println(" . Foreign key '" + sFkName + "' removed");
			}
		}

		//--------------------------------------------------------------------------------
		// 3) UPDATE existing COLUMNS if necessary and ADD new ones
		//--------------------------------------------------------------------------------
		//--- For each column of the table in the DataBase ...
		List<DatabaseColumn> dbColumns = dbTable.getColumns();
		for ( DatabaseColumn dbColumn : dbColumns ) {
			String sColumnName = dbColumn.getColumnName();
			
			//--- Search this column in the REPOSITORY
			Column column = entity.getColumn(sColumnName);
			if ( column != null ) 
			{
				// The column exists => update it
				changeCount = changeCount + updateEntityAttribute(column, dbColumn);

			} else {
				// The column doesn't exist => add it
				column = addEntityAttribute(entity, dbColumn);
				_updateLogger.println(" . Column '" + sColumnName + "' added");
				changeCount++;
			}
			//--- If this column is a member of a Foreign Key
			//setFkAttribute(sColumnName, column, listFK);
		}

		//--------------------------------------------------------------------------------
		// 4) UPDATE existing FOREIGN KEYS if necessary and ADD new ones
		//--------------------------------------------------------------------------------
		//--- For each FK of the table in the DataBase ...( v 0.9.0 )
		List<DatabaseForeignKey> dbForeignKeys = dbTable.getForeignKeys();
		for ( DatabaseForeignKey dbForeignKey : dbForeignKeys ) {
			
			String sFkName = dbForeignKey.getForeignKeyName();
			
			//--- Search this foreign key in the REPOSITORY
			ForeignKey newForeignKey = buildForeignKey(dbForeignKey) ;
			ForeignKey foreignKey = entity.getForeignKey(sFkName);
			if ( foreignKey != null ) 
			{
				// The FK exists => update it if it has changed
				if ( ! foreignKey.equals( newForeignKey ) )
				{
					// 
					entity.storeForeignKey(newForeignKey);
					changeCount++;
					_updateLogger.println(" . Foreign key '" + sFkName + "' updated");
				}
			}
			else
			{
				// The FK doesn't exist => add it to the list
				entity.storeForeignKey(newForeignKey);
				changeCount++;
				_updateLogger.println(" . Foreign key '" + sFkName + "' added");
			}
		}

		//--- Number of changes
		return changeCount;
	}
	
	private boolean checkTableExistsInDatabase(String sTableName, LinkedList<String> databaseTables) {
		// --- Search the Table Name in the Database Tables
		int n = databaseTables.size();
		for (int i = 0; i < n; i++) {
			String s = databaseTables.get(i);
			if (s != null) {
				if (s.equals(sTableName))
					return true;
			}
		}
		return false;
	}
}
