/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.module.routingutils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleEvent;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

/**
 * @author MuleSoft, Inc.
 */
public class RoutingUtilsModuleTestCase extends FunctionalTestCase
{

    private static final String ORIGINAL_PAYLOAD_VALUE = "original payload";
    private static final String MODIFIED_PAYLOAD_VALUE = "modified payload";
    private static final String FLOW_VAR_WHEN = "when";
    private static final String FLOW_VAR_NULL_RETURNS_TRUE = "nullReturnsTrue";


    @Override
    protected String getConfigFile()
    {
        return "config.xml";
    }

    @Test
    public void testDetour_chainExecution_whenTrueString() throws Exception
    {
        for (String trueStringValue : new String[]{"true", "TRUE", "TrUe"})
        {
            runDetourFlowVarsAndExpect(trueStringValue, false, ORIGINAL_PAYLOAD_VALUE, MODIFIED_PAYLOAD_VALUE);
        }
    }

    @Test
    public void testDetour_notChainExecution_whenFalseString() throws Exception
    {
        for (String falseStringValue : new String[]{"false", "FALSE", "FAlsE"})
        {
            runDetourFlowVarsAndExpect(falseStringValue, false, ORIGINAL_PAYLOAD_VALUE, ORIGINAL_PAYLOAD_VALUE);
        }
    }

    @Test
    public void testDetour_chainExecution_whenTrueBoolean() throws Exception
    {
        runDetourFlowVarsAndExpect(true, false, ORIGINAL_PAYLOAD_VALUE, MODIFIED_PAYLOAD_VALUE);
    }

    @Test
    public void testDetour_notChainExecution_whenFalseBoolean() throws Exception
    {
        runDetourFlowVarsAndExpect(false, false, ORIGINAL_PAYLOAD_VALUE, ORIGINAL_PAYLOAD_VALUE);
    }

    @Test
    public void testDetour_chainExecution_whenEvaluatingExpression() throws Exception
    {
        runDetourFlowVarsAndExpect("false == false", false, ORIGINAL_PAYLOAD_VALUE, MODIFIED_PAYLOAD_VALUE);
    }

    @Test
    public void testDetour_chainExecution_whenNull() throws Exception
    {
        runDetourFlowVarsAndExpect(null, true, ORIGINAL_PAYLOAD_VALUE, MODIFIED_PAYLOAD_VALUE);
    }

    @Test
    public void testDetour_notChainExecution_whenNull() throws Exception
    {
        runDetourFlowVarsAndExpect(null, false, ORIGINAL_PAYLOAD_VALUE, ORIGINAL_PAYLOAD_VALUE);
    }

    @Test
    public void testDetour_chainExecution_whenUsingProperty() throws Exception
    {
        MuleEvent initialEvent = getTestEvent(ORIGINAL_PAYLOAD_VALUE);
        initialEvent.getMessage().setOutboundProperty("foo", "bar");

        MuleEvent resultEvent = runFlow("detourOnProperty", initialEvent);
        assertThat(resultEvent.getMessage().getPayloadAsString(), is(MODIFIED_PAYLOAD_VALUE));
    }

    @Test
    public void testDetour_notChainExecution_whenUsingProperty() throws Exception
    {
        MuleEvent initialEvent = getTestEvent(ORIGINAL_PAYLOAD_VALUE);

        MuleEvent resultEvent = runFlow("detourOnProperty", initialEvent);
        assertThat(resultEvent.getMessage().getPayloadAsString(), is(ORIGINAL_PAYLOAD_VALUE));
    }


    private void runDetourFlowVarsAndExpect(Object expressionValue, boolean nullReturnsTrue, Object payload, String expected) throws Exception
    {
        runDetourFlowAndExpect("detourOnFlowVars", expressionValue, nullReturnsTrue, payload, expected);
    }

    private void runDetourFlowAndExpect(String flowName, Object expressionValue, boolean nullReturnsTrue, Object payload, String expected) throws Exception
    {
        MuleEvent initialEvent = getTestEvent(payload);
        initialEvent.setFlowVariable(FLOW_VAR_WHEN, expressionValue);
        initialEvent.setFlowVariable(FLOW_VAR_NULL_RETURNS_TRUE, nullReturnsTrue);

        initialEvent.getMessage().setOutboundProperty("foo", "bar");


        MuleEvent resultEvent = runFlow(flowName, initialEvent);
        assertThat(resultEvent.getMessage().getPayloadAsString(), is(expected));
    }
}
