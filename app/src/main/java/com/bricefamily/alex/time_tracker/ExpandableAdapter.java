package com.bricefamily.alex.time_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bricenangue on 16/03/16.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<Category> categoryName=new ArrayList<Category>();
    ArrayList<ArrayList<SubCategory>> subCategoryName = new ArrayList<ArrayList<SubCategory>>();
    ArrayList<Integer> subCategoryCount = new ArrayList<Integer>();
    int count;
    private Context context;

    SubCategory singleChild = new SubCategory();

    public ExpandableAdapter(Context context, ArrayList<Category> categoryName, ArrayList<ArrayList<SubCategory>> subCategoryName, ArrayList<Integer> subCategoryCount)
    {

        layoutInflater = LayoutInflater.from(context);
        this.categoryName= categoryName;
        this.subCategoryName = subCategoryName;
        this.subCategoryCount = subCategoryCount;
        this.count= categoryName.size();
        this.context=context;


    }

    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition)
    {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public int getGroupCount()
    {

        return categoryName.size();
    }

    @Override
    public int getChildrenCount(int i)
    {

        if(i == 0) {
            return 5;
        } else {
            return 0;
        }

    }

    @Override
    public Object getGroup(int i)
    {
        return categoryName.get(i).getCatName();
    }

    @Override
    public SubCategory getChild(int i, int i1)
    {

        ArrayList<SubCategory> tempList = new ArrayList<SubCategory>();
        tempList =  subCategoryName.get(i);
        return tempList.get(i1);

    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup)
    {

        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.expandablecategory, viewGroup, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.cat_desc_1);
        ImageView v=(ImageView)view.findViewById(R.id.collapse_arrow);
        textView.setText(getGroup(i).toString());

        switch (getGroup(i).toString()){
            case "OVERVIEW":
                v.setVisibility(View.GONE);

                break;
            case "PREFERENCES":
                v.setVisibility(View.GONE);

                break;
            case "ABOUT":
                v.setVisibility(View.GONE);

                break;
           default:
                v.setVisibility(View.VISIBLE);
                break;
        }

        return view;

    }


    @Override
    public View getChildView(int i, int i1, boolean isExpanded, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.expandablesubcat, viewGroup, false);

        }

        singleChild = getChild(i,i1);

        TextView childSubCategoryName = (TextView) view.findViewById(R.id.subcat_name);
        View v=(View)view.findViewById(R.id.view1);
        switch (singleChild.getSubCatName()){
            case "Normal":
                v.setBackgroundColor(context.getResources().getColor(R.color.normal));

                break;
            case "Business":
                v.setBackgroundColor(context.getResources().getColor(R.color.business));

                break;
            case "Birthdays":
                v.setBackgroundColor(context.getResources().getColor(R.color.birthdays));

                break;
            case "Grocery":
                v.setBackgroundColor(context.getResources().getColor(R.color.grocery));

                break;
            case "Work Plans":
                v.setBackgroundColor(context.getResources().getColor(R.color.workPlans));

                break;
        }

        childSubCategoryName.setText(singleChild.getSubCatName());

        return view;

    }

    @Override
    public boolean isChildSelectable(int i, int i1)
    {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }


}
