package lk.ac.mrt.uom.uomonline.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import lk.ac.mrt.uom.uomonline.MainActivity;
import lk.ac.mrt.uom.uomonline.R;
import lk.ac.mrt.uom.uomonline.model.ArticleMinified;
import lk.ac.mrt.uom.uomonline.ui.NewsReadActivity;

/**
 * Created by sineth on 8/11/17.
 */

public class MainRVAdapter extends RecyclerView.Adapter<MainRVAdapter.ArticleMinifiedViewHolder> {

    LinkedHashMap<String,ArticleMinified> articleMinifieds;
    Context context;

    public MainRVAdapter(LinkedHashMap<String,ArticleMinified> articleMinifieds,Context context) {
        this.articleMinifieds = articleMinifieds;
        this.context = context;
    }

    @Override
    public ArticleMinifiedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_button, parent, false);
        return new ArticleMinifiedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleMinifiedViewHolder holder, int position) {
        ArticleMinified article = articleMinifieds.get(articleMinifieds.keySet().toArray()[position]);
        holder.title.setText(article.getTitle());
        holder.url.setText(article.getImageURL());
        Picasso.with(context).load(article.getImageURL()).placeholder(R.mipmap.hadoop).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Toast.makeText(context,"Something went wrong.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleMinifieds.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class ArticleMinifiedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView title;
        TextView url;
        public ArticleMinifiedViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleID);
            url = itemView.findViewById(R.id.url);
            imageView = itemView.findViewById(R.id.coverImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context=view.getContext();
            final Intent intent;
            intent=new Intent(context, NewsReadActivity.class);
            intent.putExtra("Article",articleMinifieds.get(articleMinifieds.keySet().toArray()[getLayoutPosition()]));
            context.startActivity(intent);
        }
    }
}
