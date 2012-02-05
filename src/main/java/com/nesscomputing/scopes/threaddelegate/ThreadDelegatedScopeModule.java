package com.nesscomputing.scopes.threaddelegate;


import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.nesscomputing.scopes.threaddelegate.servlet.ThreadDelegatingScopeFilter;

/**
 * Installs the ThreadDelegated Scope in an application.
 */
public class ThreadDelegatedScopeModule extends ServletModule
{
    @Override
    public void configureServlets()
    {
        bind(ThreadDelegatedScope.class).toInstance(ThreadDelegatedScope.SCOPE);

        bindScope(ThreadDelegated.class, ThreadDelegatedScope.SCOPE);

        bind(ThreadDelegatingScopeFilter.class).in(Scopes.SINGLETON);
        filter("/*").through(ThreadDelegatingScopeFilter.class);
    }
}
