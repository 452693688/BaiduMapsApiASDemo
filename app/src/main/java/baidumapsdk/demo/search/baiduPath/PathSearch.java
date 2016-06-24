package baidumapsdk.demo.search.baiduPath;

import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.List;

import baidumapsdk.demo.search.RouteLineAdapter;

/**
 * 路线规划 (搜索)
 * Created by Administrator on 2016/6/22.
 */
public class PathSearch {
    private static final String TAG = "PathSearch";
    private RoutePlanSearch searchPath;
    private Context context;

    public PathSearch(Context context) {
        this.context = context;
        searchPath = RoutePlanSearch.newInstance();
        searchPath.setOnGetRoutePlanResultListener(new OnSearchListener());
    }


    //搜索路线类型
    public void searchPath(int type, PlanNode startLocationNode, PlanNode tagLocationNode) {
        switch (type) {
            case 0:
                //驾车路线搜索
                searchPath.drivingSearch((new DrivingRoutePlanOption())
                        .from(startLocationNode).to(tagLocationNode));
                break;
            case 1:
                //公交路线搜索
                String startCity = tagLocationNode.getCity();
                String endCity = startLocationNode.getCity();
                if (!startCity.equals(endCity)) {
                    Log.e(TAG, "非同一城市，不能查询公交换乘路线");
                    return;
                }
                searchPath.transitSearch((new TransitRoutePlanOption())
                        .from(startLocationNode).city(startCity).to(tagLocationNode));
                break;
            case 2:
                //步行路线搜索
                searchPath.walkingSearch((new WalkingRoutePlanOption())
                        .from(startLocationNode).to(tagLocationNode));
                break;
            case 3:
                //骑行路线搜索
                searchPath.bikingSearch((new BikingRoutePlanOption())
                        .from(startLocationNode).to(tagLocationNode));
                break;
        }
    }

    public void onDestroy() {
        searchPath.destroy();
    }

    class OnSearchListener implements OnGetRoutePlanResultListener {
        //步行路线结果回调
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.e(TAG, "抱歉，未找到结果");
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            resultPath(result.getRouteLines(), 2, null);
        }

        //换乘路线结果回调
        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.e(TAG, "抱歉，未找到结果");
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            resultPath(result.getRouteLines(), 1, RouteLineAdapter.Type.TRANSIT_ROUTE);

        }

        //驾车路线结果回调
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.e(TAG, "抱歉，未找到结果");
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            resultPath(result.getRouteLines(), 0, RouteLineAdapter.Type.DRIVING_ROUTE);


        }

        //骑行路线结果回调
        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.e(TAG, "抱歉，未找到结果");
            }
            if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            resultPath(bikingRouteResult.getRouteLines(), 3, null);

        }

        //searchType : 驾车  0；公交 1；步行 2；骑行 3；
        private void resultPath(List<? extends RouteLine> transitRouteLines, final int searchType, RouteLineAdapter.Type AdapterType) {
            if (onResultPath == null) {
                return;
            }
            if (transitRouteLines.size() == 1) {
                onResultPath.onResultPath(transitRouteLines.get(0), searchType);
                return;
            }
            //多个结果 用户自行选择
            PathDialog myTransitDlg = new PathDialog(context,
                    transitRouteLines, AdapterType);
            myTransitDlg.setOnItemInDlgClickLinster(new PathDialog.OnItemInDlgClickListener() {
                public void onItemClick(int position, RouteLine route) {
                    onResultPath.onResultPath(route, searchType);
                }

            });
            myTransitDlg.show();
        }
    }

    private OnResultPath onResultPath;

    public void setOnResultPath(OnResultPath onResultPath) {
        this.onResultPath = onResultPath;
    }

    public interface OnResultPath {
        public void onResultPath(RouteLine route, int type);
    }
}
