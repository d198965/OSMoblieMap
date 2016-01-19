package org.osmdroid.shape.geom;

import android.os.Parcel;

/**
 * @version 1.0
 */
public class Extent implements Area {
    /**
     */
    protected CPoint mleftdown = null;

    /**
     *
     */
    protected CPoint mrightup = null;

    /**
     * @param leftdown
     * @param rightup
     */
    public Extent(CPoint leftdown, CPoint rightup) {
        this.mleftdown = new CPoint(leftdown.x, leftdown.y);
        this.mrightup = new CPoint(rightup.x, rightup.y);

    }

    /**
     * @param minx
     * @param miny
     * @param maxx
     * @param maxy
     */

    public Extent(double minx, double miny, double maxx, double maxy) {
        this(new CPoint(minx, miny), new CPoint(maxx, maxy));
    }

    /**
     * @return
     */
    public CPoint getLeftdown() {
        return this.mleftdown;
    }

    public void setLeftdown(CPoint the_mleftdown) {
        this.mleftdown = the_mleftdown;
    }

    /**
     * @return
     */
    public CPoint getRightup() {
        return this.mrightup;
    }

    public void setRightup(CPoint the_mrightup) {
        this.mrightup = the_mrightup;
    }

    /**
     * @return
     */
    public CPoint getLeftUp() {
        return new CPoint(this.mleftdown.x, this.mrightup.y, this.mrightup.z);
    }

    /**
     * @return
     */
    public CPoint getRightDown() {
        return new CPoint(this.mrightup.x, this.mleftdown.y, this.mleftdown.z);
    }

    public double getMinX() {
        return this.mleftdown.x;
    }

    public double getMaxX() {
        return this.mrightup.x;
    }

    public double getMinY() {
        return this.mleftdown.y;
    }

    public double getMaxY() {
        return this.mrightup.y;
    }

    public double getMaxZ() {
        return this.mrightup.z;
    }

    public double getMinZ() {
        return this.mleftdown.z;
    }

    /**
     * @param translatex
     * @param translatey
     * @return
     */
    public Extent translate(double translatex, double translatey) {
        CPoint leftdown = new CPoint(this.getLeftdown().x - translatex,
                this.getLeftdown().y - translatey, 0);
        CPoint rightup = new CPoint(this.getRightup().x - translatex,
                this.getRightup().y - translatey, 0);
        return new Extent(leftdown, rightup);
    }

    public Extent translate(double translatex, double translatey, double translatez) {
        CPoint leftdown = new CPoint(this.getLeftdown().x - translatex,
                this.getLeftdown().y - translatey,
                this.getLeftdown().z - translatez);
        CPoint rightup = new CPoint(this.getRightup().x - translatex,
                this.getRightup().y - translatey,
                this.getRightup().z - translatez);
        return new Extent(leftdown, rightup);
    }

    /**
     * @param rate
     * @return
     */
    public Extent changeExtent(double rate) {
        double dw = this.getWidth() * (rate - 1) / 2; //delta width

        double dh = this.getHeight() * (rate - 1) / 2; //delta height

        CPoint nld = new CPoint(this.mleftdown);
        nld.x -= dw;
        nld.y -= dh;

        CPoint nru = new CPoint(this.mrightup);
        nru.x += dw;
        nru.y += dh;
        return new Extent(nld, nru);
    }

    /**
     * @return ����
     * @todo ��Extent����ת��Ϊһ�����ĸ����Ring
     */
    public Ring toRing() {
        CPoint leftdown = this.getLeftdown();
        CPoint rightup = this.getRightup();
        CPoint rightdown = new CPoint(rightup.x, leftdown.y, 0);
        CPoint leftup = new CPoint(leftdown.x, rightup.y, 0);

        Ring r = new Ring();
        r.add(leftdown);
        r.add(rightdown);
        r.add(rightup);
        r.add(leftup);
        return r;
    }

