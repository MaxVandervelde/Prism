/*
 * Copyright (c) 2014-2015 Ink Applications, LLC.
 * Distributed under the MIT License (http://opensource.org/licenses/MIT)
 */
package prism.framework;

import android.app.Activity;
import dagger.ObjectGraph;

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

    final private boolean requireInjectionFlag;

    protected DependencyInjector(GraphContext graphContext, boolean requireInjectionFlag)
    {
        this.graphContext = graphContext;
        this.requireInjectionFlag = requireInjectionFlag;

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
        if (this.requireInjectionFlag && false == target.getClass().isAnnotationPresent(Injected.class)) {
            return;
        }

        ObjectGraph applicationGraph = this.graphContext.getApplicationGraph();

        applicationGraph.injectStatics();
        applicationGraph.inject(target);
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
        if (this.requireInjectionFlag && false == target.getClass().isAnnotationPresent(Injected.class)) {
            return;
        }

        Object[] activityModules = this.graphContext.getActivityModules(context);
        ObjectGraph applicationGraph = this.graphContext.getApplicationGraph();
        ObjectGraph activityGraph = applicationGraph.plus(activityModules);

        Class injectionScope = this.getInjectionScope(target);
        if (null == injectionScope) {
            activityGraph.inject(target);
            return;
        }

        Map<Class, Object> scopeModules = this.graphContext.getScopeModules(context);
        Object scopeModule = scopeModules.get(injectionScope);
        ObjectGraph localGraph = activityGraph.plus(scopeModule);

        localGraph.injectStatics();
        localGraph.inject(target);
    }

    /**
     * Find a scoped module that should be used for the target injectable.
     *
     * @param target The service that is being injected.
     * @return The module specified by the injectable target.
     */
    private Class getInjectionScope(Object target)
    {
        Injected injectedScope = target.getClass().getAnnotation(Injected.class);
        if (null != injectedScope) {
            return  injectedScope.moduleScope();
        }

        ModuleScope moduleScope = target.getClass().getAnnotation(ModuleScope.class);
        if (null != moduleScope) {
            return moduleScope.value();
        }

        return null;
    }
}
