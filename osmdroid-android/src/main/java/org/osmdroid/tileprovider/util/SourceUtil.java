package org.osmdroid.tileprovider.util;

/**
 * Created by zdh on 15/12/11.
 */
public class SourceUtil {
    public static final int MAX_TYPE = 8;

    public static final int CLOUDMADE_TILE_SOURCE_TYPE = 1; // 不能改变
    public static final int XY_TILE_SOURCE_TYPE = 2;   // 不能改变
    public static final int MAP_BOX_TILE_SOURCE_TYPE = 4;  // 不能改变
    public static final int QUAD_TREE_SOURCE_TYPE = MAX_TYPE; // 不能改变

    //每个的SourceID位数不能超过 32 - LEFT_OFFSET
    public static final int MAX_LEFT_OFFSET = findMaxPosition(MAX_TYPE);//最高Type

    public static int findMaxPosition(final int n) {
        int position = 0;
        int m = n;
        while (m > 0) {
            m = m >> 1;
            if (m > 0) {
                position++;
            }
        }
        return position;
    }

    public static int findMinPosition(final long n){
        //获取SourceID二进制的第一个1
        int k = 1;
        int position = 0;
        while (true){
            if ((n & k) > 0){
                break;
            }
            k = k << 1;
            position++;
        }
        return position;
    }
}