    /**
     * @return
     */
    public double getWidth() {
        return Math.abs(mrightup.x - mleftdown.x);
    }

    /**
     * @return
     */
    public double getHeight() {
        return Math.abs(mrightup.y - mleftdown.y);
    }

    /**
     * @param x
     * @param y
     * @return true or false
     */
    public boolean contains(double x, double y) {
        return contains(new CPoint(x, y, 0));
    }

    public boolean contains(double x, double y, double z) {
        return contains(new CPoint(x, y, z));
    }

    /**
     * @param point
     * @return true or false
     */
    public boolean contains(CPoint point) {
        return (mleftdown.x <= point.x && point.x <= mrightup.x)
                && (mleftdown.y <= point.y && point.y <= mrightup.y)
                && (mleftdown.z <= point.z && point.z <= mrightup.z);
    }

    /**
     * @param x
     * @param y
     * @param offset
     * @return
     */
    public boolean hitTest(double x, double y, double offset) {
        return this.contains(x, y);
    }

    /**
     * @param point
     * @param offset
     * @return
     */
    public boolean hitTest(CPoint point, double offset) {
        return hitTest(point.x, point.y, offset);
    }

    public Extent getExtent() {
        return this;
    }

    /**
     * @return true or false
     */
    public boolean equals(Object obj) {
        if (obj instanceof Extent) {
            Extent ext = (Extent) obj;
            return (ext.getLeftdown().equals(this.getLeftdown())
                    && ext.getRightup().equals(this.getRightup()));
        } else if (obj instanceof Ring) {
            return this.toRing().equals((Ring) obj);
        } else {
            return false;
        }
    }

    public String toString() {
        return "Extent :(" + this.mleftdown.x + "," + this.mrightup.z + "," +
                this.mleftdown.y + "," + this.mrightup.y +
                this.mleftdown.z + "," + this.mrightup.z + ")";
    }

    /**
     * @param extent
     * @return true or false
     */
    public boolean covers(Extent extent) {
        return ((this.getMinX() <= extent.getMinX())
                && (this.getMinY() <= extent.getMinY())
                && (this.getMaxX() >= extent.getMaxX())
                && (this.getMaxY() >= extent.getMaxY())
                && (this.getMinZ() <= extent.getMinZ())
                && (this.getMaxZ() >= extent.getMaxZ()));

    }

    public Extent getUnion(Extent objTemp) {
        if (objTemp == null || this.covers(objTemp)) {
            return this;
        } else if (objTemp.covers(this)) {
            return objTemp;
        } else {
            double minX = this.getMinX() < objTemp.getMinX() ? this.getMinX() : objTemp.getMinX();
            double minY = this.getMinY() < objTemp.getMinY() ? this.getMinY() : objTemp.getMinY();
            double maxY = this.getMaxY() > objTemp.getMaxY() ? this.getMaxY() : objTemp.getMaxY();
            double maxX = this.getMaxX() > objTemp.getMaxX() ? this.getMaxX() : objTemp.getMaxX();
            return new Extent(minX,minY,maxX,maxY);
        }
    }

