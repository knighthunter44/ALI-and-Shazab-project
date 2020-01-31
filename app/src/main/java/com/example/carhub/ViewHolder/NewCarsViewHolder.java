package com.example.carhub.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carhub.Interface.ItemClickListner;
import com.example.carhub.R;

import androidx.recyclerview.widget.RecyclerView;

public class NewCarsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

    public TextView text_Newcars;
    public ImageView image_Newcars;
    private ItemClickListner itemClickListner_newcars;
    public NewCarsViewHolder(View itemView) {
        super(itemView);
        text_Newcars= (TextView) itemView.findViewById(R.id.tv_usecar_name);
        image_Newcars= (ImageView) itemView.findViewById(R.id.IV_listcar_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListner_newcars(ItemClickListner itemClickListner_newcars) {
        this.itemClickListner_newcars = itemClickListner_newcars;
    }

    @Override
    public void onClick(View v) {
        itemClickListner_newcars.onClick(v,getAdapterPosition(),false);
    }
}
