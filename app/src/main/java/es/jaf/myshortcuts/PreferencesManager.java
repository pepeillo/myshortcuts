package es.jaf.myshortcuts;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PreferencesManager {
    private static ArrayList<ShortcutItem> listItems = null;

    private static void load() {
        listItems = new ArrayList<>();
        try {
            SharedPreferences prefs = GlobalApplication.getInstance().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
            Set<String> set = prefs.getStringSet("listItems", null);
            if (set != null) {
                List<String> list = new ArrayList<>(set);
                listItems.clear();
                for (String c : list) {
                    listItems.add(new ShortcutItem(c));
                }
                listItems.sort((o1, o2) -> {
                    String o1low = o1.getName().toLowerCase();
                    String o2low = o2.getName().toLowerCase();
                    if (o1low.compareTo(o2low) > 0 ) {
                        return 1;
                    }
                    if (o2low.compareTo(o1low) > 0) {
                        return -1;
                    }
                    return 0;
                });
            }
        } catch (Exception e) {
            GlobalApplication.saveException("Error en PreferencesManager.load()", e);
        }
    }

     protected static void saveItems(ArrayList<ShortcutItem> value) {
        try {
            listItems = (value == null ? new ArrayList<>() : value);
            Set<String> hs = null;
            if (value != null) {
                hs = new HashSet<>();
                for (ShortcutItem str : value) {
                    hs.add(str.toString());
                }
            }
            SharedPreferences.Editor editor = GlobalApplication.getInstance().getApplicationContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit();
            editor.putStringSet("listItems", hs);
            editor.apply();
            editor.commit();
        } catch (Exception e) {
            GlobalApplication.saveException("Error en saveItems", e);
        }
    }

    protected static ArrayList<ShortcutItem> getItems() {
        if (listItems == null) {
            load();
        }
        return listItems;
    }
}
