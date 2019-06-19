package com.smart.car.ui.view

import com.smart.car.base.BaseView
import com.smart.car.ui.model.TestVo

interface MainView : BaseView {
    fun checkIdentity(b: Boolean)
}
