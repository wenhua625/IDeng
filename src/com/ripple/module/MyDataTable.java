package com.ripple.module;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FastArrayList;

public class MyDataTable
{
	private MyMetaData [] metaData;
	
	private List data;
	
	private int currentRow = 0;
	
	private int currentColumn = 0;
	
	private int rows = 0;
	
	private int columns = 0;

	public MyDataTable( int rows, int columns )
	{
		if( rows > 0 && columns > 0 )
		{
			this.rows = rows;
			this.columns = columns;
			metaData = new MyMetaData[ columns ];
			data = new ArrayList( rows );
			for( int i=0; i < rows; i ++ )
			{
				Object [] rowData = new Object[ columns ];
				data.add( rowData );
			}
		}
	}
	
    public MyDataTable( ResultSet rs, String DSEncoding, String DBEncoding ) throws SQLException
    {
    	ResultSetMetaData RSMetaData = rs.getMetaData();
    	columns = RSMetaData.getColumnCount();
    	//获得元数据
    	metaData = new MyMetaData[ columns ];
    	for( int i=0; i < columns; i ++ )
    	{
    		MyMetaData tempMeta = new MyMetaData();
    		tempMeta.setColumnName( RSMetaData.getColumnName( i+1 ).toLowerCase() );
    		tempMeta.setColumnType( RSMetaData.getColumnType( i+1 ) );
    		tempMeta.setColumnSize( RSMetaData.getColumnDisplaySize( i+1 ) );
    		metaData[i] = tempMeta;
    	}
    	//获取数据
    	data = new FastArrayList();
        while( rs.next() )
        {
        	Object [] row = new Object[ columns ];
            for( int i = 0; i < columns; i ++ )
            {
            	int columnType = RSMetaData.getColumnType( i + 1 );
            	if( columnType == Types.CHAR || columnType == Types.VARCHAR || columnType == Types.LONGVARCHAR )
                {
            		String temp = rs.getString( i + 1 );
            		if( temp == null )
            		{
            			temp = "";
            		}else
            		{
            			try
						{
							temp = new String( temp.getBytes( DBEncoding ), DSEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
            		}
            		row[i] = temp;
                }else
                {
                	row[i] = rs.getObject( i + 1 );
                }
            }
            data.add( row );
            rows ++;
        }
    }
    
    public MyDataTable( ResultSet rs, int pageSize, String DSEncoding, String DBEncoding ) throws SQLException
    {
    	ResultSetMetaData RSMetaData = rs.getMetaData();
    	columns = RSMetaData.getColumnCount();
    	//获得元数据
    	metaData = new MyMetaData[ columns ];
    	for( int i=0; i < columns; i ++ )
    	{
    		MyMetaData tempMeta = new MyMetaData();
    		tempMeta.setColumnName( RSMetaData.getColumnName( i+1 ).toLowerCase() );
    		tempMeta.setColumnType( RSMetaData.getColumnType( i+1 ) );
    		tempMeta.setColumnSize( RSMetaData.getColumnDisplaySize( i+1 ) );
    		metaData[i] = tempMeta;
    	}
    	//获取数据
    	data = new ArrayList( pageSize );
    	int rowNumber = 0;
        while( rs.next() && rowNumber < pageSize )
        {
        	Object [] row = new Object[ columns ];
            for( int i = 0; i < columns; i ++ )
            {
            	int columnType = RSMetaData.getColumnType( i + 1 );
            	if( columnType == Types.CHAR || columnType == Types.VARCHAR || columnType == Types.LONGVARCHAR )
                {
            		String temp = rs.getString( i + 1 );
            		if( temp == null )
            		{
            			temp = "";
            		}else
            		{
            			try
						{
							temp = new String( temp.getBytes( DBEncoding ), DSEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
            		}
            		row[i] = temp;
                }else
                {
                	row[i] = rs.getObject( i + 1 );
                }
            }
            data.add( row );
            rows ++;
            rowNumber ++; 
        }
    }
    
    public Object [] getRowData( int row )
    {
    	Object [] row_data = null;
    	if( data != null && row > 0 && row <= rows )
    	{
    		row_data = (Object[]) data.get( row );
    	}
    	return row_data;
    }
   
    public List getData()
	{
		return data;
	}

	public void setData(List data)
	{
		this.data = data;
	}
	
	public Object getCellData()
	{
		Object cellData = null;
		if( currentRow > 0 && currentColumn > 0 )
		{
			cellData = ((Object[])data.get( currentRow-1 ))[currentColumn-1]; 
		}
		return cellData;
	}
	
	public Object getCellData( int row, int column )
	{
		Object cellData = null;
		if( row > 0 && row <= rows && column > 0 && column <= columns )
		{
			currentRow = row;
			currentColumn = column;
			cellData = ((Object[])data.get( row-1 ))[column-1]; 
		}
		return cellData;
	}
	
	public Object getCellData( int row, String columnName )
	{
		Object cellData = null;
		if( row > 0 && row <= rows )
		{
			int column = getColumnNumber( columnName );
			if( column > 0 && column <= columns )
			{
				currentRow = row;
				currentColumn = column;
				cellData = ((Object[])data.get(row-1))[column-1]; 
			}
		}
		if( cellData == null )
		{
			cellData = "";
		}
		return cellData;
	}
	
	public boolean setCellData( Object cellData )
	{
		boolean flag = false;
		if( currentRow > 0 && currentColumn > 0 )
		{
			((Object[])data.get( currentRow ))[currentColumn] = cellData; 
			flag = true;
		}
		return flag;
	}
	
	public boolean setCellData( int row, int column, Object cellData )
	{
		boolean flag = false;
		if( row > 0 && row <= rows && column > 0 && column <= columns )
		{
			currentRow = row;
			currentColumn = column;
			((Object[])data.get( row -1 ))[ column-1 ] = cellData; 
			flag = true;
		}
		return flag;
	}
	
	public boolean setCellData( int row, String columnName, Object cellData )
	{
		boolean flag = false;
		if( row > 0 && row <= rows )
		{
			int column = getColumnNumber( columnName );
			if( column > 0 && column <= columns )
			{
				currentRow = row;
				currentColumn = column;
				((Object[])data.get( row-1 ))[ column-1 ] = cellData; 
				flag = true;
			}
		}
		return flag;
	}
	
	private int getColumnNumber( String columnName )
	{
		int column = 0;
		if( columnName != null && !"".equals( columnName ))
		{
			for( int i=0; i < columns; i ++ )
			{
				if( columnName.hashCode() == metaData[i].getColumnName().hashCode() )
				{
					column = i+1;
					break;
				}
			}
		}
		return column;
	}
	
	public String getColumnName( int column )
	{
		String name = null;
		if( column > 0 && column <= columns )
		{
			name = metaData[column-1].getColumnName();
		}
		return name;
	}

	public MyMetaData getMetaData( int column )
	{
		return metaData[column];
	}
	
	public void setMetaData( int column, MyMetaData mmd )
	{
		if( column > 0 && column <= columns )
		{
			metaData[column] = mmd;
		}
	}
	
	public boolean ForwardRow()
	{
		boolean flag = false;
		if( currentRow > 0 )
		{
			currentRow --;
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
	
	public boolean NextRow()
	{
		boolean flag = false;
		if( currentRow < rows )
		{
			currentRow ++;
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
	
	public boolean LastRow()
	{
		boolean flag = false;
		if( rows > 0 )
		{
			currentRow = rows;
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
	
	public boolean FirstRow()
	{
		boolean flag = false;
		if( rows > 0 )
		{
			currentRow = 1;
			flag = true;
		}
		return flag;
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
	
	public void BeforeFirstRow()
	{
		currentRow = 0;
	}
	
	public void BeforeFirstColumn()
	{
		currentColumn = 0;
	}

	public MyMetaData [] getMetaData()
    {
    	return metaData;
    }
    
    public void setMetaData( MyMetaData [] metadata )
    {
    	this.metaData = metadata;
    }
    
    public void setRows( int rows )
    {
    	this.rows = rows;
    }
    
    public int getRows()
    {
    	return rows;
    }
    
    public int getColumns()
    {
    	return columns;
    }
    
    public void setColumns( int columns )
    {
    	this.columns = columns;
    }

	public int getCurrentColumn()
	{
		return currentColumn;
	}

	public void setCurrentColumn( int currentColumn )
	{
		this.currentColumn = currentColumn;
	}

	public int getCurrentRow()
	{
		return currentRow;
	}

	public void setCurrentRow( int currentRow )
	{
		this.currentRow = currentRow;
	}
}
