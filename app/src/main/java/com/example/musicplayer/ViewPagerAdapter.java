package com.example.musicplayer;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private HashMap<Integer, String> fragmentTag;
    private FragmentManager fragmentManager;
    private Context context;

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm,  Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentTag = new HashMap<>();
        this.fragmentManager = fm;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return titleList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    public void AddFragment (Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if(obj instanceof Fragment) {
            //Record Fragment tag
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            fragmentTag.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = fragmentTag.get(position);
        if(tag == null) return null;
        return fragmentManager.findFragmentByTag(tag);
    }
}
