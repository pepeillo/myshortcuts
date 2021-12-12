package es.jaf.myshortcuts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewShortcutActivity extends Activity {
    private EditText txtName;
    private TextView txtPath;
    private int index;
    private ShortcutItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shortcut);

        txtName = findViewById(R.id.txtName);
        txtPath = findViewById(R.id.txtPath);

        setResult(RESULT_CANCELED);

        index = getIntent().getIntExtra("index", -1);
        if (index < 0) {
            item = new ShortcutItem("", "");
        } else {
            item = PreferencesManager.getItems().get(index);
        }
        txtName.setText(item.getName());
        txtPath.setText(item.getPath());

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            if (txtName.getText().length() == 0) {
                Toast.makeText(NewShortcutActivity.this, getString(R.string.name_mandatory), Toast.LENGTH_LONG).show();
                return;
            }
            if (txtPath.getText().length() == 0) {
                Toast.makeText(NewShortcutActivity.this, getString(R.string.path_mandatory), Toast.LENGTH_LONG).show();
                return;
            }
            Intent returnIntent = new Intent();
            returnIntent.putExtra("index", index);
            item.setName(txtName.getText().toString());
            item.setPath(txtPath.getText().toString());
            if (index < 0) {
                returnIntent.putExtra("item", item.toString());
            }
            NewShortcutActivity.this.setResult(Activity.RESULT_OK, returnIntent);
            NewShortcutActivity.this.finish();
        });

        findViewById(R.id.btnFile).setOnClickListener(v -> {
            String current = txtPath.getText().toString();
            showFileChooser();
            /*
            DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.FILE_SELECT;
            properties.root = Environment.getExternalStorageDirectory();
            properties.error_dir = properties.root;
            if (current.length() == 0 || current.toLowerCase().startsWith("http://") || current.toLowerCase().startsWith("https://")) {
                properties.offset = properties.root; //new File(DialogConfigs.DEFAULT_DIR);
            } else {
                properties.offset = new File(current).getParentFile();
            }
            properties.extensions = null;

            FilePickerDialog dialog = new FilePickerDialog(NewShortcutActivity.this ,properties);
            dialog.setTitle(R.string.select_file);
            dialog.show();

            dialog.setDialogSelectionListener(files -> {
                File file = new File(files[0]);
                txtPath.setText(file.getAbsolutePath());
            });
             */
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (index > -1) {
            getMenuInflater().inflate(R.menu.menu_element, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (index < 0) {
            return false;
        }

        if (item.getItemId() == R.id.delete_element) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(NewShortcutActivity.this);
            dialog.setTitle(R.string.app_name)
                    .setIcon(android.R.drawable.ic_menu_help)
                    .setMessage(NewShortcutActivity.this.getString(R.string.confirm_delete))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dlg, ide) -> {
                        Intent intent = new Intent();
                        intent.putExtra("index", index);
                        intent.putExtra("action", "delete");
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    })
                    .setNegativeButton(android.R.string.cancel, (dlg, ide) -> dlg.dismiss());
            dialog.show();
        }
        return false;
    }

    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user doesn't pick a file just return
        if (requestCode != 111 || resultCode != RESULT_OK) {
            return;
        }
        txtPath.setText(getPath(this, data.getData()));
    }

    public String getPath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) { // DocumentProvider
                //System.out.println("hhhhhhhhhh isDocumentUri" );
                if (isExternalStorageDocument(uri)) { // ExternalStorageProvider
                    //System.out.println("hhhhhhhhhh isExternalStorageDocument" );
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                    //System.out.println("hhhhhhhhhh isDownloadsDocument" );
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);

                } else if (isMediaDocument(uri)) { // MediaProvider
                    //System.out.println("hhhhhhhhhh isMediaDocument" );
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)
            //System.out.println("hhhhhhhhhh isContent" );
            if (isGooglePhotosUri(uri)) {
                //System.out.println("hhhhhhhhhh isGooglePhotosUri" );
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
            //System.out.println("hhhhhhhhhh isFile" );
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}