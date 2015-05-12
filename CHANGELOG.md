Change Log
==========

v1.1.0
------

 - Added an `@Injected` annotation in order to allow the injectors
   to discriminate services that they should automatically inject.
   This change was made in order to prevent dagger injections from running on
   every class such as activities from third party libraries.
 - A new constructor is available for the kernel that allows users to enforce
   that the new injected annotation is present for injections to run.
 - Module scoping functionality has been moved into the new injector annotation
   and the old one has been deprecated. The new annotation will always take
   precedence over the deprecated definitions, but will continue to work.

v1.0.0
------
 - Created a base framework for injecting classes
 - Provided a way to scope dependencies to individual injected classes.
 - Provided a way to inject activities automatically
 - Provided a way to set Activity layouts automatically
 - Provided standard set of Activity & Application modules.
