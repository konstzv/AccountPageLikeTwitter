package ru.zagulin.twitterlikeaccountpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private static final String[] PAGE_TITLES = {"Tweets", "Tweet&replies", "Media", "Likes"};

    public PageAdapter(@NonNull final FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(final int position) {
        return new Fragment();
    }

    @Override
    public int getCount() {
        return PAGE_TITLES.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(final int position) {
        return PAGE_TITLES[position];
    }
}
