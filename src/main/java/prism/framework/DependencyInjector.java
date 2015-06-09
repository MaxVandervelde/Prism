/*
 * Copyright (c) 2014-2015 Ink Applications, LLC.
 * Distributed under the MIT License (http://opensource.org/licenses/MIT)
 */
package prism.framework;

import android.app.Activity;
import dagger.ObjectGraph;
import org.apache.commons.logging.Log;

import java.util.Map;

/**
 * Injects objects with the application's graph contexts.
 *
 * @author Maxwell Vandervelde (Max@MaxVandervelde.com)
 */
final class DependencyInjector
{
    /**
     * Container for Application and activity graphs.
     */
    final private GraphContext graphContext;

    /**
     * An optional logger for any injection problems.
     */
    private Log logger = new NoOpLog();

    /** Constructor with an application graph. */
    protected DependencyInjector(GraphContext graphContext)
    {
        this.graphContext = graphContext;

        if (null != this.graphContext) {
            this.graphContext.getApplicationGraph().injectStatics();
        }
    }

    /**
     * Run Dagger injections for an Activity.
     *
     * Runs Dagger's application object graph on the class as well as static
     * injections.
     *
     * @param target The activity to inject services into
     */
    public void inject(Activity target)
    {
        this.inject(target, target);
    }

    /**
     * Run Dagger injections for a Fragment.
     *
     * Runs Dagger's application object graph on the class as well as static
     * injections.
     *
     * @param target The fragment to inject services into
     */
    public void inject(android.app.Fragment target)
    {
        Activity parentActivity = target.getActivity();
        this.inject(target, parentActivity);
    }

    /**
     * Run Dagger injections for a Support Fragment.
     *
     * Runs Dagger's application object graph on the class as well as static
     * injections.
     *
     * @param target The fragment to inject services into
     */
    public void inject(android.support.v4.app.Fragment target)
    {
        Activity parentActivity = target.getActivity();
        this.inject(target, parentActivity);
    }

    /**
     * Run Dagger injections for a an arbitrary object.
     *
     * Runs Dagger's application object graph on the class as well as static
     * injections.
     * Will not do activity modules, since there is no activity context.
     *
     * @todo include scopes without an activity context
     * @param target The fragment to inject services into
     */
    public void inject(Object target)
    {
        ObjectGraph applicationGraph = this.graphContext.getApplicationGraph();

        applicationGraph.injectStatics();
        this.inject(applicationGraph, target);
    }

    /**
     * Run Dagger injections for a an arbitrary object, using a designated
     * activity as a context.
     *
     * Runs Dagger's application object graph on the class as well as static
     * injections.
     * Will not do activity modules, since there is no activity context.
     *
     * @param target The fragment to inject services into
     */
    public void inject(Object target, Activity context)
    {
        Object[] activityModules = this.graphContext.getActivityModules(context);
        ObjectGraph applicationGraph = this.graphContext.getApplicationGraph();
        ObjectGraph activityGraph = applicationGraph.plus(activityModules);

        ModuleScope injectionScope = target.getClass().getAnnotation(ModuleScope.class);
        if (null == injectionScope) {
            this.inject(activityGraph, target);
            return;
        }

        Map<Class, Object> scopeModules = this.graphContext.getScopeModules(context);
        Object scopeModule = scopeModules.get(injectionScope.value());
        ObjectGraph localGraph = activityGraph.plus(scopeModule);

        localGraph.injectStatics();
        this.inject(localGraph, target);
    }

    /**
     * Attempts to inject a target with the specified graph of dependencies.
     *
     * If this fails, an error will be logged instead of an exception being
     * thrown. This is to allow automatic injections to proceed even through
     * third-party activities.
     *
     * @param graph The service graph to use for injections.
     * @param target the class containing injectable fields.
     */
    private void inject(ObjectGraph graph, Object target)
    {
        try {
            graph.inject(target);
        } catch(IllegalArgumentException e) {
            this.logger.warn(
                "PROBLEM OCCURRED injecting target with activity context! "
                + "Target class: ("
                + target.getClass().getName()
                + ") Dagger requires that the class is explicitly added into"
                + " `injects={}` on a module.",
                e
            );
        }
    }

    /**
     * Change the logger used for tracking injection problems.
     *
     * @param logger A logger to use for tracking injection problems.
     */
    public void setLogger(Log logger)
    {
        this.logger = logger;
    }
}
