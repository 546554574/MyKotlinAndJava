package com.smart.car.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife

/**
 * Created by Administrator on 2018/4/3 0003.
 */

abstract class BaseFragment<V, T : BasePresenterImpl<V>> : Fragment(), BaseView {
    var mRootView: View? = null

    protected var mPresenter: T? = null
    //下拉刷新

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(getLayout(), container, false)
        ButterKnife.bind(mRootView!!)
        //initPresenter()是抽象方法，让view初始化自己的presenter
        mPresenter = initPresenter()
        //presenter和view的绑定
        if (mPresenter != null) {
            mPresenter!!.attachView(this as V)
        }
        //        AutoSizeConfig.getInstance().setCustomFragment(true);
        init(savedInstanceState)
        return mRootView
    }

    abstract fun getLayout(): Int
    // 实例化presenter
    abstract fun initPresenter(): T

    abstract fun init(savedInstanceState: Bundle?)

    companion object {

        val TAG = BaseFragment!!.javaClass.simpleName
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
