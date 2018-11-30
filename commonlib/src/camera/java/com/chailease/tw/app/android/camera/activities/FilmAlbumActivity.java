package com.chailease.tw.app.android.camera.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.chailease.tw.app.android.commonlib.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FilmAlbumActivity extends AppCompatActivity {
    public static final String EXTRA_PATHS = "EXTRA_FOLDER";
    public static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";

    private static final String TAG = FilmAlbumActivity.class.getSimpleName();
    private static final int NO_POSITION = RecyclerView.NO_POSITION;

    ProgressBar progressBar;
    RecyclerView recyclerViewSmall;
    RecyclerView recyclerViewBig;

    private List<String> items = null;
    private int mSelectedPosition = NO_POSITION;
    private int smallCenterItemPos = NO_POSITION; // 用在強迫滾動到某個item
    private SnapHelper linearSnapHelper, pagerSnapHelper;
    private ImageAdapter adapterBig;
    private ImageAdapterSmall adapterSmall;

    //region 設定開關
    private boolean isCenterAlign = true; // 設定上面縮圖列表是否自動置中
    private boolean isScrollBigWhenSmallCenterChange = false; // 設定縮圖列表自動置中時(上面設定為true)是否連動變更下方大圖列表項目
    //endregion 設定開關

    private File albumFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_album);

        //UI
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerViewSmall = (RecyclerView) findViewById(R.id.recycler_view_small);
        recyclerViewBig = (RecyclerView) findViewById(R.id.recycler_view_big);

        //RecyclerView setting
        recyclerViewSmall.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBig.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // 加入Pager的效果
        pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerViewBig);

        recyclerViewBig.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 取得目前顯示位置
                    View centerView = pagerSnapHelper.findSnapView(recyclerView.getLayoutManager());
                    if (null != centerView) {
                        int pos = recyclerView.getChildViewHolder(centerView).getAdapterPosition();
                        Log.d(TAG, "Big Snapped Item Position: " + pos);
                        setCurrentPosition(pos);
                    }
                }
            }
        });

        adapterSmall = new ImageAdapterSmall();
        adapterSmall.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                setCurrentPosition(position);
            }
        });
        recyclerViewSmall.setAdapter(adapterSmall);

        adapterBig = new ImageAdapter();
        adapterBig.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                Intent intent = new Intent(FilmAlbumActivity.this, ImagePreviewFullscreenActivity.class);
                intent.putExtra(ImagePreviewFullscreenActivity.EXTRA_PATH, item);
                startActivity(intent);
            }
        });
        recyclerViewBig.setAdapter(adapterBig);

        // 加入自動置中的效果
        if (isCenterAlign) {
            linearSnapHelper = new LinearSnapHelper() {
                @Override
                public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                    int position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                    Log.d(TAG, "findTargetSnapPosition " + position + " " + smallCenterItemPos);
                    if (smallCenterItemPos != NO_POSITION) {
                        return smallCenterItemPos;
                    }
                    return position;
                }
            };
            linearSnapHelper.attachToRecyclerView(recyclerViewSmall);

            // 偵測上方滾動選中的項目
            recyclerViewSmall.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // 取得目前顯示位置
                        View centerView = linearSnapHelper.findSnapView(recyclerView.getLayoutManager());
                        if (centerView != null) {
                            int pos = recyclerView.getChildViewHolder(centerView).getAdapterPosition();
                            Log.d(TAG, "Small Snapped Item Position: " + pos);

                            if (isScrollBigWhenSmallCenterChange) {
                                // 變更選中項目＆連帶滾動大圖列表
                                int position = adapterSmall.getItemPosition(pos);
                                adapterSmall.changeSelectedItem(position);
                                recyclerViewBig.scrollToPosition(position);
                            }
                        }
                    }
                }
            });
        }


        new TestDataInitTask(this) {
            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<String> doInBackground(Void... voids) {
                // 看有沒有傳入的資料，沒有就自己產生測試資料
                final String folderPath = getIntent().getStringExtra(EXTRA_PATHS);
                if (null != folderPath) {
                    albumFolder = new File(folderPath);
                }

                List<String> pathes = getFiles(albumFolder);

                return pathes;
            }

            @Override
            protected void onPostExecute(List<String> paths) {
                progressBar.setVisibility(View.GONE);
                prepareItems(paths);
            }

        }.execute();
    }

