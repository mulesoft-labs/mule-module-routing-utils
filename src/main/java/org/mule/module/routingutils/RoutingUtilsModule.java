/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.module.routingutils;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.NestedProcessor;
import org.mule.api.annotations.Category;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Literal;
import org.mule.routing.filters.ExpressionFilter;

import javax.inject.Inject;

/**
 * @author MuleSoft, Inc.
 *
 * Utilitary module to add @Processors that might become handy while using mule.
 *
 */
@Connector(name = "routing-utils", friendlyName = "Routing Utils")
@Category(name = "org.mule.tooling.category.flowControl", description = "Flow Control")
public class RoutingUtilsModule
{

    @Inject
    private MuleContext muleContext;

    /**
     * Allows boolean expressions to be executed on a message, and when evaluates to true the processor chain will be executed.
     * Note that when using this detour operation, you must be able to either specify a boolean or use one of the standard Mule filters.
     * These can be defined as follows -
     * <ul>
     * <li>RegEx - 'regex:<pattern>': #[regex:'error' [0-9]]</li>
     * <li>Wildcard - 'wildcard:<pattern>': #[wildcard: *foo*</li>
     * <li>PayloadType - 'payload-type:<fully qualified class name>': #[payload:javax.jms.TextMessage]</li>
     * <li>ExceptionType - 'exception-type:<fully qualified class name>':
     * #[exception-type:java.io.FileNotFoundException]</li>
     * <li>Header - 'header:<boolean when>': #[header:foo!=null]</li>
     * </ul>
     * Otherwise you can use eny when filter providing you can define a boolean when i.e. <code>
     * #[xpath:count(/Foo/Bar) == 0]
     * </code> Note that it if the when is not a boolean when this filter will return true if the
     * when returns a result
     *
     * @param when            a MEL that can evaluate a range of expressions. It supports some base when types such as header, payload (payload type), regex, and wildcard.
     * @param nullReturnsTrue if the evaluated MEL {@code when} is null, then the processor chain will be executed anyway. False by default.
     * @param event           the MuleEvent, injected by DevKit.
     * @param chain           chain of elements to execute if {@code when} evaluates to true, injected by DevKit.
     * @return the {@link MuleEvent} unmodified if the {@code when} evaluates to false, the same {@link MuleEvent}
     * but with it's message payload changed to the latest value of the chain if {@code when} evaluates to true.
     * @throws Exception if the {@link NestedProcessor} chain blows up while processing the message.
     */
    @Processor
    public MuleEvent detour(@Literal String when, @Default("false") boolean nullReturnsTrue, MuleEvent event, NestedProcessor chain) throws Exception
    {
        MuleMessage message = event.getMessage();
        if (parseWhen(event, when, nullReturnsTrue))
        {
            message.setPayload(chain.process(event));
        }
        return event;
    }

    private boolean parseWhen(MuleEvent event, String when, Boolean nullReturnsTrue)
    {
        ExpressionFilter expressionFilter = new ExpressionFilter(when);
        expressionFilter.setMuleContext(muleContext);
        expressionFilter.setNullReturnsTrue(nullReturnsTrue);
        return expressionFilter.accept(event.getMessage());
    }

    public MuleContext getMuleContext()
    {
        return muleContext;
    }

    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }
}