package baidumapsdk.demo.search.geo;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * 地址和地理编码搜索
 * Created by Administrator on 2016/6/23.
 */
public class GeoCodeManager {
    private static GeoCodeManager geoCodeManager;
    private GeoCoder mSearch;

    public static GeoCodeManager getInstance() {
        if (geoCodeManager == null) {
            geoCodeManager = new GeoCodeManager();
        }
        return geoCodeManager;
    }

    public GeoCodeManager() {
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoder());
    }

    //纬度，经度
    public void searchUnGeo(float latitude, float longitude) {
        LatLng ptCenter = new LatLng(latitude, longitude);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));
    }

    //城市， 地址
    public void searchGeo(String cityName, String address) {
        mSearch.geocode(new GeoCodeOption().city(
                cityName).address(address));

    }

    public void onDestroy() {
        mSearch.destroy();
        geoCodeManager = null;
    }

    class OnGetGeoCoder implements OnGetGeoCoderResultListener {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            //城市+地址搜索结果
            if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.e("-------", "抱歉，未能找到结果");
                onBack(0, geoCodeResult, false);
                return;
            }
            onBack(0, geoCodeResult, true);
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            //纬度+经度搜索结果
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.e("-------", "抱歉，未能找到结果");
                onBack(1, reverseGeoCodeResult, false);
                return;
            }
            onBack(1, reverseGeoCodeResult, true);
        }

        private void onBack(int type, SearchResult searchResult, boolean isSucceed) {
            if (onSearchResult == null) {
                return;
            }
            onSearchResult.onSearchResult(type, searchResult, isSucceed);
        }
    }

    private OnSearchResultListener onSearchResult;

    public void setOnSearchResult(OnSearchResultListener onSearchResult) {
        this.onSearchResult = onSearchResult;
    }

    public interface OnSearchResultListener {
        /***
         *
         * @param type 0:城市+地址搜索结果 ;1:纬度+经度搜索结果
         * @param searchResult 搜索结果
         * @param isSucceed true搜索成功
         */
        public void onSearchResult(int type, SearchResult searchResult, boolean isSucceed);
    }
}

