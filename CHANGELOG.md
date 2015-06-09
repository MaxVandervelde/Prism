Change Log
==========

v1.1.0
------
 - Changed injections to not crash if the target is missing from any dagger
   modules inject statements.
 - Added an optional logger to the prism kernel that will be used for any
   internal services. Currently used to log errors during injections due to
   the change in preventing crashes.

v1.0.0
------
 - Created a base framework for injecting classes
 - Provided a way to scope dependencies to individual injected classes.
 - Provided a way to inject activities automatically
 - Provided a way to set Activity layouts automatically
 - Provided standard set of Activity & Application modules.
