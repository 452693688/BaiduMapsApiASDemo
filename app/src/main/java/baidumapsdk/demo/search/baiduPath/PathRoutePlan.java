package baidumapsdk.demo.search.baiduPath;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;

import baidumapsdk.demo.R;

/**
 * 此demo用来展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 * 同时展示如何进行节点浏览并弹出泡泡
 */
public class PathRoutePlan extends Activity implements BaiduMap.OnMapClickListener {
    // 浏览路线节点相关
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;

    private TextView popupText = null; // 泡泡view

    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null;    // 地图View
    BaiduMap mBaidumap = null;
    // 搜索相关

    TransitRouteResult nowResult = null;
    DrivingRouteResult nowResultd = null;
    //路线搜索
    private PathSearch pathShow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routeplan);
        CharSequence titleLable = "路线规划功能";
        setTitle(titleLable);
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
         // 初始化搜索模块，注册事件监听
        pathShow = new PathSearch(this);
        pathShow.setOnResultPath(new ResultPath());

    }

    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        // 重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        // 处理搜索按钮响应
        EditText editSt = (EditText) findViewById(R.id.start);
        EditText editEn = (EditText) findViewById(R.id.end);
        // 设置起终点信息，对于transit(公交路线搜索) search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", editSt.getText().toString());
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("杭州", editEn.getText().toString());
        // 实际使用中请对起点终点城市进行正确的设定
        int viewId = v.getId();
        switch (viewId) {
            case R.id.drive:
                pathShow.searchPath(0, stNode, enNode);
                break;
            case R.id.transit:
                pathShow.searchPath(1, stNode, enNode);
                break;
            case R.id.walk:
                pathShow.searchPath(2, stNode, enNode);
                break;
            case R.id.bike:
                pathShow.searchPath(3, stNode, enNode);
                break;
        }

    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        } else if (step instanceof BikingRouteLine.BikingStep) {
            nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
            nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(PathRoutePlan.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

    }

    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */
    public void changeRouteIcon(View v) {
        OverlayManager routeOverlay = PathOverlayManager.getOverlayManager();
        if (routeOverlay == null) {
            return;
        }
        boolean useDefaultIcon = PathOverlayManager.getIconState();
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(this,
                    "将使用系统起终点图标",
                    Toast.LENGTH_SHORT).show();

        } else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(this,
                    "将使用自定义起终点图标",
                    Toast.LENGTH_SHORT).show();

        }
        useDefaultIcon = !useDefaultIcon;
        PathOverlayManager.setIconChange(useDefaultIcon);
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        pathShow.onDestroy();
        mMapView.onDestroy();
        super.onDestroy();
    }

    class ResultPath implements PathSearch.OnResultPath {

        @Override
        public void onResultPath(RouteLine route, int type) {

            Log.e("type:" + type, "Title:" + route.getTitle() + " state:" + route.getStarting());
            switch (type) {
                case 0:
                    //驾车路线搜索
                    DrivingRouteOverlay drivingRouteOverlay = PathOverlayManager.getDrivingRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(drivingRouteOverlay);
                    drivingRouteOverlay.setData((DrivingRouteLine) route);
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    break;
                case 1:
                    //公交路线搜索
                    TransitRouteOverlay transitRouteOverlay = PathOverlayManager.getTransitRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(transitRouteOverlay);
                    transitRouteOverlay.setData((TransitRouteLine) route);
                    transitRouteOverlay.addToMap();
                    transitRouteOverlay.zoomToSpan();
                    break;
                case 2:
                    //步行路线搜索
                    WalkingRouteOverlay walkingRouteOverlay = PathOverlayManager.getWalkingRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(walkingRouteOverlay);
                    walkingRouteOverlay.setData((WalkingRouteLine) route);
                    walkingRouteOverlay.addToMap();
                    walkingRouteOverlay.zoomToSpan();
                    break;
                case 3:
                    //骑行路线搜索
                    BikingRouteOverlay bikingRouteOverlay = PathOverlayManager.getBikingRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(bikingRouteOverlay);
                    bikingRouteOverlay.setData((BikingRouteLine) route);
                    bikingRouteOverlay.addToMap();
                    bikingRouteOverlay.zoomToSpan();
                    break;
            }
        }
    }
}
