package com.example.nhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.core.util.Pair;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhu.adapter.ArticlesAdapter;
import com.example.nhu.api.ApiClient;
import com.example.nhu.api.ApiInterface;
import com.example.nhu.models.Article;
import com.example.nhu.models.Contact;
import com.example.nhu.models.News;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView rvArticles;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private List<Article> search_articles = new ArrayList<>();
    private ArticlesAdapter adapter;
    private final String apikey="210699";
    ProgressBar firstPro;
    ArrayList<Contact> contacts;
    private RelativeLayout errorLayout;
    private ImageView errorImage;
    private TextView errorTitle,errorMessage;
    private Button btnRetry;
    private String TAG=MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorLayout = findViewById(R.id.errorLayout);
        errorImage = findViewById(R.id.errorImage);
        errorTitle = findViewById(R.id.errorTitle);
        errorMessage = findViewById(R.id.errorMessage);
        btnRetry = findViewById(R.id.btnRetry);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        firstPro = findViewById(R.id.first_pro);
        onLoadingSwipeRefresh("");

    }
    public void LoadJson(final String keyword){
        errorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<News> call ;
        if(!keyword.equals(""))
        {
//            call = apiInterface.getSearchNews(apikey,keyword);
            call = apiInterface.getNews(apikey);
        }
        else {
            call = apiInterface.getNews(apikey);
        }
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful()&&response.body().getArticles()!=null)
                {
                    if(!articles.isEmpty()){
                        articles.clear();
                    }
                    if(!search_articles.isEmpty())
                    {
                        search_articles.clear();
                    }
                    articles = response.body().getArticles();
                    int a = articles.size();
                    try {
                        rvArticles = (RecyclerView) findViewById(R.id.recyclerView);
//                        firstPro = findViewById(R.id.first_pro);
//                      contacts = Contact.createContactsList(20);
                        if (!keyword.equals(""))
                        {
                            for(int i=0;i<articles.size();i++)
                            {
                                if(articles.get(i).getTitle().toLowerCase().indexOf(keyword.toLowerCase())!=-1)
                                {
                                    search_articles.add(articles.get(i));
                                }
                            }
                            adapter = new ArticlesAdapter(search_articles,MainActivity.this);
                            rvArticles.setAdapter(adapter);
                            rvArticles.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            firstPro.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            initListener();

                        }
                        else {
                            adapter = new ArticlesAdapter(articles, MainActivity.this);
                            rvArticles.setAdapter(adapter);
                            rvArticles.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            firstPro.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            initListener();
                        }
                    }catch (Exception e)
                    {
                        Log.d(TAG, "onResponse: "+e);
                    }
                    Toast.makeText(MainActivity.this,"yes Result: "+articles.size(),Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(MainActivity.this,"No Result",Toast.LENGTH_SHORT).show();
                    firstPro.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    String errorCode;
                    switch (response.code())
                    {
                        case 404:
                            errorCode = "404 not found";
                            break;
                        case 500:
                            errorCode = "500 server broken";
                            break;
                        default:
                            errorCode = "unknown error";
                            break;
                    }
                    showErrorMessage(
                            R.drawable.no_result,"No Result","Please Try again!\n"+errorCode
                    );
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Loi o day",Toast.LENGTH_SHORT).show();
                firstPro.setVisibility(View.INVISIBLE);
                Log.e("YOUR_APP_LOG_TAG", "I got an error", t);
                swipeRefreshLayout.setRefreshing(false);
                showErrorMessage(
                        R.drawable.no_result,"No Result","Please Try again!\n"+t.toString()
                );
            }
        });

    }
    private void initListener(){
         adapter.setOnItemClickListener(new ArticlesAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(View view, int position) {
                 ImageView imageView = view.findViewById(R.id.img);
                 Intent intent = new Intent(MainActivity.this,NewsDetailActivity.class);
                 Article article = articles.get(position);
                 intent.putExtra("url",article.getUrl());
                 intent.putExtra("title",article.getTitle());
                 intent.putExtra("img",article.getUrlToImage());
                 intent.putExtra("date",article.getPublishedAt());
                 intent.putExtra("source",article.getSource().getName());
                 intent.putExtra("author",article.getAuthor());
                 intent.putExtra("content",article.getContent());
                 Pair<View,String> pair = Pair.create((View)imageView, ViewCompat.getTransitionName(imageView));
                 ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                         MainActivity.this,pair);
                 if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
                 {
                     startActivity(intent,optionsCompat.toBundle());
                 }
                 else {
                     startActivity(intent);
                 }
             }
         });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItem favoriteMenuItem = menu.findItem(R.id.action_login);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>2)
                {
                   onLoadingSwipeRefresh(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onLoadingSwipeRefresh(newText);
                return false;
            }
        });
        searchMenuItem.getIcon().setVisible(false,false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_login)
        {
            startActivity(new Intent(MainActivity.this,LoginFacbookActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showErrorMessage(int imageView, String title, String message)
    {
        if(errorLayout.getVisibility()==View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
        }
        errorImage.setImageResource(imageView);
        errorTitle.setText(title);
        errorMessage.setText(message);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           onLoadingSwipeRefresh("");
            }
        });
    }

    @Override
    public void onRefresh() {
        onLoadingSwipeRefresh("");
    }
    private void onLoadingSwipeRefresh(final String keyword)
    {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadJson(keyword);
                    }
                }
        );
    }
}