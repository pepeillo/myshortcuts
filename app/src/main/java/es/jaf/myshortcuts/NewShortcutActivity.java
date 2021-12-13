package es.jaf.myshortcuts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
            showFileChooser();
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
        if (requestCode == 111 && resultCode == RESULT_OK) {
            txtPath.setText(FileUtils.getPath(this, data.getData()));
        }
    }
 }