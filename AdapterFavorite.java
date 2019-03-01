package com.app.yourrecipesapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.yourrecipesapp.Config;
import com.app.yourrecipesapp.R;
import com.app.yourrecipesapp.activities.ActivityRecipesDetail;
import com.app.yourrecipesapp.utilities.Constant;
import com.app.yourrecipesapp.models.ItemFavorite;
import com.app.yourrecipesapp.utilities.GDPR;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.ViewHolder> {

    private Context context;
    private List<ItemFavorite> arrayItemFavorite;
    ItemFavorite itemFavorite;
    private InterstitialAd interstitialAd;
    private int counter = 1;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;
        public MaterialRippleLayout materialRippleLayout;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.news_title);
            image = (ImageView) view.findViewById(R.id.news_image);
            materialRippleLayout = (MaterialRippleLayout) view.findViewById(R.id.ripple);

        }

    }

    public AdapterFavorite(Context mContext, List<ItemFavorite> arrayItemFavorite) {
        this.context = mContext;
        this.arrayItemFavorite = arrayItemFavorite;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_recipes_list, parent, false);

        loadInterstitialAd();

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemFavorite = arrayItemFavorite.get(position);

        Typeface font1 = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        holder.title.setTypeface(font1);

        holder.title.setText(itemFavorite.getNewsHeading());

        Picasso.with(context)
                .load(Config.SERVER_URL + "/upload/thumbs/" + itemFavorite.getNewsImage())
                .placeholder(R.drawable.ic_thumbnail)
                .resize(Constant.PICASSO_WIDTH_HEIGHT_SIZE, Constant.PICASSO_WIDTH_HEIGHT_SIZE)
                .centerCrop()
                .into(holder.image);

        holder.materialRippleLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                itemFavorite = arrayItemFavorite.get(position);
                int pos = Integer.parseInt(itemFavorite.getCatId());

                Intent intent = new Intent(context, ActivityRecipesDetail.class);
                intent.putExtra("POSITION", pos);
                Constant.NEWS_ITEMID = itemFavorite.getCatId();

                context.startActivity(intent);

                showInterstitialAd();

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayItemFavorite.size();
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            MobileAds.initialize(context, context.getString(R.string.admob_app_id));
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(context.getResources().getString(R.string.admob_interstitial_id));
            final AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd((Activity) context)).build();
            interstitialAd.loadAd(adRequest);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(adRequest);
                }
                @Override
                public void onAdLoaded() {
                    Log.d("admob", "Interstitial is loaded.");
                }

            });
        }
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                if (counter == Config.ADMOB_INTERSTITIAL_ADS_INTERVAL) {
                    interstitialAd.show();
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }
    }
}
