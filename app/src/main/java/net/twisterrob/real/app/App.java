package net.twisterrob.real.app;

import javax.inject.Inject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class App extends Application implements HasActivityInjector {

	@Inject
	DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

	@Inject
	AppComponent appComponent;

	@Override protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this); // [multidex]
	}

	@Override public void onCreate() {
		super.onCreate();
		Stetho.initializeWithDefaults(this); // [stetho]
		createAppComponent().inject(this);
	}

	protected @NonNull AppComponent createAppComponent() {
		return DaggerAppComponent.builder()
		                         .application(this)
		                         .build();
	}

	public @NonNull AppComponent getAppComponent() {
		return appComponent;
	}

	@Override
	public AndroidInjector<Activity> activityInjector() { // [dagger-android]
		return dispatchingActivityInjector;
	}
}
