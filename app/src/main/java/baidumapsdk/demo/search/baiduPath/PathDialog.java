package baidumapsdk.demo.search.baiduPath;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.mapapi.search.core.RouteLine;

import java.util.List;

import baidumapsdk.demo.R;
import baidumapsdk.demo.search.RouteLineAdapter;

/**
 * 有多个路线时显示 选择
 * Created by Administrator on 2016/6/22.
 */
public class PathDialog extends Dialog {

    private List<? extends RouteLine> mtransitRouteLines;
    private ListView transitRouteList;
    private RouteLineAdapter mTransitAdapter;

    OnItemInDlgClickListener onItemInDlgClickListener;

    public PathDialog(Context context, int theme) {
        super(context, theme);
    }

    public PathDialog(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
            type) {
        this(context, 0);
        mtransitRouteLines = transitRouteLines;
        mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit_dialog);

        transitRouteList = (ListView) findViewById(R.id.transitList);
        transitRouteList.setAdapter(mTransitAdapter);

        transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RouteLine route = mtransitRouteLines.get(position);
                onItemInDlgClickListener.onItemClick(position, route);
                dismiss();
            }
        });
    }

    public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
        onItemInDlgClickListener = itemListener;
    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position, RouteLine route);
    }
}
