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
 * Denotes that a service should be injected by the framework automatically.
 *
 * This annotation does NOT cause the framework to inject any service, rather
 * this is intended to prevent the framework from injecting services
 * automatically unless this annotation is present.
 *
 * @since 1.1
 * @author Maxwell Vandervelde (Max@MaxVandervelde.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Injected
{
    /**
     * The Module class to include in the injection scope.
     */
    Class moduleScope();
}