    /**
     * @param objTemp
     * @return
     */
    public Extent getIntersection(Extent objTemp) {
        Extent extObj = null;
        if (!this.intersected(objTemp)) {
            return null;
        }
        if (this.covers(objTemp)) {
            extObj = objTemp;
        } else if (objTemp.covers(this)) {
            extObj = this;
        } else {
            CPoint startPoint = null;
            CPoint endPoint = null;
            LineSegment lsOfSelf1 = new LineSegment(new CPoint(this.getMinX(),
                    this.getMinY(), 0), new CPoint(this.getMaxX(), this.getMinY(), 0)),
                    lsOfSelf2 = new LineSegment(new CPoint(this.getMaxX(),
                            this.getMinY(), 0), new CPoint(this.getMaxX(), this.getMaxY(), 0)),
                    lsOfSelf3 = new LineSegment(new CPoint(this.getMaxX(),
                            this.getMaxY(), 0), new CPoint(this.getMinX(), this.getMaxY(), 0)),
                    lsOfSelf4 = new LineSegment(new CPoint(this.getMinX(),
                            this.getMaxY(), 0), new CPoint(this.getMinX(), this.getMinY(), 0));


            LineSegment lsOfObj1 = new LineSegment(new CPoint(objTemp.getMinX(),
                    this.getMinY(), 0), new CPoint(this.getMaxX(), this.getMinY(), 0)),
                    lsOfObj2 = new LineSegment(new CPoint(objTemp.getMaxX(),
                            objTemp.getMinY(), 0),
                            new CPoint(objTemp.getMaxX(),
                                    objTemp.getMaxY(), 0)),
                    lsOfObj3 = new LineSegment(new CPoint(objTemp.getMaxX(),
                            objTemp.getMaxY(), 0),
                            new CPoint(objTemp.getMinX(),
                                    objTemp.getMaxY(), 0)),
                    lsOfObj4 = new LineSegment(new CPoint(objTemp.getMinX(),
                            objTemp.getMaxY(), 0),
                            new CPoint(objTemp.getMinX(),
                                    objTemp.getMinY(), 0));
        }
        return extObj;
    }

    /**
     * @param objTemp
     * @return
     */
    public Extent[] getDivisionOfSelf(Extent objTemp) {
        return null;
    }

    /**
     * @param objTemp
     * @return
     */
    private Extent[] getDivision(Extent objTemp) {
        java.util.Vector vecExtObj = new java.util.Vector(10, 10);

        Extent extObj[] = new Extent[vecExtObj.size()];
        return extObj;
    }

    /**
     * @param extent1
     * @return true or false
     */
    public boolean intersected(Extent extent1) {

        boolean notIntersected =
                this.getMaxX() < extent1.getMinX() //left
                        || this.getMinX() > extent1.getMaxX() //right
                        || this.getMaxY() < extent1.getMinY()
                        || this.getMinY() > extent1.getMaxY()
                        || this.getMaxZ() < extent1.getMinZ()
                        || this.getMinZ() > extent1.getMaxZ();
        return !notIntersected || this.covers(extent1) || extent1.covers(this);
    }

    public boolean inExtent(CPoint thePoint) {
        boolean notIntersected =
                thePoint.x > this.getMaxX() ||
                        thePoint.x < this.getMinX() ||
                        thePoint.y > this.getMaxY() ||
                        thePoint.y < this.getMinY();
        return !notIntersected;
    }

    /**
     * @param ext
     * @return
     */
    public Extent getMaxExtent(Extent ext) {
        if (ext == null) {
            return this;
        } else {
            return new Extent(
                    Math.min(this.getMinX(), ext.getMinX())
                    , Math.min(this.getMinY(), ext.getMinY())
                    , Math.max(this.getMaxX(), ext.getMaxX())
                    , Math.max(this.getMaxY(), ext.getMaxY())
            );
        }
    }

    /**
     */
    public double getArea() {
        return this.getWidth() * this.getHeight();
    }

    /**
     * @return
     */
    public Object clone() {
        return new Extent(this.getMinX(), this.getMinY(), this.getMaxX(),
                this.getMaxY());
    }

    @Override
    public double areaVaue() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static final Creator<Extent> CREATOR =
            new Creator<Extent>() {
                @Override
                public Extent createFromParcel(Parcel in) {
                    if (in == null) {
                        return null;
                    }
                    CPoint mleftdownpCPoint = CPoint.CREATOR.createFromParcel(in);
                    CPoint mrightupCPoint = CPoint.CREATOR.createFromParcel(in);
                    return new Extent(mleftdownpCPoint, mrightupCPoint);
                }

                @Override
                public Extent[] newArray(int size) {
                    return new Extent[size];
                }
            };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        mleftdown.writeToParcel(dest, flags);
        mrightup.writeToParcel(dest, flags);
    }
}