/*
 * Copyright (c) 2015 Ink Applications, LLC.
 * Distributed under the MIT License (http://opensource.org/licenses/MIT)
 */
package prism.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a user-displayed name for a particular application element.
 *
 * This can be used to define the name of any element, however it is designed
 * to be used on Activities and Fragments for defining the page/element title
 * that is being displayed.
 * This functionality can be leveraged in logging, analytics, and action bars
 * to identify a visible element.
 * The integer in this corresponds to a resource ID. No implementation exists
 * for hardcoded strings.
 *
 * @author Maxwell Vandervelde (Max@MaxVandervelde.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DisplayName
{
    int value();
}
