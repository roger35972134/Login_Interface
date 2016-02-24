package com.example.roger.parsetest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;

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
    Firebase ref;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        ButterKnife.bind(this);
        Intent intent = this.getIntent();
        Firebase.setAndroidContext(this);


        PlayerId = intent.getStringExtra("PLAYER_ID");

        ref = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/" + PlayerId);
        toolbarSet("Rich Man");
        setPhoto();

        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setPhoto() {
        ref.child("photo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    byte[] base = Base64.decode(dataSnapshot.getValue().toString(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(base, 0, base.length);
                    ImageView userPhoto = (ImageView) findViewById(R.id.userPhoto);
                    userPhoto.setImageBitmap(bitmap);
                    TextView userName = (TextView) findViewById(R.id.userName);
                    userName.setText(PlayerId);
                } else {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    setToast("Select your profile photo");
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //ImageView imageView = (ImageView) findViewById(R.id.userPhoto);
            //imageView.setImageURI(selectedImage);
            //TextView userName = (TextView) findViewById(R.id.userName);
            //userName.setText(PlayerId);
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bitmap.recycle();
            byte[] bytesArray = stream.toByteArray();
            String imageFile = Base64.encodeToString(bytesArray, Base64.DEFAULT);

            ref.child("photo").setValue(imageFile);
            /*ParseFile parseFile= new ParseFile(file);


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
            }*/
        }

    }

    public void setToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.custom_toast));
        TextView toastText = (TextView) layout.findViewById(R.id.toastText);
        toastText.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putString("PLAYER_ID", PlayerId);
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
                SetMapFragment setMapFragment=new SetMapFragment();
                setMapFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame,setMapFragment);
                fragmentTransaction.commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
