package com.roncoder.bookstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.roncoder.bookstore.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NavAdapter extends BaseExpandableListAdapter {

    private List<String> listGroup;
    private Map<String, List<String>> listChild;

    public NavAdapter(List<String> listGroup, Map<String, List<String>> listChild) {
        this.listGroup = listGroup;
        this.listChild = listChild;
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<String> list = listChild.get(listGroup.get(groupPosition));
        if (list != null)
            return list.get(childPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String group = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        }
        TextView groupView = convertView.findViewById(R.id.item_group);
        groupView.setText(group);
        if (isExpanded)
            groupView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_drop_up, 0);
        else
            groupView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_drop_down, 0);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        String child = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drop_down, parent, false);
        }
        TextView childView = convertView.findViewById(R.id.item);
        childView.setText(child);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
