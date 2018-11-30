package com.chailease.tw.app.android.camera.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class GridImagesActivity extends AppCompatActivity {
    public static final String EXTRA_FOLDER = "EXTRA_FOLDER";//指定相簿路徑
    public static final String EXTRA_SELECTED_PATHS = "EXTRA_SELECTED_PATHS"; // 回傳用的欄位
    public static final String EXTRA_CLOUMN_NUMBERS = "EXTRA_CLOUMN_NUMBERS";//Grid的cloum數量
    private static final String TAG = GridImagesActivity.class.getSimpleName();
    //Intent傳來的參數
    protected File albumFolder;
    protected int colNums;
    //UI
    protected ProgressBar progressBar;
    protected RecyclerView recyclerView;

    protected LayoutInflater layoutInflater;
    protected GridLayoutManager gridLayoutManager;
    protected List<ItemObject> items = new ArrayList<>();
    protected ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Layout
        setContentView(savedInstanceState);

        //Intent Data
        String folder = getIntent().getStringExtra(EXTRA_FOLDER);
        colNums = getIntent().getIntExtra(EXTRA_CLOUMN_NUMBERS, 3);

        //UI
        setUI();

        //讀取相簿資料
        if (folder != null) {
            albumFolder = new File(folder);
        }
        loadImage(albumFolder);
    }

    //繼承客製化的方法

    //Override此方法客製化修改Layout必須有progress_bar,recycler_view
    protected void setContentView(Bundle bundle) {
        setContentView(R.layout.activity_grid_images);
    }

    //Override此方法客製化UI，若有override setContentView則一定要修改此方法
    protected void setUI() {
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        //RecyclerView setting
        setRecyclerView();

    }

    //Override此方法修改原生UI的RecyclerView的表現/行為
    protected void setRecyclerView() {
        gridLayoutManager = new GridLayoutManager(this, colNums);
        recyclerView.setLayoutManager(gridLayoutManager);
        //RecyclerView.Adapter setting
        setAdapter();
        recyclerView.setAdapter(imageAdapter);
    }

    //Override此方法修改adapter的表現/行為
    protected void setAdapter() {
        imageAdapter = new ImageAdapter();
        imageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ItemObject item, int position) {
                Intent intent = new Intent(GridImagesActivity.this, ImagePreviewFullscreenActivity.class);
                intent.putExtra(ImagePreviewFullscreenActivity.EXTRA_PATH, item.path);
                startActivity(intent);
            }
        });
    }

    //Override此方法修改ViewHolder的Layout，必須有img_content,img_selected
    protected View setViewHolderLayout(ViewGroup parent, int viewType){
        return layoutInflater.inflate(R.layout.item_image_grid, parent, false);
    }
    //切換選擇模式
    protected void setSelectionMode(boolean isSelectedMode) {
        imageAdapter.setSelectionMode(isSelectedMode);
    }

    //取得被選取的相片路徑
    protected List<String> getSelectedItems() {
        return  imageAdapter.getSelectedItems();
    }
    //取得被選取的總數量
    protected int getSelectedCount() {
        return  imageAdapter.getSelectedItemCount();
    }


    private void loadImage(final File outputFolder) {
        new TestDataInitTask(this) {

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<String> doInBackground(Void... voids) {
                List<String> files = getFiles(outputFolder);
                if (files == null || files.isEmpty()) {
                    return super.doInBackground(voids);
                } else {
                    return files;
                }
            }

            @Override
            protected void onPostExecute(List<String> items) {
                progressBar.setVisibility(View.GONE);
                prepareSelectedItems(items);
            }
        }.execute();
    }

    private void prepareSelectedItems(List<String> paths) {
        for (String path : paths) {
            ItemObject itemObject = new ItemObject();
            itemObject.path = path;
            itemObject.isSelected = false;
            items.add(itemObject);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loadImage(albumFolder);
    }

    class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private final int TYPE_CAMERA = 1;
        private OnItemClickListener onItemClickListener;
        private boolean isSelectionMode = false;
        private LocalImageLoader localImageLoader;
        private int selectedItemCount = 0;

        public ImageAdapter() {
            layoutInflater = getLayoutInflater();
            localImageLoader = LocalImageLoader.getInstance();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = setViewHolderLayout(parent, viewType);
            ViewHolder viewHolder = new ViewHolder(view, parent, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() != TYPE_CAMERA) {
                ItemObject item = getItem(position);
                localImageLoader.loadBitmap(item.path, holder.imgContent, holder.width, holder.height);
                if (isSelectionMode && item.isSelected) {
                    holder.imgSelected.setVisibility(View.VISIBLE);
                } else {
                    holder.imgSelected.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void changeSelectedItem(int selectedPosition) {
            ItemObject item = getItem(selectedPosition);
            item.isSelected = !item.isSelected;
            if (item.isSelected) {
                selectedItemCount++;
            } else {
                selectedItemCount--;
            }
            notifyItemChanged(selectedPosition);
        }

        public void setSelectionMode(boolean isSelectionMode) {
            this.isSelectionMode = isSelectionMode;
            if (!isSelectionMode) {
                clearSelection();
            }
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {
            return selectedItemCount;
        }

        public void clearSelection() {
            selectedItemCount = 0;
            for (ItemObject item : items) {
                item.isSelected = false;
            }
        }

        public ArrayList<String> getSelectedItems() {
            ArrayList<String> selectedItems = new ArrayList<>();
            for (ItemObject item : items) {
                if (item.isSelected) {
                    selectedItems.add(item.path);
                }
            }
            return selectedItems;
        }

        private ItemObject getItem(int position) {
            return items.get(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgContent;
            ImageView imgSelected;
            int width, height;

            public ViewHolder(View itemView, ViewGroup parent, int viewType) {
                super(itemView);
                imgContent = (ImageView) itemView.findViewById(R.id.img_content);
                imgSelected = (ImageView) itemView.findViewById(R.id.img_selected);
                int size = parent.getWidth() / gridLayoutManager.getSpanCount();
                width = size;
                height = size;
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.width = width;
                params.height = height;
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = getAdapterPosition();
                            if (isSelectionMode) {
                                changeSelectedItem(position);
                            } else if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(getItem(position), position);
                            }
                        }
                    });
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(ItemObject item, int position);
    }

    public class ItemObject {
        public String path;
        boolean isSelected;
    }

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
}