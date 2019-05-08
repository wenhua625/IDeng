package com.ripple.module;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MyDataRow
{
	private MyMetaData [] metaData;
	
	private Object [] rowData;
	
	private int currentColumn = 0;
	
	private int columns = 0;
	
	public MyDataRow( int columns )
	{
		if( columns > 0 )
		{
			this.columns = columns;
			metaData = new MyMetaData[ columns ];
			rowData = new Object[ columns ];
		}
	}
	
	public MyDataRow( ResultSet rs ) throws SQLException
	{
    	ResultSetMetaData rsmd = rs.getMetaData();
    	columns = rsmd.getColumnCount();
    	metaData = new MyMetaData[ columns ];
    	Object [] rowData = new Object[ columns ];
        if( rs.next() )
        {        	
            for( int i = 0; i < columns; i ++ )
            {
            	metaData[i].setColumnName( rsmd.getColumnName( i+1 ) );
            	metaData[i].setColumnType( rsmd.getColumnType( i+1 ) );
            	metaData[i].setColumnSize( rsmd.getColumnDisplaySize( i+1 ) );
            	rowData[i] = rs.getObject( i+1 );
            }
        }
	}

	public MyMetaData [] getMetaData()
	{
		return metaData;
	}

	public void setMetaData( MyMetaData [] metaData )
	{
		this.metaData = metaData;
	}

	public Object[] getRowData()
	{
		return rowData;
	}

	public Object getCellData( int column )
	{
		Object cellData = null;
		if( column > 0 && column <= columns )
		{
			cellData = rowData[ column ];
			currentColumn = column;
		}
		return cellData;
	}
	
	public Object getCellData( String columnName )
	{
		Object cellData = null;
		int column = getColumnNumber( columnName );
		if( column > 0 && column <= columns )
		{
			cellData = rowData[ column ];
 		}
		return cellData;
	}
	
	private int getColumnNumber( String columnName )
	{
		int column = 0;
		if( columnName != null && "".equals( columnName ) )
		{
			for( int i = 0; i < columns; i ++ )
			{
				if( columnName.equals( metaData[i].getColumnName() ))
				{
					column = i + 1;
					break;
				}
			}
		}
		return column;
	}
	
	public void setRowData( Object[] rowData )
	{
		this.rowData = rowData;
	}
	
	public boolean setCellData( int column, Object cellData )
	{
		boolean flag = false;
		if( column > 0 && column < columns )
		{
			rowData[column] = cellData;
			currentColumn = column;
		}
		return flag;
	}
	
	public void BeforeFirstColumn()
	{
		currentColumn = 0;
	}
	
	public boolean FirstColumn()
	{
		boolean flag = false;
		if( columns > 0 )
		{
			currentColumn = 1;
			flag = true;
		}
		return flag;
	}
	
	public boolean LastColumn()
	{
		boolean flag = false;
		if( columns > 0 )
		{
			currentColumn = columns;
			flag = true;
		}
		return flag;
	}
	
	public boolean NextColumn()
	{
		boolean flag = false;
		if( currentColumn < columns )
		{
			currentColumn ++;
			flag = true;
		}
		return flag;
	}
	
	public boolean ForwardColumn()
	{
		boolean flag = false;
		if( currentColumn > 0 )
		{
			currentColumn --;
			flag = true;
		}
		return flag;
	}
}
