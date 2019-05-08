package com.ripple.datasource.exception;

public class MetadataException extends Exception
{
    
    /**
     * 
     */
    public MetadataException()
    {
        super();
    }
    /**
     * @param message
     */
    public MetadataException(String message)
    {
        super(message);
    }
    /**
     * @param message
     * @param originalException
     */
    public MetadataException(String message, Exception originalException)
    {
        super(message, originalException);
    }
}
