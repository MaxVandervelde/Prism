Using Prism
===========

A Simple Setup
--------------

To begin using Prism, you'll need to keep an instance of the Kernel. To do this
you will create your `PrismKernel` in your Application class's `onCreate` method
and make your Activity implement the `KernelContext` interface.

```java
public class MyApplication extends Application implements KernelContext {
    private PrismKernel kernel;

    @Override public void onCreate() {
        super.onCreate();
        
        this.kernel = new PrismKernel();
    }

    @Override public PrismKernel getKernel() {
        return this.kernel;
    }
}
```

Prism Bootstrapping Facade
--------------------------

Prism provides a simple interface for accessing the bootstrap methods from any
activity or Fragment of a Prism Application.

```java
public class MyFragment extends Fragment {
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PrismFacade.bootstrap(this);
    }
}
```

Automatic Activity Bootstrapping
--------------------------------

Prism can be Bootstrapped manually with the facade for any class, however
Activities can be done automatically with lifecycle callbacks.

Prism provides a subscriber to do this automatically. All you have to do
is create a `LifecycleSubscriber` and give it to the
`registerActivityLifecycleCallbacks` method in your application's `onCreate`

```java
public class MyApplication extends Application implements KernelContext {
    private PrismKernel kernel;

    @Override public void onCreate() {
        super.onCreate();
        
        this.kernel = new PrismKernel(this);
        this.registerActivityLifecycleCallbacks(new LifecycleSubscriber(this));
    }

    @Override public PrismKernel getKernel() {
        return this.kernel;
    }
}
```

Automatic Content Views
-----------------------

By default, Prism will try to set a content view on your Activities.
You can specify which layout an activity uses by annotating it with `@Layout`
instead of calling `setContentView()` yourself.

```java
@Layout(R.layout.main)
public class MyActivity extends Activity {
    /* 
        With Automatic Bootstrapping and layout injection this class can work
        Completely empty without a base class! Pretty neat, huh?
    */
}
```

Using With Dependency Injection
-------------------------------

Prism has has a setup for Dependency Injection using [Dagger][1] out of the box.
Setting up your application with dagger requires minimal configuration.

Prism defines a series of Graph Contexts for use with dagger. To provide these
you will need to create a class implementing the `GraphContext` interface.
For simplicity, this can be your application class itself. Once you implement
the `GraphContext` interface on your application you will be required to provide
the graphs and modules needed by dagger.

### Application Graph ###

The application graph is the most commonly used graph in your application.
Everything injected by dagger will use this graph. The context gets this graph
by invoking the `public ObjectGraph getApplicationGraph()` method, so you will
need to implement this. However, for performance reasons, you should not create
the Application Graph every time. Instead, you should create it in the 
application's `onCreate()` method, and store it.

For information on creating Dagger modules, see the [Documentation][2] for
Dagger.

```java
public class MyApplication extends Application implements GraphContext, KernelContext {
    private ObjectGraph applicationGraph;

    @Override public void onCreate() {
        super.onCreate();
        
        this.applicationGraph = ObjectGraph.create(this.getApplicationModules());
    }

    @Override public ObjectGraph getApplicationGraph() {
        return this.applicationGraph;
    }

    private Object[] getApplicationModules() { 
        return new Object[] {
            new ApplicationModule(this),
        };
    }
}
```

### Activity Graph ###

Next you'll set up dependency modules that are only available with an activity
context (Activities and Fragments). These modules are provided in a different
way. This is because, unlike the Application graph, the Activity graph is
re-created every time a new activity or fragment is created. While this is
slower than using the application graph, it does provide a way to hook into the 
activity in your dependencies, as well as give a narrower scope since these
dependencies will not be available to the application services.

```java
    @Override public Object[] getActivityModules(Activity activity) {
        return new Object[] {
            new ActivityModule(activity),
        };
    }
```

### Scope-Specific Graph ###

Lastly is the most narrow version of dependency modules, which are specific
to a designated scope. This is an additional feature in Prism not provided
in Dagger.

Scope-Specific modules allow you to designate an injected class as a part of
an exclusive set of modules. These modules will not be available to injected
classes unless they are in that scope.

These modules are defined in our graph context as a map where the key is
the identifier class for the module and the value is an instantiated version of 
that same class. 

```java
    @Override public Map<Class, Object> getScopeModules(Activity activity) {
        LinkedHashMap<Class, Object> definitions = new LinkedHashMap<>();
        definitions.put(ExampleModule.class, new ExampleModule());

        return definitions;
    }
```

Doing so will allow you to give access to the modules only when the injected
class is annotated with `@ModuleScope(ExampleModule.class)`

### Finishing Dagger Injection ###

Once you've set up a Graph Context, you just need to give it to Prism. This
can be done in the Kernel's constructor when it is created:

```java
    @Override public void onCreate() {
        super.onCreate();

        this.applicationGraph = ObjectGraph.create(this.getApplicationModules());
        
        this.kernel = new PrismKernel(this); // add the graph context (this)
    }
```

### Full Injection Example ###

```java
public class MyApplication extends Application implements GraphContext, KernelContext {
    private ObjectGraph applicationGraph;
    private PrismKernel kernel;

    @Override public void onCreate() {
        super.onCreate();

        this.applicationGraph = ObjectGraph.create(this.getApplicationModules());
        this.kernel = new PrismKernel(this);
    }

    @Override public PrismKernel getKernel() {
        return this.kernel;
    }

    @Override public ObjectGraph getApplicationGraph() {
        return this.applicationGraph;
    }

    @Override public Map<Class, Object> getScopeModules(Activity activity) {
        LinkedHashMap<Class, Object> definitions = new LinkedHashMap<>();
        definitions.put(ExampleModule.class, new ExampleModule());

        return definitions;
    }

    @Override public Object[] getActivityModules(Activity activity) {
        return new Object[] {
            new ActivityModule(activity),
        };
    }

    private Object[] getApplicationModules() { 
        return new Object[] {
            new ApplicationModule(this),
        };
    }
}
```

[1]: http://square.github.io/dagger/
[2]: http://square.github.io/dagger/#using
