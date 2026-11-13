package com.example.cardmatchinggame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 控制器（Controller）：应用首页。
 * 实验五新增选项菜单（menu_main.xml）与「关于」对话框。
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_start).setOnClickListener(v -> startGame());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_start) {
            startGame();
            return true;
        } else if (itemId == R.id.menu_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startGame() {
        startActivity(new Intent(this, GameActivity.class));
    }

    /** 点击菜单项后弹出的关于对话框，标题 / 正文 / 按钮文字全部来自 strings.xml */
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about_title)
                .setIcon(R.drawable.ic_info)
                .setMessage(R.string.about_message)
                .setPositiveButton(R.string.dialog_ok, null)
                .show();
    }
}
