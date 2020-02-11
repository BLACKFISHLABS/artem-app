package io.github.blackfishlabs.artem.ui.event;

import java.util.List;

import io.github.blackfishlabs.artem.domain.json.MaterialJson;

public class AddedMaterialItemsEvent {

    private final List<MaterialJson> mItems;

    private AddedMaterialItemsEvent(final List<MaterialJson> items) {
        mItems = items;
    }

    public static AddedMaterialItemsEvent newEvent(final List<MaterialJson> items) {
        return new AddedMaterialItemsEvent(items);
    }

    public List<MaterialJson> getItems() {
        return mItems;
    }
}