//

    private void prepareItems(List<String> paths) {
        items = paths;
        adapterBig.notifyDataSetChanged();
        adapterSmall.notifyDataSetChanged();

        // 如果前一頁有指定當前項目，將兩個列表都滾動到當前項目
        int currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, NO_POSITION);
        if (currentPosition != NO_POSITION) {
            setCurrentPosition(currentPosition);
        }
    }

    private void setCurrentPosition(int position) {
        adapterSmall.changeSelectedItem(position);
        if (isCenterAlign) {
            smallCenterItemPos = position + 1;
            linearSnapHelper.onFling(recyclerViewSmall.getMaxFlingVelocity(), 0);
            smallCenterItemPos = NO_POSITION;
        } else {
            recyclerViewSmall.smoothScrollToPosition(position);
        }
        recyclerViewBig.scrollToPosition(position);
    }

    // 大圖用的Adapter
    class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private LayoutInflater layoutInflater;
        private OnItemClickListener onItemClickListener;
        private LocalImageLoader localImageLoader;

        public ImageAdapter() {
            layoutInflater = getLayoutInflater();
            localImageLoader = LocalImageLoader.getInstance();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.item_image_list, parent, false);
            return new ViewHolder(view, parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position + " " + holder.width + " " + holder.height);
            localImageLoader.loadBitmap(getItem(position), holder.imageView, holder.width, holder.height);
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (items != null) {
                count = items.size();
            }
            return count;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public int getItemPosition(int position) {
            return position;
        }

        private String getItem(int position) {
            position = getItemPosition(position);
            return items.get(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            int width, height;

            public ViewHolder(View itemView, ViewGroup parent) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.image);
                width = parent.getWidth();
                height = parent.getHeight();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            int position = getAdapterPosition();
                            onItemClickListener.onItemClick(getItem(position), position);
                        }
                    }
                });
            }
        }
    }

    // 小圖的Adapter
    class ImageAdapterSmall extends RecyclerView.Adapter<ImageAdapterSmall.ViewHolderSmall> {
        private final int TYPE_EMPTY = 1;
        private LayoutInflater layoutInflater;
        private OnItemClickListener onItemClickListener;
        private LocalImageLoader localImageLoader;

        public ImageAdapterSmall() {
            layoutInflater = getLayoutInflater();
            localImageLoader = LocalImageLoader.getInstance();
        }

        @Override
        public int getItemViewType(int position) {
            if (isCenterAlign) {
                if (position == 0 || position == getItemCount() - 1) {
                    return TYPE_EMPTY;
                }
            }
            return super.getItemViewType(position);
        }

        @Override
        public ViewHolderSmall onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.item_image_list_small, parent, false);
            ViewHolderSmall viewHolder = new ViewHolderSmall(view, parent, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolderSmall holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position + " " + holder.width + " " + holder.height);
            if (holder.getItemViewType() == TYPE_EMPTY) {
                return;
            }
            localImageLoader.loadBitmap(getItem(position), holder.imageView, holder.width, holder.height);
            if (getItemPosition(position) == mSelectedPosition) {
                holder.itemView.setBackgroundColor(Color.YELLOW);
            } else {
                holder.itemView.setBackgroundColor(Color.BLACK);
            }
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (items != null) {
                count = items.size();
            }
            if (isCenterAlign) {
                count += 2;
            }
            return count;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void changeSelectedItem(int selectedPosition) {
            mSelectedPosition = selectedPosition;
            notifyDataSetChanged();
        }

        public int getItemPosition(int position) {
            if (isCenterAlign) {
                return position - 1;
            }
            return position;
        }

        private String getItem(int position) {
            position = getItemPosition(position);
            return items.get(position);
        }

        class ViewHolderSmall extends RecyclerView.ViewHolder {
            ImageView imageView;
            int width, height;

            public ViewHolderSmall(View itemView, ViewGroup parent, int viewType) {
                super(itemView);
                imageView = (ImageView)itemView.findViewById(R.id.image);
                height = parent.getHeight();
                width = parent.getHeight();
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.width = width;
                if (viewType == TYPE_EMPTY) {
                    params.width = parent.getWidth() / 2 - width / 2;
                } else {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClickListener != null) {
                                int position = getAdapterPosition();
                                onItemClickListener.onItemClick(getItem(position), getItemPosition(position));
                            }
                        }
                    });
                }
                params.height = height;
                itemView.setLayoutParams(params);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(String item, int position);
    }

    //region 準備測試資料
    static class TestDataInitTask extends AsyncTask<Void, Void, List<String>> {
        private Context context;

        public TestDataInitTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            // for init test data
            String sampleFolderName = "sample_pics";
            File outputFolder = new File(context.getCacheDir(), sampleFolderName);
            copyAssets(sampleFolderName, outputFolder);

            // get file path list
            return getFiles(outputFolder);
        }

        protected List<String> getFiles(File outputFolder) {
            // get file path list
            List<String> items = new ArrayList<>();
            File[] files = outputFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    items.add(file.getAbsolutePath());
                }
            }
            return items;
        }

        private void copyAssets(String assetFolderPath, File outputFolder) {
            AssetManager assetManager = context.getAssets();
            String[] files = null;
            try {
                files = assetManager.list(assetFolderPath);
            } catch (IOException e) {
                Log.e(TAG, "Failed to get asset file list.", e);
            }
            if (files != null) {
                if (!outputFolder.exists()) {
                    outputFolder.mkdirs();
                }
                for (String filename : files) {
                    Log.d(TAG, "filename " + filename);
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open(assetFolderPath + File.separator + filename);
                        File outFile = new File(outputFolder, filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    } catch (IOException e) {
                        Log.e("tag", "Failed to copy asset file: " + filename, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                    }
                }
            }
        }

        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
    }
    //endregion 準備測試資料
}
