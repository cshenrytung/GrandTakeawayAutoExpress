package com.share.gta.domain.model;

/**
 * Created by diego.rotondale on 08/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class MenuItem {
    private String icon;
    private String label;

    public MenuItem(String icon, String label) {
        this.icon = icon;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconAndLabel() {
        return getIcon() + getLabel();
    }
}
