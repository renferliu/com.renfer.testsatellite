package com.renfer.testsatellite;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.renfer.testsatellite.SatelliteMenu.OnSatelliteMenuItemClickListener;
import com.renfer.testsatellite.SatelliteMenu.OnSatelliteMenuStateListener;


public class MainActivity extends Activity {
	private SatelliteMenu menu;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu = (SatelliteMenu)findViewById(R.id.menu);
        menu.setOnSatelliteMenuStateListener(new OnSatelliteMenuStateListener() {
			

			@Override
			public void onMenuOpen(View mainItemView) {
				ImageView iv = (ImageView) mainItemView;
				iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
			}
			

			@Override
			public void onMenuClose(View mainItemView) {
				ImageView iv = (ImageView) mainItemView;
				iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
			}
		});
        
        menu.setOnSatelliteMenuItemClickListener(new OnSatelliteMenuItemClickListener() {
			
			@Override
			public void onItemClick(View view, int pos) {
				Toast.makeText(MainActivity.this, ""+pos, Toast.LENGTH_SHORT).show();
			}
		});
    }

}
