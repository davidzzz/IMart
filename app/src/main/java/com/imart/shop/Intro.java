package com.imart.shop;


import com.imart.shop.intro.AppIntro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Intro extends AppIntro {

	@Override
	public void init(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro4));
	}

	 private void loadMainActivity(){
	        Intent intent = new Intent(this, MainActivity.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	        this.finish();
	    }

	    @Override
	    public void onNextPressed() {
	    }

	    @Override
	    public void onSkipPressed() {
	        loadMainActivity();
	    }

	    @Override
	    public void onDonePressed() {
	        loadMainActivity();
	    }

	    @Override
	    public void onSlideChanged() {
	    }

	    public void getStarted(View v){
	        loadMainActivity();
	    }
	}
