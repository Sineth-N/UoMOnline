package lk.ac.mrt.uom.uomonline.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;

import lk.ac.mrt.uom.uomonline.R;
import lk.ac.mrt.uom.uomonline.model.Article;
import lk.ac.mrt.uom.uomonline.ui.NewsReadActivity;

/**
 * Created by sineth on 8/11/17.
 */

public class MainRVAdapter extends RecyclerView.Adapter<MainRVAdapter.ArticleMinifiedViewHolder> {

    private LinkedHashMap<String, Article> articleMinifieds;
    private Context context;

    public MainRVAdapter(LinkedHashMap<String, Article> articleMinifieds, Context context) {
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
        Article article = articleMinifieds.get(articleMinifieds.keySet().toArray()[position]);
        holder.title.setText(article.getTitle());
        holder.tagLine.setText(article.getTagLine());
        Picasso.with(context).load(article.getImageURL()).placeholder(R.mipmap.mora_news_image_please_wait).into(holder.imageView, new Callback() {
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
        TextView tagLine;
        public ArticleMinifiedViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleID);
            tagLine = itemView.findViewById(R.id.tagLine);
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
