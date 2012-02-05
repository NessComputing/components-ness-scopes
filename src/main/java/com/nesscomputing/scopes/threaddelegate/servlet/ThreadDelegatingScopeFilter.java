package com.nesscomputing.scopes.threaddelegate.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nesscomputing.logging.Log;
import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedScope;

@Singleton
public class ThreadDelegatingScopeFilter implements Filter
{
    private static final Log LOG = Log.findLog();

    public static final String THREAD_DELEGATING_SCOPE_ACTIVE = ThreadDelegatingScopeFilter.class.getName() + ".active";

    private final ThreadDelegatedScope scope;

    @Inject
    ThreadDelegatingScopeFilter(final ThreadDelegatedScope scope)
    {
        this.scope = scope;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        try {
            // chase out all objects that might be here, because the thread managed earlier to escape without
            // going though the LEAVE below. This is very unlikely to happen, though.
            scope.changeScope(null);

            // Mark to the filters further down that the scope is now active.
            request.setAttribute(THREAD_DELEGATING_SCOPE_ACTIVE, Boolean.TRUE);
            chain.doFilter(request, response);
        }
        finally {
            // All accumulated members (and listeners) will see a 'LEAVE' event now.
            scope.changeScope(null);

            // Remove the marker from the request.
            request.removeAttribute(THREAD_DELEGATING_SCOPE_ACTIVE);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException
    {
        LOG.info("ThreadDelegating scope now available!");
    }

    @Override
    public void destroy()
    {
    }
}
