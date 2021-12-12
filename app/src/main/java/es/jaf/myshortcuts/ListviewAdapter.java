package es.jaf.myshortcuts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ListviewAdapter extends ArrayAdapter<ShortcutItem> {
    private final Context context;

    public ListviewAdapter(Activity context, ArrayList<ShortcutItem> items) {
        super(context, 0, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ShortcutItem item = getItem(position);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item, null);

        }
        TextView itemName = view.findViewById(R.id.itemName);
        TextView itemPath = view.findViewById(R.id.itemPath);
        ImageView itemIcon = view.findViewById(R.id.itemIcon);

        itemName.setText(item.getName());
        itemPath.setText(item.getPath());
        itemIcon.setImageDrawable(context.getDrawable(getFileTypeIcon(item.getPath())));

        return view;
    }

    private int getFileTypeIcon(String path) {
        String ext;
        if (path.toLowerCase().startsWith("http:") || path.toLowerCase().startsWith("https:")) {
            ext = "html";
        } else {
            File file = new File(path);
            String fileName = file.getName();
            ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        switch (ext) {
            case "pdf":
                return R.drawable.pdf;
            case "jpg":
            case "png":
            case "jpeg":
            case "gif":
            case "bmp":
                return R.drawable.image;
            case "mp3":
            case "wav":
            case "ogg":
            case "midi":
                return R.drawable.audio;
            case "mp4":
            case "rmvb":
            case "avi":
            case "flv":
            case "3gp":
                return R.drawable.video;
            case "jsp":
            case "html":
            case "htm":
            case "js":
            case "php":
                return R.drawable.html;
            case "jar":
            case "zip":
            case  "rar":
            case "gz":
                //return R.drawable.zip;
            case "apk":
                //return R.drawable.apk;
            case "xls":
            case "xlsx":
                //
            case "doc":
            case "docx":
                //
            case "ppt":
            case "pptx":
                //
            case "txt":
            case "c":
            case "cpp":
            case "xml":
            case "py":
            case "json":
            case "log":
            default:
                return R.drawable.file;
        }
    }

}