package io.github.blackfishlabs.artem;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import io.github.blackfishlabs.artem.data.realm.RealmMigrationImpl;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initLogging();
        initRealm();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }
        });

    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    protected void initLogging() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(final StackTraceElement element) {
                return super.createStackElementTag(element) + ":" + element.getLineNumber();
            }
        });
    }

    protected void initRealm() {
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("artem.realm")
                .schemaVersion(1L)
                .migration(new RealmMigrationImpl())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

}

