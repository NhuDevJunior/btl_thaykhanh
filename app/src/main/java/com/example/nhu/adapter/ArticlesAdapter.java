package com.example.nhu.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;


import com.example.nhu.R;
import com.example.nhu.Utils;
import com.example.nhu.models.Article;

import java.util.List;

public class ArticlesAdapter extends
        Adapter<ArticlesAdapter.ViewHolder> {
    private List<Article> mArticles;
    private Context context;
    private OnItemClickListener onItemClickListener;
    public ArticlesAdapter(List<Article> mArticles,Context context) {
        this.mArticles = mArticles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView,onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Article article = mArticles.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();
        Glide.with(context).load(article.getUrlToImage()).apply(requestOptions).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);

                return false;
            }
        }).transition(DrawableTransitionOptions.withCrossFade()).into(holder.imageView);
        // Set item views based on your views and data model
        holder.title.setText(article.getTitle());
        holder.desc.setText(article.getDescription());
        holder.source.setText(article.getSource().getName());
        holder.time.setText("\u2020"+ Utils.DateToTimeFormat(article.getPublishedAt()));
        holder.published.setText(Utils.DateFormat(article.getPublishedAt()));
        holder.author.setText(article.getAuthor());
//        Picasso.with(context).load(article.getUrlToImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener=onItemClickListener;
    }

    public  interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView title,desc,author,published,source,time;
        ImageView imageView;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.title);
            title= (TextView) itemView.findViewById(R.id.title);
            desc= (TextView) itemView.findViewById(R.id.desc);
            author= (TextView) itemView.findViewById(R.id.author);
            published = (TextView) itemView.findViewById(R.id.publishedAt);
            source = (TextView) itemView.findViewById(R.id.source);
            time = (TextView) itemView.findViewById(R.id.time);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_load_photo);
            this.onItemClickListener = onItemClickListener;

        }

        @Override
        public void onClick(View v) {
          onItemClickListener.onItemClick(v,getAdapterPosition());
        }
    }
}
