package baidumapsdk.demo.search.baiduPath;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;

import baidumapsdk.demo.R;

/**
 * 覆盖物管理
 * 起点和终点图标
 * Created by Administrator on 2016/6/22.
 */
public class PathOverlayManager {
    private boolean useDefaultIcon;
    private static PathOverlayManager pathIcon;
    private OverlayManager overlayManager;

    public static PathOverlayManager getInstance() {
        if (pathIcon == null) {
            pathIcon = new PathOverlayManager();
        }
        return pathIcon;
    }

    //获取 覆盖物 管理
    public static OverlayManager getOverlayManager() {
        return getInstance().overlayManager;
    }

    //获取图片 状态： true 自定义；false 百度自带的
    public static boolean getIconState() {
        return getInstance().useDefaultIcon;
    }

    //修改 标志 图标
    public static void setIconChange(boolean useDefaultIcon) {
        getInstance().useDefaultIcon = useDefaultIcon;
    }

    public static MyWalkingRouteOverlay getWalkingRouteOverlay(BaiduMap baiduMap) {
        return getInstance().new MyWalkingRouteOverlay(baiduMap);
    }

    //步行路线 图标
    public class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
            overlayManager = this;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    public static MyTransitRouteOverlay getTransitRouteOverlay(BaiduMap baiduMap) {
        return getInstance().new MyTransitRouteOverlay(baiduMap);
    }

    //换乘路线图标
    public class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
            overlayManager = this;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    public static DrivingRouteOverlay getDrivingRouteOverlay(BaiduMap baiduMap) {
        return getInstance().new MyDrivingRouteOverlay(baiduMap);
    }

    //驾车路线图标
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
            overlayManager = this;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    public static MyBikingRouteOverlay getBikingRouteOverlay(BaiduMap baiduMap) {
        return getInstance().new MyBikingRouteOverlay(baiduMap);
    }

    //骑行路线图标
    public class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
            overlayManager = this;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }
}
