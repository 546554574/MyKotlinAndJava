package com.smart.car.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.blankj.rxbus.RxBus;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smart.car.R;
import com.smart.car.base.AppConstant;
import com.smart.car.base.BaseActivity;
import com.smart.car.base.BaseFragment;
import com.smart.car.base.MyUtil;
import com.smart.car.busmsg.SendMsg;
import com.smart.car.ui.model.GroupAndPlaceVo;
import com.smart.car.ui.model.SendMsgVo;
import com.smart.car.ui.presenter.CallActivityPresenter;
import com.smart.car.ui.view.CallView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.smart.car.widget.dialog.ClearDialog;
import com.smart.util.rxtool.view.RxToast;

public class MissionActivity extends BaseFragment<CallView, CallActivityPresenter> implements CallView {
    RecyclerView rv_group;
    RecyclerView rv_place;

    private GroupRVAdapter mGroupAdapter;
    private PlaceRVAdapter mPlaceAdapter;

    private GroupAndPlaceVo vo;
    private List<GroupAndPlaceVo.GroupBean.PlaceBean> places = new ArrayList<>();
    private int groupPos = 0;
    private int placePos = 0;
    private LinearLayoutManager groupManager;
    private LinearLayoutManager placeManager;

    public static MissionActivity fragment;

    public static MissionActivity newInstance() {
        if (fragment == null) {
            Bundle args = new Bundle();
            fragment = new MissionActivity();
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_mission;
    }

    @Override
    public CallActivityPresenter initPresenter() {
        return null;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        rv_group = getMRootView().findViewById(R.id.rv_group);
        rv_place = getMRootView().findViewById(R.id.rv_place);
        vo = AppConstant.groupandplaceVo;
        vo.getPlaces().get(0).setSelect(true);
        if (mGroupAdapter == null && vo != null) {
            mGroupAdapter = new GroupRVAdapter(R.layout.item_mission_group, vo.getPlaces());
            groupManager = new LinearLayoutManager(getContext());
            rv_group.setLayoutManager(groupManager);
            rv_group.setAdapter(mGroupAdapter);
            rv_group.setHasFixedSize(true);
            rv_group.setNestedScrollingEnabled(false);
        }
        if (mPlaceAdapter == null && vo != null) {
            places.clear();
            places.addAll(vo.getPlaces().get(0).getPlace());
            mPlaceAdapter = new PlaceRVAdapter(R.layout.item_mission_place, places);
            placeManager = new LinearLayoutManager(getContext());
            rv_place.setLayoutManager(placeManager);
            rv_place.setAdapter(mPlaceAdapter);
            rv_place.setHasFixedSize(true);
            rv_place.setNestedScrollingEnabled(false);
        }
        mGroupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                places.clear();
                places.addAll(vo.getPlaces().get(position).getPlace());
                mPlaceAdapter.notifyDataSetChanged();
                vo.getPlaces().get(groupPos).setSelect(false);
                for (GroupAndPlaceVo.GroupBean.PlaceBean bean : vo.getPlaces().get(groupPos).getPlace()) {
                    bean.setSelect(false);
                }
                vo.getPlaces().get(position).setSelect(true);
                groupPos = position;
                mGroupAdapter.notifyDataSetChanged();
            }
        });
        mPlaceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                places.get(placePos).setSelect(false);
                places.get(position).setSelect(true);
                placePos = position;
                mPlaceAdapter.notifyDataSetChanged();
                final ClearDialog dialogSureCancel = new ClearDialog(getContext());
                dialogSureCancel.getContentView().setText("确定派遣该车");
                dialogSureCancel.getLogoView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogSureCancel.cancel();
                    }
                });
                dialogSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SendMsgVo vo = new SendMsgVo();
                        vo.setId(AppConstant.ID);
                        vo.setMsgCode(162);
                        vo.setDestination(places.get(position).getId());
                        vo.setStateCode(AppConstant.STATE);
//                        RxToast.info(vo.toString());
                        RxBus.getDefault().post(new SendMsg(vo));
                        //发送状态请求
                        MyUtil.INSTANCE.sendRequestStateMsg();
                        dialogSureCancel.dismiss();
                    }
                });
                dialogSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogSureCancel.cancel();
                    }
                });
                dialogSureCancel.show();
            }
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    private class GroupRVAdapter extends BaseQuickAdapter<GroupAndPlaceVo.GroupBean, BaseViewHolder> {
        public GroupRVAdapter(int layoutResId, @Nullable List<GroupAndPlaceVo.GroupBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, GroupAndPlaceVo.GroupBean item) {
            TextView tv_groupname = helper.getView(R.id.tv_groupname);
            if (item.isSelect()) {
                tv_groupname.setSelected(true);
            } else {
                tv_groupname.setSelected(false);
            }
            helper.setText(R.id.tv_groupname, item.getGroup_name());
        }
    }

    private class PlaceRVAdapter extends BaseQuickAdapter<GroupAndPlaceVo.GroupBean.PlaceBean, BaseViewHolder> {
        public PlaceRVAdapter(int layoutResId, @Nullable List<GroupAndPlaceVo.GroupBean.PlaceBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, GroupAndPlaceVo.GroupBean.PlaceBean item) {
            TextView tv_placename = helper.getView(R.id.tv_placename);
            if (item.isSelect()) {
                tv_placename.setSelected(true);
            } else {
                tv_placename.setSelected(false);
            }
            helper.setText(R.id.tv_placename, item.getName());
        }
    }
}
