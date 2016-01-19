package org.osmdroid.api.georeference;


public  class Datum {
	public String datumName;
	public double a;
	public double e;
	public double f;
	public Datum(String name,double a,double f) 
	{
		this.a = a;
		this.f = f;
		this.datumName = name;
		this.e = Math.sqrt(2*f-f*f);
	}	
}
