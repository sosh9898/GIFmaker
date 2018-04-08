package jy.sopt.gifexample;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by jyoung on 2018. 4. 5..
 */

public class RcvAdapter extends RecyclerView.Adapter {
    List<Bitmap> captureItems;

    public RcvAdapter(List<Bitmap> captureItems) {
        this.captureItems = captureItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.capture_item_layout, parent, false);
        return new CusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CusViewHolder)holder).bindView(captureItems.get(position));
    }

    @Override
    public int getItemCount() {
        return captureItems != null ? captureItems.size() : 0;
    }

    public void updateItem(List<Bitmap> captureItems){
        this.captureItems = captureItems;
        notifyDataSetChanged();
    }

    public class CusViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public CusViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.capture_img);
        }

        public void bindView(Bitmap captureItem){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            captureItem.compress(Bitmap.CompressFormat.PNG, 10, stream);
            Glide.with(image.getContext())
                    .load(stream.toByteArray())
                    .asBitmap()
                    .into(image);
        }


    }
}
