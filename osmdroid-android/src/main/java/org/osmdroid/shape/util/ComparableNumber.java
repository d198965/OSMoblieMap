package org.osmdroid.shape.util;

import java.lang.*;

/**
 *
 */

public class ComparableNumber implements java.lang.Comparable<java.lang.Comparable>
{
    private double num=0.0;

    public ComparableNumber(int i){
        this.num=i;
    }
    public ComparableNumber(double d){
        this.num=d;
    }
    public ComparableNumber(float f){
        this.num=f;
    }
    /**
     */
    public double getNumber(){
        return this.num;
    }

    @Override
    public int compareTo(java.lang.Comparable comparableNumber)
    {
        return (int)(this.num-((ComparableNumber)comparableNumber).getNumber());
    }
    public String toString(){
        return String.valueOf(this.num);
    }
}