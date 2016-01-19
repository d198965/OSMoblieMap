package org.osmdroid.shape.geom;

public final class Crossings
{
    public static final boolean debug = false;
    int limit = 0;
    double yranges[] = null;
    double xlo = 0;
    double ylo = 0;
    double xhi = 0;
    double yhi = 0;

    public Crossings( double d, double d1, double d2, double d3 )
    {
        limit = 0;
        yranges = new double[10];
        xlo = d;
        ylo = d1;
        xhi = d2;
        yhi = d3;
    }

    public final double getXLo()
    {
        return xlo;
    }

    public final double getYLo()
    {
        return ylo;
    }

    public final double getXHi()
    {
        return xhi;
    }

    public final double getYHi()
    {
        return yhi;
    }

    public final boolean isEmpty()
    {
        return limit == 0;
    }

    public boolean accumulateLine( double d, double d1, double d2, double d3 )
    {
        if ( d1 <= d3 )
            return accumulateLine( d, d1, d2, d3, 1 );
        else
            return accumulateLine( d2, d3, d, d1, -1 );
    }

    public boolean accumulateLine( double d, double d1, double d2, double d3,
                                   int i )
    {
        if ( yhi <= d1 || ylo >= d3 )
            return false;
        if ( d >= xhi && d2 >= xhi )
            return false;
        if ( d1 == d3 )
            return d >= xlo || d2 >= xlo;
        double d8 = d2 - d;
        double d9 = d3 - d1;
        double d4;
        double d5;
        if ( d1 < ylo )
        {
            d4 = d + ( ( ylo - d1 ) * d8 ) / d9;
            d5 = ylo;
        }
        else
        {
            d4 = d;
            d5 = d1;
        }
        double d6;
        double d7;
        if ( yhi < d3 )
        {
            d6 = d + ( ( yhi - d1 ) * d8 ) / d9;
            d7 = yhi;
        }
        else
        {
            d6 = d2;
            d7 = d3;
        }
        if ( d4 >= xhi && d6 >= xhi )
            return false;
        if ( d4 > xlo || d6 > xlo )
        {
            return true;
        }
        else
        {
            record( d5, d7, i );
            return false;
        }
    }

    public void record( double d, double d1, int i )
    {
        if ( d >= d1 )
            return;
        int j;
        for ( j = 0; j < limit && d > yranges[j + 1]; j += 2 )
            ;
        int k = j;
        while ( j < limit )
        {
            double d2 = yranges[j++];
            double d3 = yranges[j++];
            if ( d1 < d2 )
            {
                yranges[k++] = d;
                yranges[k++] = d1;
                d = d2;
                d1 = d3;
                continue;
            }
            double d4;
            double d5;
            if ( d < d2 )
            {
                d4 = d;
                d5 = d2;
            }
            else
            {
                d4 = d2;
                d5 = d;
            }
            double d6;
            double d7;
            if ( d1 < d3 )
            {
                d6 = d1;
                d7 = d3;
            }
            else
            {
                d6 = d3;
                d7 = d1;
            }
            if ( d5 == d6 )
            {
                d = d4;
                d1 = d7;
            }
            else
            {
                if ( d5 > d6 )
                {
                    d = d6;
                    d6 = d5;
                    d5 = d;
                }
                if ( d4 != d5 )
                {
                    yranges[k++] = d4;
                    yranges[k++] = d5;
                }
                d = d6;
                d1 = d7;
            }
            if ( d >= d1 )
                break;
        }

        if ( k < j && j < limit )
            System.arraycopy( yranges, j, yranges, k, limit - j );
        k += limit - j;
        if ( d < d1 )
        {
            if ( k >= yranges.length )
            {
                double ad[] = new double[k + 10];
                System.arraycopy( yranges, 0, ad, 0, k );
                yranges = ad;
            }
            yranges[k++] = d;
            yranges[k++] = d1;
        }
        limit = k;
    }

}