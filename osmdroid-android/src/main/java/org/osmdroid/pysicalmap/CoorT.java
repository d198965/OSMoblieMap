package org.osmdroid.pysicalmap;

import org.osmdroid.shape.geom.Extent;

/**
 * Created by zdh on 15/12/18.
 */
public class CoorT {
    public Extent picRect;//
    public  Integer startX,startY;//
    public  double blC,blL,blO;//
    public  int picWide = 800,picHigh = 800;//
    public  double MapWide=800,MapHeight = 800;//
    public  int picCenteralX = 400 ,picCenteralY = 400;
    public  int orpicCenteralX = 400 ,orpicCenteralY = 400;
    public CoorT()
    {
        startX =0;
        startY =0 ;
        blC = 1;
        blL =1;
        blO=1;
    }
}
