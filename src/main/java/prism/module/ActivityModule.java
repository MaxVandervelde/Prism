/*
 * Copyright (c) 2015 Ink Applications, LLC.
 * Distributed under the MIT License (http://opensource.org/licenses/MIT)
 */
package prism.module;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.WindowManager;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Provides access to Android's Activity-level services.
 *
 * @author Maxwell Vandervelde (Max@MaxVandervelde.com)
 */
@Module(library = true)
@SuppressWarnings("unused")
final public class ActivityModule
{
    /**
     * Activity context to fetch services from.
     */
    final private Activity activity;

    /**
     * @param activity The Activity that all of the provided resources
     *                 will be based off of.
     */
    public ActivityModule(Activity activity)
    {
        this.activity = activity;
    }

    /**
     * Current Activity instance.
     *
     * Avoid depending on this service in favor of more granular services.
     *
     * @return The currently activity instance that this module is bound to.
     */
    @Provides
    @Singleton
    public Activity activityContext()
    {
        return this.activity;
    }

    /**
     * Instantiates a layout XML file into its corresponding View objects.
     *
     * @return LayoutInflater instance that this Window retrieved from its Context.
     */
    @Provides
    @Singleton
    public LayoutInflater inflater()
    {
        return this.activity.getLayoutInflater();
    }

    /**
     * Interface for interacting with Fragment objects inside of an Activity.
     *
     * @return The FragmentManager for interacting with fragments associated with this activity.
     */
    @Provides
    @Singleton
    public FragmentManager fragmentManager()
    {
        return this.activity.getFragmentManager();
    }

    /**
     * Used to instantiate menu XML files into Menu objects.
     *
     * @return a MenuInflater with the current activity context.
     */
    @Provides
    @Singleton
    public MenuInflater menuInflater()
    {
        return this.activity.getMenuInflater();
    }

    /**
     * The interface that apps use to talk to the window manager.
     *
     * @return The window manager for showing custom windows.
     */
    @Provides
    @Singleton
    public WindowManager windowManager()
    {
        return this.activity.getWindowManager();
    }
}
