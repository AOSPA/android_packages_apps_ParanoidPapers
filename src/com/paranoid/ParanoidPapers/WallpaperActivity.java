/*
 * Copyright (C) 2012-2017 ParanoidAndroid Project
 *
 * Licensed under the GNU GPLv2 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl-2.0.txt
 */

package com.paranoid.ParanoidPapers;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

public class WallpaperActivity extends FragmentActivity {
    private static final String TAG = WallpaperActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * The {@link TabLayout} that will host the page indicators.
     */
    private TabLayout mTabLayout;

    /**
     * The {@link WallpaperManager} used to set wallpaper.
     */
    private static WallpaperManager sWallpaperManager;

    /**
     * The {@link Integer} that stores current viewpager position
     */
    private static int sCurrentPosition;

    /**
     * The {@link ArrayList} that will host the wallpapers resource ID's.
     */
    private static ArrayList<Integer> sWallpapers = new ArrayList<Integer>();

    /**
     * The {@link Activity} that will be used to finish the activity.
     */
    private static FragmentActivity sActivity;

    /**
     * The {@link Snackbar} that is responsible for showing the info text.
     */
    private Snackbar mSnackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        sActivity = this;

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                sCurrentPosition = position;
            }
        });
        mTabLayout = (TabLayout) findViewById(R.id.page_indicator);
        mTabLayout.setupWithViewPager(mViewPager, true);

        sWallpaperManager = WallpaperManager.getInstance(this);

        sWallpapers.clear();

        fetchWallpapers(R.array.wallpapers);

        mSnackbar = Snackbar.make(mViewPager, R.string.longpress_info, 4000 /* 4 seconds */);
        mSnackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSnackbar.show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new WallpaperFragment();
            Bundle args = new Bundle();
            args.putInt(WallpaperFragment.ARG_SECTION_NUMBER, i);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return sWallpapers.size();
        }
    }

    public static class WallpaperFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final Bundle args = getArguments();
            final Context context = getContext();
            final WallpaperLoader loader = new WallpaperLoader(context);

            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setImageResource(sWallpapers.get(args.getInt(ARG_SECTION_NUMBER)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                            sWallpapers.get(sCurrentPosition));
                    loader.setBitmap(bitmap);
                    loader.execute();
                    return true;
                }
            });
            return imageView;
        }
    }

    private void fetchWallpapers(int list) {
        final String[] walls = getResources().getStringArray(list);
        for (String wall : walls) {
            int res = getResources().getIdentifier(wall, "drawable", getPackageName());
            if (res != 0) {
                sWallpapers.add(res);
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }
    }

    private static class WallpaperLoader extends AsyncTask<Integer, Void, Boolean> {
        Bitmap mBitmap;
        Context mContext;
        ProgressDialog mDialog;

        private WallpaperLoader(Context context) {
            super();
            mContext = context;
        }

        private void setBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {
                sWallpaperManager.setBitmap(mBitmap);

                // Help GC
                mBitmap.recycle();

                return true;
            } catch (IOException | OutOfMemoryError e) {
                Log.e(TAG, "Exception ocurred while trying to set wallpaper", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sActivity.startActivity(intent);
            sActivity.finish();
        }

        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.applying));
        }
    }
}
