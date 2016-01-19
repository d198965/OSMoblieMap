package org.osmdroid.api;

public class KeyPairValue
{
	private String key ;
	private Object value;
	public KeyPairValue(String key,Object value)
	{
		this.key = key;
		this.value = value;
	}
	public String getKey()
	{
		return key;
	}
	public Object getValue()
	{
		return value;
	}
}