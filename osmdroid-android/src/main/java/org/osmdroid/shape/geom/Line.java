package org.osmdroid.shape.geom;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.shape.util.CCollection;
import org.osmdroid.shape.util.CIterator;

import java.util.Vector;


/**
 *
 * @author soft2com
 * @version 1.0
 */
public class Line extends AbstractShape {
    public Line() {
    }

    public Line(Line line) {
        this(line.getPoints());
    }

    public Line(CCollection points) {
        CIterator it = points.iterator();
        while (it.hasNext()) {
            super.add(new CPoint((CPoint) it.next()));
        }
    }

    /**
     *
     */
    public Line mergeSamePoint() {
        if (super.size() <= 1) {
            return this;
        }
        CIterator it = this.iterator();
        Line line = new Line();
        CPoint prept = null,
                pt = null;
        prept = pt = (CPoint) it.next();
        line.add(pt);
        while (it.hasNext()) {
            pt = (CPoint) it.next();
            if (!prept.equals(pt)) {
                prept = pt;
                line.add(pt);
            }
        }
        return line;
    }

    /**
     * @param lineWidth
     * @return
     * @todo
     */
    public Ring getWrapRing(double lineWidth) {
        Line l = this.mergeSamePoint(); //�ϲ��ظ��ĵ㣬������ƽ����ʱ����
        if (l.size() == 0) {
            return null;
        }

        if (l.size() == 1) {
            CPoint pt = (CPoint) l.iterator().next();
            Extent ext = new Extent(new CPoint(pt.x - lineWidth,
                    pt.y - lineWidth, 0),
                    new CPoint(pt.x + lineWidth,
                            pt.y + lineWidth, 0));
            return ext.toRing();
        }
        Object[] points = l.toArray();

        Vector points1 = new Vector(this.size());
        Ring ring = new Ring();

        LineSegment
                cls = null //currentLineSegment
                , cals = null //currentAboveLineSegment
                , cbls = null //currentBelowLineSegment
                , pls = null //nextLineSegment
                , pals = null //nextAboveLineSegment
                , pbls = null; //nextBelowLineSegment

        CPoint aip = null //aboveIntersetionPoint
                , bip = null; //belowIntersetionPoint

        cls = new LineSegment((CPoint) points[0], (CPoint) points[1]);
        cals = cls.getParallelLineSegment(lineWidth);
        cbls = cls.getParallelLineSegment(-lineWidth);
        ring.add(cals.startPoint);
        points1.addElement(cbls.startPoint);

        for (int i = 1; i < points.length - 1; i++) {
            pls = cls;
            pals = cals;
            pbls = cbls;

            cls = new LineSegment((CPoint) points[i], (CPoint) points[i + 1]);
            cals = cls.getParallelLineSegment(lineWidth);
            cbls = cls.getParallelLineSegment(-lineWidth);

            aip = cals.getIntersectionPoint(pals);
            if (aip == null) {
                ring.add(pals.endPoint);
                ring.add(cals.startPoint);
            } else {
                ring.add(aip);
            }

            bip = cbls.getIntersectionPoint(pbls);
            if (bip == null) {
                points1.addElement(pbls.endPoint);
                points1.addElement(cbls.startPoint);
            } else {
                points1.addElement(bip);
            }
        }
        ring.add(cals.endPoint);
        ring.add(cbls.endPoint);

        for (int i = points1.size() - 1; i >= 0; i--) {
            if (points1.elementAt(i) != null) {
                ring.add(points1.elementAt(i));
            }
        }
        return ring;
    }

    /**
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj instanceof Line) {
            return ((Line) obj).getPoints().equals(this.getPoints());
        } else {
            return false;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Line include " + collection.size() + " point\n");
        CIterator it = collection.iterator();
        int i = 0;
        CPoint point = null;
        while (it.hasNext()) {
            i++;
            point = (CPoint) it.next();
            sb.append("\tpoint " + i + " x=" + point.x + "\ty=" + point.y +
                    "\n");
        }
        return sb.toString();
    }

    /**
     * @return
     */
    public double getLength() {
        CIterator it = collection.iterator();
        CPoint cpoint = null;
        CPoint nextPoint = null;
        double length = 0.0;
        if (it.hasNext()) {
            cpoint = (CPoint) it.next();
        }

        while (it.hasNext()) {
            nextPoint = (CPoint) it.next();
            if (cpoint == null){
                continue;
            }
            length += cpoint.getDistance(nextPoint);
            cpoint = nextPoint;
        }
        return length;
    }

    // 应按照顺序添加点
    protected void addPoint(double x,double y,double z){
        add(new CPoint(x,y,z));
    }

    @Override
    public void add(Object object) {
        if (object instanceof CPoint){
            super.add(object);
        }
    }

    public boolean hitTest(double x, double y, double offset )
    {
        return this.getWrapRing( offset ).contains( x, y );
    }

    @Override
    public boolean hitTest(CPoint point, double offset) {
        return this.getWrapRing( offset ).contains( point.x, point.y);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

    }
    public static final Parcelable.Creator<Line> CREATOR =
            new Parcelable.Creator<Line>() {
                @Override
                public Line createFromParcel(Parcel in) {
                    // TODO Auto-generated method stub
                    return  null;
                }

                @Override
                public Line[] newArray(int size) {
                    return new Line[size];
                }
            };

}