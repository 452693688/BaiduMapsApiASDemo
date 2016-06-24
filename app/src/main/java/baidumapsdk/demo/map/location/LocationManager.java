package baidumapsdk.demo.map.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationManager {
    private LocationClient mLocationClient;
    //位置信息
    private BDLocation bDLocation;
    private String currentCity; // 用于保存定位到的城市
    private String currentCityId;// 用户保存城市Id
    private static LocationManager locationUtile;

    public static LocationManager getInstance() {
        if (locationUtile == null) {
            locationUtile = new LocationManager();
        }
        return locationUtile;
    }

    // 定位初始化
    public void initLocation(Context context, OnLocationListenner onLocationListenner) {
        this.onLocationListenner = onLocationListenner;
        if (mLocationClient != null) {
            mLocationClient.start();
            return;
        }
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(new LocationListener());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(10000);
        // 需要地址信息，设置为其他任何值（string类型，且不能为null）时，都表示无地址信息。
        option.setAddrType("all");
        // 设置是否返回POI的电话和地址等详细信息。默认值为false，即不返回POI的电话和地址信息。
        option.setProdName("通过GPS定位我当前的位置");
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    //获取城市名称
    public String getLocationCity() {
        return currentCity;
    }

    //获取城市代码
    public String getLocationCityCode() {
        return currentCityId;
    }

    //获取位置信息
    public BDLocation getBDLocation() {
        return bDLocation;
    }

    //停止定位
    public void locationStop() {
        if (mLocationClient == null) {
            return;
        }
        continuous = 1;
        mLocationClient.stop();
    }

    //1 表示只定位1次，大于1表示多次
    private int continuous = 1;

    //开始定位
    public void locationStart(int continuous) {
        if (mLocationClient == null) {
            return;
        }
        this.continuous = continuous;
        mLocationClient.start();
    }
    private OnLocationListenner onLocationListenner;
    /**
     * 实现实位回调监听
     */
    public class LocationListener implements BDLocationListener {


        public LocationListener( ) {

        }

        @Override
        public void onReceiveLocation(BDLocation arg0) {
            // 定位完成
            if (arg0.getCity() == null) {
                currentCity = "";
            } else {
                currentCity = arg0.getCity().substring(0,
                        arg0.getCity().length() - 1);
                currentCityId = arg0.getCityCode();
            }
            if (continuous <= 1) {
                locationStop();
            }
            bDLocation = arg0;
            onLocationListenner.locationComplete(arg0);
        }

        public void onReceivePoi(BDLocation arg0) {
            onLocationListenner.locationComplete(arg0);
        }
    }

    /**
     * 监听定位结果
     */
    public interface OnLocationListenner {

        void locationComplete(BDLocation arg0);
    }
}
