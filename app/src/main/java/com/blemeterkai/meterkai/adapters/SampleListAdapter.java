package com.blemeterkai.meterkai.adapters;

import android.content.Context;
import android.graphics.Color; // Import Android's Color class
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;

import com.blemeterkai.meterkai.R;
import com.blemeterkai.meterkai.ui.main.item.SampleListItem;

import java.util.List;

public class SampleListAdapter extends ArrayAdapter<SampleListItem> {

    private int mResource;
    private List<SampleListItem> mItems;
    private LayoutInflater mInflater;
    private Context mContext; // Keep context for accessing resources like drawables

    public SampleListAdapter(Context context, int resource, List<SampleListItem> items) {
        super(context, resource, items);
        mContext = context; // Store context
        mResource = resource;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        }
        else {
            view = mInflater.inflate(mResource, parent, false);
        }

        SampleListItem item = mItems.get(position);

        // Title
        TextView title = view.findViewById(R.id.title);
        if (title != null) {
            title.setText(item.getTitle());
            title.setTextColor(item.getColor()); // We are not setting text color based on item.getColor() anymore
        }

        // Contents
        TextView contents = view.findViewById(R.id.contents);
        if (contents != null) {
            contents.setText(item.getContents()); // Using item.getContents() directly
            // contents.setTextColor(item.getColor()); // We are not setting text color based on item.getColor() anymore
        }

        // Status Dot Indicator
        ImageView statusDot = view.findViewById(R.id.status_dot_indicator);
        if (statusDot != null) {
            int itemColor = item.getColor();
            if (itemColor == Color.BLUE) {
                statusDot.setImageResource(R.drawable.ic_status_dot_blue);
            } else if (itemColor == Color.RED) {
                statusDot.setImageResource(R.drawable.ic_status_dot_red);
            } else { // Covers Color.GRAY, Color.BLACK, or any other default
                statusDot.setImageResource(R.drawable.ic_status_dot_default);
            }
            statusDot.setVisibility(View.VISIBLE); // Ensure it's visible
        }

        // Chevron (optional, if you want to set click listeners or visibility)
        ImageView chevronDetails = view.findViewById(R.id.chevron_details);
        if (chevronDetails != null) {
            // You can add click listeners here if needed, as discussed before,
            // or simply ensure it's visible if it's always part of the layout.
            chevronDetails.setVisibility(View.VISIBLE);
        }

        return view;
    }
}