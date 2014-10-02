package com.itsme;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itsme.model.Data;
import com.itsme.model.ImageSet;
import com.itsme.model.SearchResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainFragment extends Fragment {

    private static final String IMAGE_ARRAY = "image_array";
    private static final String MAX_TAG = "max_tag";

    @InjectView(R.id.listview)
    ListView listview;

    @InjectView(R.id.connection_text)
    TextView connection_text;

    ArrayList<ImageSet> imageArray;
    PhotoListAdapter photoListAdapter;
    InstagramService instagramService;

    String next_max_tag_id;
    boolean loading;


    public MainFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.INSTAGRAM_ENDPOINT)
                .build();

        instagramService = restAdapter.create(InstagramService.class);

        if(savedInstanceState == null) {
            imageArray = new ArrayList<ImageSet>();
            photoListAdapter = new PhotoListAdapter(getActivity(), imageArray);
            load();
        }
        else {
            imageArray = savedInstanceState.getParcelableArrayList(IMAGE_ARRAY);
            photoListAdapter = new PhotoListAdapter(getActivity(), imageArray);
            next_max_tag_id = savedInstanceState.getString(MAX_TAG);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listview.setAdapter(photoListAdapter);
        listview.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int first_visible_item, int visible_item_count, int total_item_count) {

                int last_visible_item = first_visible_item + visible_item_count;

                if((last_visible_item  == total_item_count) && !(loading)){
                    loadMore();
                }
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(IMAGE_ARRAY, imageArray);
        outState.putString(MAX_TAG, next_max_tag_id);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        next_max_tag_id = null;
        photoListAdapter.clear();
        load();
    }

    private void load() {
        loading = true;
        instagramService.searchForTag("selfie", callback);
    }

    private void loadMore() {
        loading = true;
        instagramService.searchForTag("selfie", next_max_tag_id, callback);
    }

    private void showImage(String imageUrl) {
        final Dialog dialog = new Dialog(getActivity(), R.style.BorderlessDialog);
        dialog.setContentView(R.layout.dialog_image);
        dialog.show();

        ImageView imageView = (ImageView) dialog.findViewById(R.id.image_view);
        Picasso.with(getActivity()).load(imageUrl).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    Callback<SearchResult> callback = new Callback<SearchResult>() {
        @Override
        public void success(SearchResult searchResult, Response response){

            loading = false;
            connection_text.setVisibility(View.INVISIBLE);

            next_max_tag_id = searchResult.pagination.next_max_tag_id;

            if(searchResult.data != null && searchResult.data.length > 0) {

                Data data = null;
                ImageSet imageSet = null;

                for(int i = 0; i < searchResult.data.length; i++) {
                    data = searchResult.data[i];

                    int remainder = i % 3;

                    switch(remainder) {
                        case 0:
                            imageSet = new ImageSet();
                            imageSet.largeImageUrl = searchResult.data[i].images.standard_resolution.url;
                            break;
                        case 1:
                            imageSet.smallImageUrlOne = searchResult.data[i].images.low_resolution.url;
                            break;
                        case 2:
                            imageSet.smallImageUrlTwo = searchResult.data[i].images.low_resolution.url;
                            imageArray.add(imageSet);
                            break;
                    }
                }

                photoListAdapter.notifyDataSetChanged();
            }
            else {
                connection_text.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            loading = false;
            connection_text.setVisibility(View.VISIBLE);
        }

    };


    public class PhotoListAdapter extends ArrayAdapter {

        LayoutInflater inflater;

        class ViewHolder {
            @InjectView(R.id.large_image)
            ImageView large_image;

            @InjectView(R.id.small_one_image)
            ImageView small_one_image;

            @InjectView(R.id.small_two_image)
            ImageView small_two_image;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }


        public PhotoListAdapter(Context context, ArrayList<ImageSet> images) {
            super(context, R.layout.listrow, images);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder;

            final ImageSet imageSet = (ImageSet)getItem(position);

            if(view != null) {
                holder = (ViewHolder)view.getTag();
            }
            else {
                view = this.inflater.inflate(R.layout.listrow, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }

            Picasso.with(getContext()).load(imageSet.largeImageUrl).into(holder.large_image);
            Picasso.with(getContext()).load(imageSet.smallImageUrlOne).into(holder.small_one_image);
            Picasso.with(getContext()).load(imageSet.smallImageUrlTwo).into(holder.small_two_image);

            holder.large_image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showImage(imageSet.largeImageUrl);
                }
            });

            holder.small_one_image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showImage(imageSet.smallImageUrlOne);
                }
            });

            holder.small_two_image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showImage(imageSet.smallImageUrlTwo);
                }
            });

            return view;
        }
    }
}
