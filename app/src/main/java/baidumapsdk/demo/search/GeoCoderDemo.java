package baidumapsdk.demo.search;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import baidumapsdk.demo.R;
import baidumapsdk.demo.search.geo.GeoCodeManager;

/**
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class GeoCoderDemo extends Activity {
    BaiduMap mBaiduMap = null;
    MapView mMapView = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocoder);
        CharSequence titleLable = "地理编码功能";
        setTitle(titleLable);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        GeoCodeManager.getInstance().setOnSearchResult(new OnSearchResult());

    }

    /**
     * 发起搜索
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        if (v.getId() == R.id.reversegeocode) {
            EditText lat = (EditText) findViewById(R.id.lat);
            EditText lon = (EditText) findViewById(R.id.lon);
            GeoCodeManager.getInstance().searchUnGeo(Float.valueOf(lat.getText()
                    .toString()), Float.valueOf(lon.getText().toString()));

        } else if (v.getId() == R.id.geocode) {
            EditText editCity = (EditText) findViewById(R.id.city);
            EditText editGeoCodeKey = (EditText) findViewById(R.id.geocodekey);
            // Geo搜索
            GeoCodeManager.getInstance().searchGeo(editCity.getText().toString(),
                    editGeoCodeKey.getText().toString());
        }
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
        GeoCodeManager.getInstance().onDestroy();
        mMapView.onDestroy();
        super.onDestroy();
    }


    class OnSearchResult implements GeoCodeManager.OnSearchResultListener {

        @Override
        public void onSearchResult(int type, SearchResult searchResult, boolean isSucceed) {
            if (!isSucceed) {
                Toast.makeText(GeoCoderDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            mBaiduMap.clear();
            if (type == 0) {
                GeoCodeResult result = (GeoCodeResult) searchResult;
                mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_marka)));
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                        .getLocation()));
                String strInfo = String.format("纬度：%f 经度：%f",
                        result.getLocation().latitude, result.getLocation().longitude);
                Toast.makeText(GeoCoderDemo.this, strInfo, Toast.LENGTH_LONG).show();

            }
            if (type == 1) {
                ReverseGeoCodeResult result = (ReverseGeoCodeResult) searchResult;
                mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_marka)));
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                        .getLocation()));
                Toast.makeText(GeoCoderDemo.this, result.getAddress(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
