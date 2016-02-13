package com.example.roger.parsetest;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int RESULT_LOAD_IMAGE = 1;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    String PlayerId;
    Bitmap bitmap;
    File file;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);
        Intent intent = this.getIntent();


        PlayerId = intent.getStringExtra("PLAYER_ID");
        toolbarSet("Rich Man");
        setPhoto();

        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setPhoto() {
        ParseQuery query = ParseQuery.getQuery("UserData");
        query.whereEqualTo("Id", PlayerId);
        try {
            List<ParseObject> objects=query.find();
            ParseObject parseObject = objects.get(0);
            ParseFile parseFile = (ParseFile) parseObject.get("photo");
            if (parseFile != null) {
                parseFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null && data.length != 0) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                            TextView userName=(TextView)findViewById(R.id.userName);
                            userName.setText(PlayerId);
                            ImageView userPhoto=(ImageView)findViewById(R.id.userPhoto);
                            userPhoto.setImageBitmap(bitmap);
                        } else {
                            Toast.makeText(MainActivity.this, "Shit! There's some error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath = null;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //LayoutInflater layoutInflater =LayoutInflater.from(this);
            //View view=layoutInflater.inflate(R.layout.nav_header_main, null);


            file=new File(picturePath);
            System.out.println(file);
            ParseFile parseFile= new ParseFile(file);


            ParseQuery query=ParseQuery.getQuery("UserData");
            query.whereEqualTo("Id", PlayerId);
            try {
                List<ParseObject> objects=query.find();
                ParseObject object=objects.get(0);
                object.put("photo", parseFile);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ImageView imageView = (ImageView) findViewById(R.id.userPhoto);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        TextView textView = (TextView) findViewById(R.id.userName);
                        textView.setText(PlayerId);
                    }
                });

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            TextView textView=(TextView)findViewById(R.id.userName);
            ImageView imageView = (ImageView) findViewById(R.id.userPhoto);

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            textView.setText(PlayerId);

            file=new File(picturePath);
            System.out.println(file);
            ParseFile parseFile= new ParseFile(file);
            //bitmap = BitmapFactory.decodeFile(picturePath);
            //userPhoto.setImageBitmap(bitmap);


            ParseQuery query=ParseQuery.getQuery("UserData");
            query.whereEqualTo("Id", PlayerId);
            try {
                List<ParseObject> objects=query.find();
                ParseObject object=objects.get(0);
                object.put("photo", parseFile);
                object.saveInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }*/

    public void toolbarSet(String Title) {
        toolbar.setTitle(Title);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle bundle=new Bundle();
        bundle.putString("PLAYER_ID",PlayerId);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (id) {
            case R.id.dice_rolling:
                toolbarSet("Dice");
                DiceFragment diceFragment = new DiceFragment();
                fragmentTransaction.replace(R.id.frame, diceFragment);
                fragmentTransaction.commit();
                break;
            case R.id.nav_map:
                toolbarSet("Map");
                MapsFragment fragment = new MapsFragment();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commit();
                break;
            case R.id.nav_assets:
                toolbarSet("Assets");
                AssetsFragment assetsFragment=new AssetsFragment();
                assetsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame,assetsFragment);
                fragmentTransaction.commit();
                break;
            case R.id.nav_store:
                toolbarSet("Store");
                StoreFragment storeFragment=new StoreFragment();
                //storeFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame,storeFragment);
                fragmentTransaction.commit();
                break;
            case R.id.nav_profile:
                toolbarSet("Profile");
                break;
            case R.id.nav_players:
                toolbarSet("Players");
                break;
            case R.id.nav_setting:
                toolbarSet("Setting");
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
