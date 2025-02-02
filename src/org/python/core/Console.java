// Copyright (c) 2013 Jython Developers
package org.python.core;

import java.io.IOException;

/**
 * A class named in configuration as the value of <code>python.console</code> must implement this
 * interface, and provide a constructor with a single <code>String</code> argument, to be acceptable
 * during initialization of the interpreter. The argument to the constructor names the encoding in
 * use on the console. Such a class may provide line editing and history recall to an interactive
 * console. A default implementation (that does not provide any such facilities) is available as
 * {@link PlainConsole}.
 */
public interface Console {

    /**
     * Complete initialization and (optionally) install a stream object with line-editing as the
     * replacement for <code>System.in</code>.
     *
     * @throws IOException in case of failure related to i/o
     */
    public void install() throws IOException;

    /**
     * Uninstall the Console (if possible). A Console that installs a replacement for
     * <code>System.in</code> should put back the original value.
     *
     * @throws UnsupportedOperationException if the Console cannot be uninstalled
     */
    public void uninstall() throws UnsupportedOperationException;

}
