package es.jaf.myshortcuts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private ListviewAdapter adapter;
    private ArrayList<ShortcutItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = PreferencesManager.getItems();
        adapter = new ListviewAdapter(this, items);

        final ListView lvItems = findViewById(R.id.lvItems);
        lvItems.setAdapter(adapter);
        lvItems.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.i(BuildConfig.APPLICATION_ID + "_LOG", items.get(position).getName());
            Intent intent = new Intent(MainActivity.this, NewShortcutActivity.class);
            intent.putExtra("index", position);
            startActivityForResult(intent, 101);
            return false;
        });

        lvItems.setOnItemClickListener((parent, view, position, id) -> {
            String itemPath = items.get(position).getPath();
            Uri contentUri;
            String type;
            if (itemPath.toLowerCase().startsWith("http:")
                    || itemPath.toLowerCase().startsWith("https:")) {
                contentUri = Uri.parse(itemPath);
                type = "text/html";
            } else {
                File file = new File(itemPath);
                String fileName = file.getName();
                String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                contentUri = FileProvider.getUriForFile(MainActivity.this, "es.jaf.myshortcuts", file);
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, type);
                startActivity(intent);
            } catch (Exception e) {
                GlobalApplication.saveException("Error abriendo el fichero " + itemPath, e);
                e.printStackTrace();
            }
        });

        ImageView fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewShortcutActivity.class);
            intent.putExtra("index", -1);
            startActivityForResult(intent, 101);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                int index = data.getIntExtra("index", -1);
                String action = data.getStringExtra("action");
                if (index == -1) {
                    String strItem = data.getStringExtra("item");
                    if (strItem == null || strItem.length() == 0) {
                        return;
                    }
                    items.add(new ShortcutItem(strItem));
                } else if ("delete".equals(action)) {
                    items.remove(index);
                }

                adapter.notifyDataSetChanged();
                PreferencesManager.saveItems(items);
            }
        }
    }

    public void onBackPressed() {
        android.os.Process.killProcess(Process.myPid());
        finish();
    }
}