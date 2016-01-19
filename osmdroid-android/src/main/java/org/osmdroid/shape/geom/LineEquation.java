package org.osmdroid.shape.geom;

/**
 */
public class LineEquation
{
    /**
     *
     */
    double k;

    /**
     */
    double b;

    /**
     * @param k
     * @param b
     */
    public LineEquation( double k, double b )
    {
        this.k = k;
        this.b = b;
    }

    /**
     * @param p1
     * @param p2
     */
    public LineEquation( CPoint p1, CPoint p2 )
    {
        if ( p1.getX() == p2.getX() )
        {
            this.k = Double.NaN;
            this.b = p1.getX();
        }
        else
        {
            if ( p1.getY() == p2.getY() )
            {
                this.k = 0;
                this.b = p1.getY();
            }
            else
            {
                this.k = ( p1.getY() - p2.getY() ) / ( p1.getX() - p2.getX() );
                this.b = p1.getY() - k * p1.getX();
            }
        }
    }

    /**
     * @param k
     * @param p
     */
    public LineEquation( double k, CPoint p )
    {
        if ( Double.isNaN( k ) )
        {
            this.k = Double.NaN;
            this.b = p.x;
        }
        else
        {
            if ( k == 0 )
            {
                this.k = 0;
                this.b = p.y;
            }
            else
            {
                this.k = k;
                this.b = p.getY() - k * p.getX();
            }
        }
    }

    /**
     * @param line2
     * @return
     */
    public CPoint getIntersection( LineEquation line2 )
    {
        if ( this.k == line2.k )
        {
            return new CPoint( Double.NaN, Double.NaN,Double.NaN);
        }

        if ( Double.isNaN( this.k ) )
        {
            if ( line2.k == 0 )
            {
                return new CPoint( this.b, line2.b,0);
            }
            else
            {
                return new CPoint( this.b, line2.getY( this.b ),0);
            }
        }
        else if ( this.k == 0 )
        {
            if ( Double.isNaN( line2.k ) )
            {
                return new CPoint( line2.b, this.b,0 );
            }
            else
            {
                return new CPoint( line2.getX( this.b ), this.b ,0);
            }
        }
        else
        {
            double x = ( line2.b - b ) / ( k - line2.k );
            double y = getY( x );
            return new CPoint( x, y,0 );
        }
    }

    /**
     * ���x�õ�y;
     * @param y
     * @return
     */
    public double getX( double y )
    {
        return ( y - b ) / k;
    }

    /**
     * ���y�õ�x
     */
    public double getY( double x )
    {
        return k * x + b;
    }

    /**
     */
    public LineEquation getLineEquationByDistance( double distance )
    {
        if ( Double.isNaN( this.k ) )
        {
            return new LineEquation( Double.NaN, this.b + distance );
        }
        else if ( this.k == 0 )
        {
            return new LineEquation( 0, this.b + distance );
        }
        else
        {
            int factor = 1;
            double offsetB = distance * Math.sqrt( 1 + k * k ) * factor;
            return new LineEquation( k, b + offsetB );
        }
    }

    /**
     */
    public boolean isBelow( LineEquation line2 )
    {
        return b < line2.b;
    }


    /**
     * @return
     */
    public double getSlope()
    {
        return this.k;
    }

    /**
     */
    public double getVerticalSlope()
    {
        if ( Double.isNaN( this.k ) )
        {
            return 0;
        }
        else if ( this.k == 0 )
        {
            return Double.NaN;
        }
        else
        {
            double arc = Math.PI / 180;
            return Math.tan( ( Math.atan( k ) / arc + 90 ) * arc );
        }
    }
}