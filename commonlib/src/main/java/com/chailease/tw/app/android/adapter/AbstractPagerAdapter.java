package com.chailease.tw.app.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 *
 */
abstract public class AbstractPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {

    public AbstractPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // getItem is called to instantiate the fragment for the given page.
    // Return a PlaceholderFragment (defined as a static inner class below).
    @Override
    abstract public T getItem(int position);

    @Override
    public int getCount() {
        return 0;
    }
}