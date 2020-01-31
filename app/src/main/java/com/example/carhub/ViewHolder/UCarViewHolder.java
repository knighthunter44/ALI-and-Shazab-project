package com.example.carhub.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carhub.Interface.ItemClickListner;
import com.example.carhub.R;

public class UCarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView textUsecarName;
    public ImageView iv_Usecar;
    private ItemClickListner carclickListener;



    public UCarViewHolder(View itemView) {
        super(itemView);

        textUsecarName = (TextView) itemView.findViewById(R.id.tv_usecar_name);
        iv_Usecar = (ImageView)itemView.findViewById(R.id.IV_listcar_image);
        itemView.setOnClickListener(this);

    }

    public void setCarclickListener(ItemClickListner carclickListener) {
        this.carclickListener = carclickListener;
    }

    @Override
    public void onClick(View v) {

        carclickListener.onClick(v,getAdapterPosition(),false);

    }
}
