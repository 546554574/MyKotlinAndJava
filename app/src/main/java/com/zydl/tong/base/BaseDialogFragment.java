package com.zydl.tong.base;

import android.content.Context;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kangyi on 2016-11-21.
 */
public class BaseDialogFragment extends DialogFragment {
    View rootView;

    Context mContext;

    public View getRootView(ViewGroup container, int resId) {
        mContext = getActivity();
        rootView = LayoutInflater.from(mContext).inflate(resId, container, false);
        return rootView;
    }

    public <T extends View> T obtainView(int resId) {
        if (null == rootView) {
            return null;
        }
        return (T) rootView.findViewById(resId);
    }

    /**
     * Display the dialog, adding the fragment to the given FragmentManager.  This
     * is a convenience for explicitly creating a transaction, adding the
     * fragment to it with the given tag, and committing it.  This does
     * <em>not</em> add the transaction to the back stack.  When the fragment
     * is dismissed, a new transaction will be executed to remove it from
     * the activity.
     * @param manager The FragmentManager this fragment will be added to.
     * @param tag The tag for this fragment, as per
     * {@link FragmentTransaction#add(Fragment, String) FragmentTransaction.add}.
     */
    @Override
    public void show(FragmentManager manager, String tag) {
//        mDismissed = false;
//        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        // 这里吧原来的commit()方法换成了commitAllowingStateLoss()
        ft.commitAllowingStateLoss();
    }
}