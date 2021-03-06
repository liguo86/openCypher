/*
 * Copyright (c) 2015-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencypher.tools;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OptionTest
{
    public interface Options
    {
        default String foo()
        {
            return "foo";
        }

        interface WithMandatory
        {
            long mandatory();
        }

        interface WithOperator
        {
            default String anOperator( String parameter )
            {
                return prefix() + parameter;
            }

            String prefix();
        }
    }

    @Test
    public void shouldInvokeDefaultsIfNoOverride() throws Exception
    {
        // given
        Options options = Option.options( Options.class );

        // then
        assertEquals( "foo", options.foo() );
    }

    @Test
    public void shouldOverride() throws Exception
    {
        // given
        Options options = Option.options( Options.class, foo -> "bar" );

        // then
        assertEquals( "bar", options.foo() );
    }

    @Test
    public void shouldRejectInvalidOverride() throws Exception
    {
        // when
        try
        {
            Option.options( Options.class, baz -> 11 );

            fail( "expected exception" );
        }
        // then
        catch ( IllegalArgumentException e )
        {
            assertEquals( "No such option: baz", e.getMessage() );
        }
    }

    @Test
    public void shouldRequireOverrideForMethodWithoutDefault() throws Exception
    {
        // given
        try
        {
            Option.options( Options.WithMandatory.class );

            fail( "expected exception" );
        }
        // then
        catch ( IllegalArgumentException e )
        {
            assertEquals( "Missing required option: mandatory", e.getMessage() );
        }
    }

    @Test
    public void shouldAllowOperators() throws Exception
    {
        // given
        Options.WithOperator operator = Option.options( Options.WithOperator.class, prefix -> "Hello " );

        // when
        String result = operator.anOperator( "World" );

        // then
        assertEquals( "Hello World", result );
    }
}
