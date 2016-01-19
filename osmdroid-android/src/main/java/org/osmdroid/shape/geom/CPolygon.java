package org.osmdroid.shape.geom;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.shape.util.CCollection;
import org.osmdroid.shape.util.CIterator;
import org.osmdroid.shape.util.VectorCollection;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class CPolygon extends Ring implements CShape {
    /*new added begin*/
    Extent box = null;
    int[] partIndex;
    //CPoint[] pts = null;

    /*new added end*/

    /**
     */
    protected CCollection holes = new VectorCollection();

    /**
     *
     * @param xpoints
     * @param ypoints
     * @param npoints
     */
    public CPolygon(int[] xpoints, int[] ypoints, int npoints, int fid) {
        if (npoints < 0) {
            throw new NegativeArraySizeException();
        }

        int minPoints = npoints > xpoints.length ? xpoints.length : npoints;
        minPoints = minPoints > ypoints.length ? ypoints.length : minPoints;
        CPoint point;
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;
        for (int i = 0; i < minPoints; i++) {
            double temx = xpoints[i];
            double temy = ypoints[i];
            point = new CPoint(xpoints[i], ypoints[i], 0);
            if (temx <= minx) {
                minx = temx;
            }
            if (temx >= maxx) {
                maxx = temx;
            }
            if (temy <= miny) {
                miny = temy;
            }
            if (temy >= maxy) {
                maxy = temy;
            }

            collection.add(point);
        }
        box = new Extent(minx, miny, maxx, maxy);
    }

    public CPolygon(int[] xpoints, int[] ypoints, int[] zpoints, int npoints, int fid) {
        if (npoints < 0) {
            throw new NegativeArraySizeException();
        }

        int minPoints = npoints > xpoints.length ? xpoints.length : npoints;
        minPoints = minPoints > ypoints.length ? ypoints.length : minPoints;
        CPoint point;
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;
        for (int i = 0; i < minPoints; i++) {
            double temx = xpoints[i];
            double temy = ypoints[i];
            point = new CPoint(xpoints[i], ypoints[i], zpoints[i]);
            if (temx <= minx) {
                minx = temx;
            }
            if (temx >= maxx) {
                maxx = temx;
            }
            if (temy <= miny) {
                miny = temy;
            }
            if (temy >= maxy) {
                maxy = temy;
            }
            //point = new CPoint( xpoints[i], ypoints[i],zpoints[i] );
            collection.add(point);
        }
        box = new Extent(minx, miny, maxx, maxy);
    }

    /**
     * �������ҳ�ʼ��һ��Polygon
     *
     * @param collection collection ��ļ���
     */
    public CPolygon(CCollection collection) {
        this.collection.addAll(collection);
    }

    public CPolygon() {
        //super();
    }


    /** new added begin*/
    /**
     * @param box
     * @param partIndex
     * @param pts
     */
    public CPolygon(Extent box, int[] partIndex, CPoint[] pts) {
        this.box = box;
        this.partIndex = partIndex;
        for (CPoint cPoint : pts) {
            collection.add(cPoint);
        }
    }

    public CPolygon(List<CPoint> pts) {
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;
        for (CPoint cPoint : pts) {
            collection.add(cPoint);
            double temx = cPoint.x;
            double temy = cPoint.y;
            if (temx <= minx) {
                minx = temx;
            }
            if (temx >= maxx) {
                maxx = temx;
            }
            if (temy <= miny) {
                miny = temy;
            }
            if (temy >= maxy) {
                maxy = temy;
            }
        }
        box = new Extent(minx, miny, maxx, maxy);
        this.partIndex = new int[1];
        this.partIndex[0] = 0;
    }

    public Extent getBox() {
        return box;
    }

    public Extent getExtent() {
        return box;
    }

    public int getNumParts() {
        return partIndex.length;
    }

    public int getNumPoints() {
        return collection.size();
    }

    public int[] getPartIndex() {
        return partIndex;
    }

    public CPoint[] getPts() {
        return (CPoint[]) collection.toArray();
    }

    public void setPartIndex(int[] partIndex) {
        this.partIndex = partIndex;
    }

    public void setPts(CPoint[] pts) {
        collection.clear();
        for (CPoint cPoint : pts) {
            collection.add(cPoint);
        }
    }

    /**
     * @param index
     * @return
     */
    public CPoint[] getPoints(int index) {
        int partIndex = this.partIndex[index];
        int nextPartPointIndex = 0;
        if (index == this.partIndex.length - 1) {
            nextPartPointIndex = getNumPoints();
        } else {
            nextPartPointIndex = this.partIndex[index + 1];
        }

        int count = nextPartPointIndex - this.partIndex[index]; //�����
        CPoint[] points = new CPoint[count];
        int j = 0;
        for (int i = this.partIndex[index]; i < nextPartPointIndex; i++) {
            points[j++] = (CPoint) this.collection.get(i);
        }
        return points;
    }

    public VectorCollection getListPoints(int index) {
        VectorCollection collection = new VectorCollection();
        int partIndex = this.partIndex[index];
        int nextPartPointIndex = 0;
        if (index == this.partIndex.length - 1) {
            nextPartPointIndex = getNumPoints();
        } else {
            nextPartPointIndex = this.partIndex[index + 1];
        }

        int count = nextPartPointIndex - this.partIndex[index]; //�����
        List<CPoint> points = new ArrayList<CPoint>();
        int j = 0;
        for (int i = this.partIndex[index]; i < nextPartPointIndex; i++) {
            collection.add((CPoint) this.collection.get(i));
        }
        return collection;
    }
    /** new added end*/

    /**
     * @return
     */
    public CCollection getHoles() {
        return holes;
    }

    /**
     * @param hole
     */
    public void addHole(Ring hole) {
        holes.add(hole);
    }

    /**
     * �Ƴ�
     */
    public void removeHole(Ring hole) {
        holes.remove(hole);
    }

    /**
     * ɾ�����е�Holes
     */
    public void removeAllHoles() {
        holes.clear();
    }

    public boolean contains(CPoint point) {
        return contains(point.x, point.y);
    }

    public boolean contains(double x, double y) {

        int[] partIndexs = this.partIndex;
        if (partIndexs == null) {
            partIndexs = new int[1];
            partIndexs[0] = 0;
        }

        boolean isIn = false;
        for (int i = 0; i < partIndexs.length; i++) {
            Ring ring;
            ring = new Ring(this.getListPoints(i));
            if (ring.contains(x, y)) {
                isIn = true;
                break;
            }
        }
        if (isIn) {
            Ring ring;
            CIterator it = holes.iterator();
            while (it.hasNext()) {
                ring = (Ring) it.next();
                if (ring.contains(x, y)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        CIterator it = collection.iterator();
        CPoint pt;
        String s = "Polygon include " + collection.size() + " points:\n";
        while (it.hasNext()) {
            pt = (CPoint) it.next();
            s += "\tx=" + pt.getX() + "\ty=" + pt.getY() + "\n";
        }
        if (holes.size() > 0) {
            s += "include " + holes.size() + " holes:\n";
            it = holes.iterator();
            Ring ring;
            int i = 1;
            while (it.hasNext()) {
                s += "\tHoles " + i + "\n:";
                ring = (Ring) it.next();
                CIterator it2 = (ring.getPoints()).iterator();
                while (it2.hasNext()) {
                    pt = (CPoint) it2.next();
                    s += "x=" + pt.getX() + "\ty=" + pt.getY() + "\n";
                }
                i++;
            }
        } else {
            s += "\tinclude 0 holes:\n";
        }
        return s;
    }

    public boolean equals(Object obj) {
        boolean bequals = false;
        if (obj instanceof CPolygon) {
            CPolygon plg = (CPolygon) obj;
            bequals = this.getPoints() == plg.getPoints()
                    && this.getHoles() == plg.getHoles();
        } else if (obj instanceof Ring) {
            if (this.getHoles().size() == 0) {
                bequals = this.getPoints() == ((Ring) obj).getPoints();
            }
        }
        return bequals;
    }

    public double length() {
        return 0;
    }

    public String pointString() {
        String theString = "";
        for (int i = 0; i < collection.size(); i++) {
            CPoint thePoint = (CPoint) collection.get(i);
            theString += thePoint.x + "," + thePoint.y;
            if (i < collection.size() - 1) {
                theString += "\t";
            }
        }
        return theString;
    }

    @Override
    public double areaVaue() {
        double area = 0;
        if (partIndex == null || partIndex.length <= 0) {
            area = super.areaVaue();
        }
        for (int k = 0; k < partIndex.length; k++) {
            area += new Ring(getListPoints(partIndex[k])).areaVaue();
        }
        return area;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return super.describeContents();
    }

    public static final Parcelable.Creator<CPolygon> CREATOR =
            new Parcelable.Creator<CPolygon>() {
                @Override
                public CPolygon createFromParcel(Parcel in) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public CPolygon[] newArray(int size) {
                    return new CPolygon[size];
                }
            };
}