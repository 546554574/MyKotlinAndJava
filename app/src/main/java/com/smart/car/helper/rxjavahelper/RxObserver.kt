package com.smart.car.helper.rxjavahelper


import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * user：lqm
 * desc：自己的Observer，减少实现不必要的回调
 */
abstract class RxObserver<T> : Observer<T> {

    override fun onSubscribe(d: Disposable) {
        onSubscribeMy(d)
    }

    override fun onNext(t: T) {
        onNextMy(t)
    }

    override fun onError(e: Throwable) {
        onErrorMy(e!!.message.toString())
    }

    override fun onComplete() {
        onCompleteMy()
    }

    open fun onSubscribeMy(d: Disposable) {

    }

    open fun onCompleteMy() {

    }

    //抽象方法，必须实现
    abstract fun onNextMy(t: T)

    abstract fun onErrorMy(errorMessage: String)

}